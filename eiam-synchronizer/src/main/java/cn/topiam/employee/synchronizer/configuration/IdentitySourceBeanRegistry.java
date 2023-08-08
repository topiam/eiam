/*
 * eiam-synchronizer - Employee Identity and Access Management
 * Copyright © 2022-Present Jinan Yuanchuang Network Technology Co., Ltd. (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.synchronizer.configuration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.JdkIdGenerator;
import org.springframework.util.StopWatch;

import com.alibaba.fastjson2.JSON;
import com.cronutils.model.CronType;

import cn.topiam.employee.common.constant.SettingConstants;
import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.enums.TriggerType;
import cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.identitysource.core.IdentitySource;
import cn.topiam.employee.identitysource.core.IdentitySourceConfig;
import cn.topiam.employee.identitysource.core.client.IdentitySourceClient;
import cn.topiam.employee.identitysource.core.event.IdentitySourceEventListener;
import cn.topiam.employee.identitysource.core.exception.IdentitySourceNotExistException;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceEventPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncDeptPostProcessor;
import cn.topiam.employee.identitysource.core.processor.IdentitySourceSyncUserPostProcessor;
import cn.topiam.employee.identitysource.dingtalk.DingTalkConfig;
import cn.topiam.employee.identitysource.dingtalk.DingTalkIdentitySource;
import cn.topiam.employee.identitysource.dingtalk.client.DingTalkClient;
import cn.topiam.employee.identitysource.feishu.FeiShuConfig;
import cn.topiam.employee.identitysource.feishu.FieShuIdentitySource;
import cn.topiam.employee.identitysource.feishu.client.FeiShuClient;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkConfig;
import cn.topiam.employee.identitysource.wechatwork.WeChatWorkIdentitySource;
import cn.topiam.employee.identitysource.wechatwork.client.WeChatWorkClient;
import cn.topiam.employee.support.scheduler.SpringSchedulerRegistrar;
import cn.topiam.employee.support.trace.TraceUtils;
import cn.topiam.employee.synchronizer.task.IdentitySourceSyncTask;

import lombok.extern.slf4j.Slf4j;
import static cn.topiam.employee.common.enums.identitysource.IdentitySourceProvider.DINGTALK;
import static cn.topiam.employee.support.lock.LockAspect.getTopiamLockKeyPrefix;
import static cn.topiam.employee.synchronizer.configuration.IdentitySourceBeanUtils.getSourceBeanName;

/**
 * 身份源Bean 注册
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/3/17 21:54
 */
@Slf4j
@Configuration
@AutoConfigureAfter({ WebMvcAutoConfiguration.EnableWebMvcConfiguration.class })
public class IdentitySourceBeanRegistry implements IdentitySourceEventListener {
    private final ApplicationContext applicationContext;

    /**
     * 构造
     *
     * @param applicationContext {@link  ApplicationContext}
     */
    public IdentitySourceBeanRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
            .getBeanFactory();
        IdentitySourceRepository repository = beanFactory.getBean(IdentitySourceRepository.class);

