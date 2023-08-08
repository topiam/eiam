/*
 * eiam-console - Employee Identity and Access Management
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
package cn.topiam.employee.console.service.setting.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.audit.entity.Target;
import cn.topiam.employee.audit.enums.TargetType;
import cn.topiam.employee.common.entity.setting.MailTemplateEntity;
import cn.topiam.employee.common.enums.MailType;
import cn.topiam.employee.common.repository.setting.MailTemplateRepository;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.converter.setting.MailTemplateConverter;
import cn.topiam.employee.console.pojo.result.setting.EmailTemplateListResult;
import cn.topiam.employee.console.pojo.result.setting.EmailTemplateResult;
import cn.topiam.employee.console.pojo.save.setting.EmailCustomTemplateSaveParam;
import cn.topiam.employee.console.service.setting.MailTemplateService;
import cn.topiam.employee.support.util.BeanUtils;
import static cn.topiam.employee.core.setting.constant.MessageSettingConstants.SETTING_EMAIL_TEMPLATE_CACHE_NAME;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_BY;
import static cn.topiam.employee.support.repository.domain.BaseEntity.LAST_MODIFIED_TIME;

/**
 * <p>
 * 邮件模板 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-13
 */
@Service
@CacheConfig(cacheNames = SETTING_EMAIL_TEMPLATE_CACHE_NAME)
public class MailTemplateServiceImpl extends SettingServiceImpl implements MailTemplateService {

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(MailTemplateServiceImpl.class);

    /**
     * 根据邮件模板类型获取配置
     *
     * @param type {@link MailType}
     * @return SettingMailTemplateEntity
     */
    @Override
    @Cacheable(key = "#result.type.code", unless = "#result == null")
    public MailTemplateEntity getEmailTemplateByType(MailType type) {
        return mailTemplateRepository.findByType(type);
    }

    /**
     * 添加邮件模板
     *
     * @param type {@link MailType}
     * @return SettingMailTemplateEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#result.type.code", unless = "#result == null")
    public MailTemplateEntity saveCustomEmailTemplate(MailType type,
                                                      EmailCustomTemplateSaveParam param) {
        //入参映射为实体
        MailTemplateEntity entity = messageDataConverter
            .emailTemplateConfigSaveParamConvertToEntity(param);
        entity.setType(type);
        //查询数据库
        MailTemplateEntity template = getEmailTemplateByType(type);
        if (!Objects.isNull(template)) {
            BeanUtils.merge(entity, template, LAST_MODIFIED_BY, LAST_MODIFIED_TIME);
            return mailTemplateRepository.save(template);
        }
        entity = mailTemplateRepository.save(entity);
        AuditContext.setTarget(
            Target.builder().id(entity.getId().toString()).type(TargetType.MAIL_TEMPLATE).build());
        return entity;
    }

    /**
     * 邮件模板详情
     *
     * @param templateType {@link MailType}
     * @return {@link EmailTemplateResult}
     */
    @Override
    public EmailTemplateResult getEmailTemplate(MailType templateType) {
        //构建查询条件
        //查询
        MailTemplateEntity template = getEmailTemplateByType(templateType);
        //如果数据库为空，查找默认模板
        EmailTemplateResult result;
        if (Objects.isNull(template)) {
            result = messageDataConverter
                .mailTemplateTypeConvertToEmailTemplateDetailResult(templateType);
        }
        //查询数据库
        else {
            result = messageDataConverter.entityConvertToEmailTemplateDetailResult(template);
        }
        return result;
    }

    /**
     * 获取邮件模板列表
     * 系统自带邮件模板及内容存入枚举，
     * 如果用户进行了自定义模板内容，将会把自定义的存入数据库，使用用户自定义的
     *
     * @return {@link List}
     */
    @Override
    public List<EmailTemplateListResult> getEmailTemplateList() {
        //从枚举类获取信息
        List<MailType> values = Arrays.asList(MailType.values());
        //从数据库获取
        List<MailTemplateEntity> lists = mailTemplateRepository.findAll();
        return messageDataConverter.mailTemplateTypeConvertToEmailTemplateListResult(values, lists);
    }

    /**
     * 禁用自定义模块
     *
     * @param type {@link MailType}
     */
    @Override
    @CacheEvict(key = "#type.code")
    public void disableCustomEmailTemplate(MailType type) {
        mailTemplateRepository.deleteByType(type);
    }

    /**
     * 消息服务数据映射
     */
    private final MailTemplateConverter  messageDataConverter;
    /**
     * SettingMailTemplateRepository
     */
    private final MailTemplateRepository mailTemplateRepository;

    public MailTemplateServiceImpl(SettingRepository settingsRepository,
                                   MailTemplateConverter messageDataConverter,
                                   MailTemplateRepository mailTemplateRepository) {
        super(settingsRepository);
        this.messageDataConverter = messageDataConverter;
        this.mailTemplateRepository = mailTemplateRepository;
    }
}
