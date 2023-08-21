/*
 * eiam-application-form - Employee Identity and Access Management
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
package cn.topiam.employee.application.form;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.topiam.employee.application.exception.AppNotExistException;
import cn.topiam.employee.application.form.converter.AppFormConfigConverter;
import cn.topiam.employee.application.form.pojo.AppFormSaveConfigParam;
import cn.topiam.employee.audit.context.AuditContext;
import cn.topiam.employee.common.entity.app.AppEntity;
import cn.topiam.employee.common.entity.app.AppFormConfigEntity;
import cn.topiam.employee.common.entity.app.po.AppFormConfigPO;
import cn.topiam.employee.common.enums.app.*;
import cn.topiam.employee.common.repository.app.AppAccountRepository;
import cn.topiam.employee.common.repository.app.AppFormConfigRepository;
import cn.topiam.employee.common.repository.app.AppRepository;
import cn.topiam.employee.support.exception.TopIamException;
import cn.topiam.employee.support.validation.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolationException;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Form 用户应用
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/20 23:20
 */
@Slf4j
@Component
public class FormStandardApplicationServiceImpl extends AbstractFormApplicationService {

    /**
     * 更新应用配置
     *
     * @param appId {@link String}
     * @param config {@link Map}
     */
    @Override
    public void saveConfig(String appId, Map<String, Object> config) {
        AppFormSaveConfigParam model;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String value = mapper.writeValueAsString(config);
            // 指定序列化输入的类型
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
            model = mapper.readValue(value, AppFormSaveConfigParam.class);
        } catch (Exception e) {
            throw new TopIamException(e.getMessage());
        }
        //@formatter:off
        ValidationUtils.ValidationResult<AppFormSaveConfigParam> validationResult = ValidationUtils.validateEntity(model);
        if (validationResult.isHasErrors()) {
            throw new ConstraintViolationException(validationResult.getConstraintViolations());
        }
        //@formatter:on
        //1、修改基本信息
        Optional<AppEntity> optional = appRepository.findById(Long.valueOf(appId));
        if (optional.isEmpty()) {
            AuditContext.setContent("保存配置失败，应用 [" + appId + "] 不存在！");
            log.error(AuditContext.getContent());
            throw new AppNotExistException();
        }
        AppEntity appEntity = optional.get();
        appEntity.setAuthorizationType(model.getAuthorizationType());
        appRepository.save(appEntity);
        //2、修改 表单代填 配置
        Optional<AppFormConfigEntity> form = appFormConfigRepository
            .findByAppId(Long.valueOf(appId));
        if (form.isEmpty()) {
            AuditContext.setContent("保存配置失败，应用 [" + appId + "] 不存在！");
            log.error(AuditContext.getContent());
            throw new AppNotExistException();
        }
        AppFormConfigEntity entity = form.get();
        AppFormConfigEntity formConfig = appFormConfigConverter
            .appFormSaveConfigParamToEntity(model);
        formConfig.setId(entity.getId());
        formConfig.setAppId(entity.getAppId());
        formConfig.setRemark(entity.getRemark());
        formConfig.setCreateBy(entity.getCreateBy());
        formConfig.setCreateTime(entity.getCreateTime());
        formConfig.setDeleted(entity.getDeleted());
        appFormConfigRepository.save(formConfig);
    }

    /**
     * 获取配置
     *
     * @param appId {@link String}
     * @return {@link Map}
     */
    @Override
    public Object getConfig(String appId) {
        AppFormConfigPO po = appFormConfigRepository.getByAppId(Long.valueOf(appId));
        return appFormConfigConverter.entityConverterToFormConfigResult(po);
    }

    /**
     * 获取应用标志
     *
     * @return {@link String}
     */
    @Override
    public String getCode() {
        return AppProtocol.FORM.getCode();
    }

    /**
     * 获取应用名称
     *
     * @return {@link String}
     */
    @Override
    public String getName() {
        return AppProtocol.FORM.getDesc();
    }

    /**
     * 获取应用描述
     *
     * @return {@link String}
     */
    @Override
    public String getDescription() {
        return "表单代填可以模拟用户在登录页输入用户名和密码，再通过表单提交的一种登录方式。应用的账号密码在 TopIAM 中使用 AES256 加密算法本地加密存储。很多旧系统、不支持标准认证协议的系统或不支持改造的系统可以使用表单代填实现统一身份管理。表单中有图片验证码、CSRF token、动态参数的场景不适用。";
    }

    /**
     * 获取应用类型
     *
     * @return {@link AppType}
     */
    @Override
    public AppType getType() {
        return AppType.STANDARD;
    }

    /**
     * 获取应用协议
     *
     * @return {@link AppProtocol}
     */
    @Override
    public AppProtocol getProtocol() {
        return AppProtocol.FORM;
    }

    /**
     * 获取表单Schema
     *
     * @return {@link Map}
     */
    @Override
    public List<Map> getFormSchema() {
        return null;
    }

    /**
     * 获取base64图标
     *
     * @return {@link String}
     */
    @Override
    public String getBase64Icon() {
        return "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/PjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+PHN2ZyB0PSIxNjcyNDA3MTc3NTExIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjQ5MjkiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCI+PHBhdGggZD0iTTQ2Ny4yNyA4NzAuMzNIMTgzLjczYy0yMC41OCAwLTM3LjMyLTE1LjE2LTM3LjMyLTMzLjhWMTg1Ljg2YzAtMTguNjMgMTYuNzQtMzMuNzkgMzcuMzItMzMuNzlINzE4YzIwLjU3IDAgMzcuMzEgMTUuMTYgMzcuMzEgMzMuNzl2MjYyLjU5YTI0IDI0IDAgMSAwIDQ4IDBWMTg1Ljg2YzAtNDUuMS0zOC4yNy04MS43OS04NS4zMS04MS43OUgxODMuNzNjLTQ3IDAtODUuMzIgMzYuNjktODUuMzIgODEuNzl2NjUwLjY3YzAgNDUuMSAzOC4yNyA4MS44IDg1LjMyIDgxLjhoMjgzLjU0YTI0IDI0IDAgMCAwIDAtNDh6IiBmaWxsPSIjNjc3Nzg3IiBwLWlkPSI0OTMwIj48L3BhdGg+PHBhdGggZD0iTTkyMi4zOSA2NzQuNThsLTAuMzQtMi4xOGE0Ni4zOCA0Ni4zOCAwIDAgMC0zOS41OC0zOC42bC0yLjE5LTAuMjlhMzcuMzcgMzcuMzcgMCAwIDEtMzIuNDEtMzdBMzYuNDQgMzYuNDQgMCAwIDEgODUwIDU4NS42bDAuNzYtMi4zMmE0Ni4xOSA0Ni4xOSAwIDAgMC0yMS40Ni01NC42N2wtNDEuNDMtMjNhNDYuMDggNDYuMDggMCAwIDAtNTMuNDQgNi4yNWwtMS41OCAxLjQzYTExMi4zNSAxMTIuMzUgMCAwIDEtMTAuNiA4LjcxYy04LjgxIDYuMTYtMTUuNDIgOC4zNC0xOC40IDguMzRzLTkuNzItMi4yMi0xOC40OC04LjQ1YTEwMS42MSAxMDEuNjEgMCAwIDEtMTAuMjYtOC40NGwtMS41Ni0xLjQ4YTQ2IDQ2IDAgMCAwLTU0LTdsLTQzLjE4IDIzLjY3YTQ2LjEgNDYuMSAwIDAgMC0yMS41OSA1NWwwLjc4IDIuMzRhMzUuNyAzNS43IDAgMCAxLTMwLjMzIDQ3LjYxbC0yLjE4IDAuMjlhNDYuMzggNDYuMzggMCAwIDAtMzkuNTcgMzguNTRsLTAuMzQgMi4xOGMtMS40OCA5LjM0LTMuMjMgMjMuMDUtMy4yMyAzNS4zMyAwIDEyLjA5IDEuNzUgMjUuODEgMy4yMiAzNS4ybDAuMzQgMi4xOGE0Ni4zOCA0Ni4zOCAwIDAgMCAzOS42MiAzOC41OWwyLjE5IDAuMjlhMzUuNzUgMzUuNzUgMCAwIDEgMzAuMjcgNDcuNjhsLTAuNzcgMi4zMUE0Ni4xNiA0Ni4xNiAwIDAgMCA1NzYuMDYgODkxbDQwLjI2IDIyLjUxYTQ1Ljg3IDQ1Ljg3IDAgMCAwIDU0LjQ1LTdsMS41OS0xLjUyYTEwMS4zNiAxMDEuMzYgMCAwIDEgMTAuNTEtOC44M2M5LTYuNiAxNS43NS04Ljk0IDE4LjgxLTguOTQgMS41NyAwIDcuNTYgMC42NiAxOS4wNyA5LjE4YTk3LjU3IDk3LjU3IDAgMCAxIDEwLjMzIDguODFsMS41NyAxLjU0YTQ2LjM4IDQ2LjM4IDAgMCAwIDU0LjU5IDcuNDlsNDItMjMuMTNhNDYuMTYgNDYuMTYgMCAwIDAgMjEuNTctNTQuNzNMODUwIDgzNGEzNi43OSAzNi43OSAwIDAgMS0yLjE0LTEwLjkzIDM3LjMyIDM3LjMyIDAgMCAxIDMyLjM2LTM3bDIuMi0wLjI5QTQ2LjM3IDQ2LjM3IDAgMCAwIDkyMiA3NDcuMjRsMC4zNS0yLjE3YzEuMTktNy42MiAzLjItMjIuMzMgMy4yLTM1LjIyIDAuMDQtMTIuMjUtMS43LTI1Ljk0LTMuMTYtMzUuMjd6IG0tNDcuNDIgNjNsLTAuMTMgMC43OS0wLjc5IDAuMWE4NS40NSA4NS40NSAwIDAgMC03NC4xOCA4NC41OCA4My43OSA4My43OSAwIDAgMCA0LjUyIDI1Ljg1bDAuMjggMC44NS0zOS4zOCAyMS43LTAuNTctMC41NmExNDcuNiAxNDcuNiAwIDAgMC0xNS40NC0xMy4xOGMtMTYuNjUtMTIuMzItMzIuNjctMTguNTctNDcuNi0xOC41N3MtMzAuNzEgNi4xMy00Ny4xNyAxOC4yMWExNTMuNiAxNTMuNiAwIDAgMC0xNS40MSAxMi45NGwtMC41NyAwLjU2LTM3LjY5LTIxLjA3IDAuMjgtMC44NGE4NC4xNSA4NC4xNSAwIDAgMCA0LjU1LTI1LjkgODUuNDIgODUuNDIgMCAwIDAtNzQuMTYtODQuNTdsLTAuOC0wLjExLTAuMTItMC43OWExOTMuMzMgMTkzLjMzIDAgMCAxLTIuNTktMjcuNzIgMTkxLjkgMTkxLjkgMCAwIDEgMi41OS0yNy44NWwwLjEzLTAuNzkgMC43OS0wLjExYTg1LjUgODUuNSAwIDAgMCA3NC4xNi04NC41NyA4My40NSA4My40NSAwIDAgMC00LjUyLTI1Ljc3bC0wLjI5LTAuODYgNDAuNi0yMi4yNSAwLjU3IDAuNTRhMTQ2LjE5IDE0Ni4xOSAwIDAgMCAxNS40NSAxMi43MmMxNi4yMiAxMS41NCAzMS44MiAxNy4zOSA0Ni4zNyAxNy4zOSAxNC4zNCAwIDI5LjgyLTUuNzQgNDYtMTcuMDdsMC4xMy0wLjA5YTE1Ny40MSAxNTcuNDEgMCAwIDAgMTUuMjMtMTIuMzZsMC41OC0wLjUyIDM4Ljg5IDIxLjU3LTAuMjggMC44NGE4My45IDgzLjkgMCAwIDAtNC41MiAyNS44NiA4NS41MSA4NS41MSAwIDAgMCA3NC4xOCA4NC41OGwwLjc5IDAuMSAwLjEzIDAuNzlhMTk2LjUyIDE5Ni41MiAwIDAgMSAyLjYyIDI3Ljg2IDE5My43MSAxOTMuNzEgMCAwIDEtMi42IDI3Ljc2eiIgZmlsbD0iIzY3Nzc4NyIgcC1pZD0iNDkzMSI+PC9wYXRoPjxwYXRoIGQ9Ik03MDIuNzcgNjMzLjQ1YTc2LjEzIDc2LjEzIDAgMSAwIDc2LjEzIDc2LjEzIDc2LjIxIDc2LjIxIDAgMCAwLTc2LjEzLTc2LjEzeiBtMCAxMDQuMjZhMjguMTMgMjguMTMgMCAxIDEgMjguMTMtMjguMTMgMjguMTYgMjguMTYgMCAwIDEtMjguMTMgMjguMTN6TTU1OS4yMyA0ODguNDVhMjQgMjQgMCAwIDAtMjQtMjRIMjM3Ljc0YTI0IDI0IDAgMSAwIDAgNDhoMjk3LjQ5YTI0IDI0IDAgMCAwIDI0LTI0ek02ODIuMzMgMzA3LjIxYTI0IDI0IDAgMCAwLTI0LTI0SDIzNy43NGEyNCAyNCAwIDAgMCAwIDQ4aDQyMC41OWEyNCAyNCAwIDAgMCAyNC0yNHpNMjQzLjIxIDYzOC45NGEyNCAyNCAwIDAgMCAwIDQ4aDE4My45NGEyNCAyNCAwIDEgMCAwLTQ4eiIgZmlsbD0iIzY3Nzc4NyIgcC1pZD0iNDkzMiI+PC9wYXRoPjwvc3ZnPg==";
    }

    /**
     * 创建应用
     *
     * @param name   {@link String} 名称
     * @param icon   {@link String} 图标
     * @param remark {@link String} 备注
     */
    @Override
    public String create(String name, String icon, String remark) {
        //1、创建应用
        AppEntity appEntity = createApp(name, icon, remark, InitLoginType.PORTAL_OR_APP,
            AuthorizationType.AUTHORIZATION);
        AppFormConfigEntity appFormConfig = new AppFormConfigEntity();
        appFormConfig.setAppId(appEntity.getId());
        //提交类型
        appFormConfig.setSubmitType(FormSubmitType.POST);
        appFormConfigRepository.save(appFormConfig);
        return String.valueOf(appEntity.getId());
    }

    private final AppFormConfigConverter appFormConfigConverter;

    protected FormStandardApplicationServiceImpl(AppAccountRepository appAccountRepository,
                                                 AppFormConfigRepository appFormConfigRepository,
                                                 AppRepository appRepository,
                                                 AppFormConfigConverter appFormConfigConverter) {
        super(appRepository, appAccountRepository, appFormConfigRepository);
        this.appFormConfigConverter = appFormConfigConverter;
    }

}
