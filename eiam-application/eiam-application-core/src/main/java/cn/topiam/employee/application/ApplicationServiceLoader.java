/*
 * eiam-application-core - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.application;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.context.annotation.Configuration;

import cn.topiam.employee.application.exception.AppTemplateNotExistException;

/**
 * 应用服务加载器
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 19:08
 */
@Configuration
public class ApplicationServiceLoader implements ApplicationContextAware {

    private final Logger                          logger                = LoggerFactory
        .getLogger(ApplicationServiceLoader.class);
    /**
     * 用于保存接口实现类名及对应的类
     */
    private Map<String, ApplicationService>       loadMap               = new HashMap<>(16);
    private ApplicationContext                    applicationContext;
    /**
     * key: code，value：templateImpl
     */
    private final Map<String, ApplicationService> applicationServiceMap = new HashMap<>(16);

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        loadMap = applicationContext.getBeansOfType(ApplicationService.class);
    }

    /**
     * 获取应用列表
     *
     * @return {@link List}
     */
    public Set<ApplicationService> getApplicationServiceList() {
        List<ApplicationService> values = loadMap.values().stream().toList();
        return new HashSet<>(values);
    }

    /**
     * 根据CODE获取应用
     *
     * @param code {@link String}
     * @return {@link List}
     */
    public ApplicationService getApplicationService(String code) {
        ApplicationService impl = applicationServiceMap.get(code);
        if (Objects.isNull(impl)) {
            for (ApplicationService service : getApplicationServiceList()) {
                if (code.equals(service.getCode())) {
                    applicationServiceMap.put(code, service);
                    return service;
                }
            }
        }
        if (Objects.isNull(impl)) {
            throw new AppTemplateNotExistException();
        }
        return impl;
    }

    public void addApplicationService(List<String> beanNameList) {
        Map<String, ApplicationService> applicationServiceMap = new HashMap<>(16);
        for (String beanName : beanNameList) {
            applicationServiceMap.put(beanName,
                applicationContext.getBean(beanName, ApplicationService.class));
        }
        loadMap.putAll(applicationServiceMap);
    }
}