        Iterable<IdentitySourceEntity> list = repository.findByEnabledIsTrue();
        list.forEach(identity -> {
            if (identity.getBasicConfig().isBlank()
                || SettingConstants.NOT_CONFIG.equals(identity.getBasicConfig())
                || !identity.getEnabled()) {
                return;
            }
            //注册
            registerIdentitySourceBean(identity, applicationContext);
            registerIdentitySourceSyncTask(identity, applicationContext);
        });
    }

    /**
     * 注册身份源 Bean
     *
     * @param entity                 {@link IdentitySourceEntity} 身份源
     * @param applicationContext {@link  ApplicationContext}
     */
    private static void registerIdentitySourceBean(IdentitySourceEntity entity,
                                                   ApplicationContext applicationContext) {
        String id = entity.getId().toString();
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
            .getBeanFactory();
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) ((ConfigurableApplicationContext) applicationContext)
            .getBeanFactory();
        //如果已经存在，销毁
        try {
            if (ObjectUtils.isNotEmpty(beanFactory.getBean(getSourceBeanName(id)))) {
                destroyIdentitySourceBean(id, applicationContext);
            }
        } catch (NoSuchBeanDefinitionException ignored) {

        } finally {
            BeanDefinitionHolder definitionHolder = getBeanDefinitionHolder(entity,
                applicationContext);
            //注册 Bean
            BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder,
                beanDefinitionRegistry);
        }
    }

    /**
     * 获取 BeanDefinitionHolder
     *
     * @param entity {@link IdentitySourceEntity}
     * @param applicationContext {@link ApplicationContext}
     * @return {@link BeanDefinitionHolder}
     */
    private static BeanDefinitionHolder getBeanDefinitionHolder(@NonNull IdentitySourceEntity entity,
                                                                ApplicationContext applicationContext) {
        //@formatter:off
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        IdentitySourceSyncUserPostProcessor identitySourceSyncUserPostProcessor = beanFactory.getBean(IdentitySourceSyncUserPostProcessor.class);
        IdentitySourceSyncDeptPostProcessor identitySourceSyncDeptPostProcessor = beanFactory.getBean(IdentitySourceSyncDeptPostProcessor.class);
        IdentitySourceEventPostProcessor identitySourceEventPostProcessor = beanFactory.getBean(IdentitySourceEventPostProcessor.class);
        IdentitySourceClient identitySourceClient;
        BeanDefinitionBuilder definitionBuilder;
        // 钉钉
        if (DINGTALK.equals(entity.getProvider())) {
            DingTalkConfig config = JSON.parseObject(entity.getBasicConfig(), DingTalkConfig.class);
            identitySourceClient = new DingTalkClient(config);
            definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DingTalkIdentitySource.class);
            return getDefinitionHolder(entity, identitySourceSyncUserPostProcessor, identitySourceSyncDeptPostProcessor, identitySourceEventPostProcessor, identitySourceClient, definitionBuilder, config);
        }
        // 企业微信
        if (IdentitySourceProvider.WECHAT_WORK.equals(entity.getProvider())) {
            WeChatWorkConfig config = JSON.parseObject(entity.getBasicConfig(), WeChatWorkConfig.class);
            identitySourceClient = new WeChatWorkClient(config);
            definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(WeChatWorkIdentitySource.class);
            return getDefinitionHolder(entity, identitySourceSyncUserPostProcessor, identitySourceSyncDeptPostProcessor, identitySourceEventPostProcessor, identitySourceClient, definitionBuilder, config);
        }
        // 飞书
        if (IdentitySourceProvider.FEISHU.equals(entity.getProvider())) {
            FeiShuConfig config = JSON.parseObject(entity.getBasicConfig(), FeiShuConfig.class);
            identitySourceClient = new FeiShuClient(config);
            //配置Bean
            definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(FieShuIdentitySource.class);
            return getDefinitionHolder(entity, identitySourceSyncUserPostProcessor, identitySourceSyncDeptPostProcessor, identitySourceEventPostProcessor, identitySourceClient, definitionBuilder, config);
        }

        log.info("未实现 [{}] 类型身份源配置", entity.getProvider());
        throw new IdentitySourceNotExistException();
        //@formatter:on
    }

    @NotNull
    private static BeanDefinitionHolder getDefinitionHolder(IdentitySourceEntity entity,
                                                            IdentitySourceSyncUserPostProcessor identitySourceSyncUserPostProcessor,
                                                            IdentitySourceSyncDeptPostProcessor identitySourceSyncDeptPostProcessor,
                                                            IdentitySourceEventPostProcessor identitySourceEventPostProcessor,
                                                            IdentitySourceClient identitySourceClient,
                                                            BeanDefinitionBuilder definitionBuilder,
                                                            IdentitySourceConfig config) {
        definitionBuilder.addConstructorArgValue(entity.getId().toString());
        definitionBuilder.addConstructorArgValue(entity.getName());
        definitionBuilder.addConstructorArgValue(config);
        definitionBuilder.addConstructorArgValue(identitySourceClient);
        definitionBuilder.addConstructorArgValue(identitySourceSyncUserPostProcessor);
        definitionBuilder.addConstructorArgValue(identitySourceSyncDeptPostProcessor);
        definitionBuilder.addConstructorArgValue(identitySourceEventPostProcessor);
        //设置为 RefreshScope
        definitionBuilder.setScope("refresh");
        return new BeanDefinitionHolder(definitionBuilder.getBeanDefinition(),
            getSourceBeanName(entity.getId().toString()));
    }

    /**
     * 销毁身份源 Bean
     *
     * @param id                 {@link String} 身份源ID
     * @param applicationContext {@link ApplicationContext}
     */
    private static void destroyIdentitySourceBean(String id,
                                                  ApplicationContext applicationContext) {
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) ((ConfigurableApplicationContext) applicationContext)
            .getBeanFactory();
        String beanName = getSourceBeanName(id);
        try {
            beanDefinitionRegistry.removeBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
    }

    /**
     * 注册身份源定时任务 Task
     *
     * @param entity             {@link String} 身份源
     * @param applicationContext {@link ApplicationContext}
     */
    public static void registerIdentitySourceSyncTask(IdentitySourceEntity entity,
                                                      ApplicationContext applicationContext) {
        String id = entity.getId().toString();
        String beanName = getSourceBeanName(id);
        IdentitySource<? extends IdentitySourceConfig> identitySource = (IdentitySource<? extends IdentitySourceConfig>) applicationContext
            .getBean(beanName);
        SpringSchedulerRegistrar schedulerRegistrarHelp = applicationContext
            .getBean(SpringSchedulerRegistrar.class);
        RedissonClient redissonClient = applicationContext.getBean(RedissonClient.class);
        //注册定时任务
        String cronExpression = entity.getJobConfig().getCronExpression(CronType.SPRING);
        destroyIdentitySourceSyncTask(entity.getId().toString(), applicationContext);
        schedulerRegistrarHelp.addCronTask(beanName,
            new IdentitySourceSyncTask(entity.getId().toString(), entity.getName(), redissonClient,
                identitySource),
            cronExpression);
    }

    /**
     * 销毁定时任务 Bean
     *
     * @param id                 {@link String} 身份源ID
     * @param applicationContext {@link ApplicationContext}
     */
    public static void destroyIdentitySourceSyncTask(String id,
                                                     ApplicationContext applicationContext) {
        SpringSchedulerRegistrar schedulerRegistrarHelp = applicationContext
            .getBean(SpringSchedulerRegistrar.class);
        schedulerRegistrarHelp.removeCronTask(id);
    }

    /**
     * 注册
     *
     * @param id {@link String}
     */
    @Override
    public void register(String id) {
        IdentitySourceRepository repository = applicationContext
            .getBean(IdentitySourceRepository.class);
        Optional<IdentitySourceEntity> optional = repository.findById(Long.valueOf(id));
        if (optional.isPresent()) {
            IdentitySourceEntity entity = optional.get();
            if (entity.getBasicConfig().isBlank()
                || SettingConstants.NOT_CONFIG.equals(entity.getBasicConfig())
                || !entity.getEnabled()) {
                return;
            }
            //注册
            log.info("注册身份源: {} 对象", id);
            registerIdentitySourceBean(entity, applicationContext);
            log.info("注册身份源: {} 同步任务", id);
            registerIdentitySourceSyncTask(entity, applicationContext);
        }
    }

    /**
     * 销毁
     *
     * @param id {@link String}
     */
    @Override
    public void destroy(String id) {
        //销毁
        log.info("销毁身份源: {} 对象", id);
        destroyIdentitySourceBean(id, applicationContext);
        log.info("销毁身份源: {} 同步任务", id);
        destroyIdentitySourceSyncTask(id, applicationContext);
    }

    /**
     * 同步
     *
     * @param id {@link String}
     */
    @Override
    public void sync(String id) {
        IdentitySource<? extends IdentitySourceConfig> identitySource = (IdentitySource<? extends IdentitySourceConfig>) applicationContext
            .getBean(getSourceBeanName(id));
        RedissonClient redissonClient = applicationContext.getBean(RedissonClient.class);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("[手动任务]-同步身份源[{}]数据开始", identitySource.getName());
        TraceUtils.put(new JdkIdGenerator().generateId().toString());
        RLock lock = redissonClient.getLock(getTopiamLockKeyPrefix() + id);
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(1, TimeUnit.SECONDS);
            if (tryLock) {
                identitySource.sync(TriggerType.MANUAL);
            }
        } catch (InterruptedException ignored) {

        } finally {
            stopWatch.stop();
            if (tryLock && lock.isLocked()) {
                lock.unlock();
            }
            TraceUtils.remove();
            log.info("[手动任务]-同步身份源[{}]数据结束, 执行时长: {} ms", identitySource.getName(),
                stopWatch.getTotalTimeMillis());
        }
    }
}
