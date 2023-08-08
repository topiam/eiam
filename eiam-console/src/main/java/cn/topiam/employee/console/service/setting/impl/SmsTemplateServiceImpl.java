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

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.topiam.employee.common.enums.Language;
import cn.topiam.employee.common.enums.SmsType;
import cn.topiam.employee.common.repository.setting.SettingRepository;
import cn.topiam.employee.console.pojo.result.setting.SmsTemplateListResult;
import cn.topiam.employee.console.service.setting.SmsTemplateService;

/**
 * <p>
 * 短信模版模板 服务实现类
 * </p>
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2020-08-13
 */
@Service
public class SmsTemplateServiceImpl extends SettingServiceImpl implements SmsTemplateService {

    /**
     * 获取短信模版列表
     *
     * @param language {@link Language}
     * @return {@link SmsTemplateListResult}
     */
    @Override
    public List<SmsTemplateListResult> getSmsTemplateList(Language language) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("sms/template/sms-template",
            new Locale(language.getLocale()));
        List<SmsTemplateListResult> results = Lists.newArrayList();
        SmsType[] values = SmsType.values();

        for (SmsType type : values) {
            SmsTemplateListResult result = new SmsTemplateListResult();
            String content = resourceBundle.getString(type.getCode());
            result.setContent(content);
            result.setType(type);
            result.setName(type.getDesc());
            result.setLanguage(Language.getType(resourceBundle.getLocale().getLanguage()));
            results.add(result);
        }
        return results;
    }

    public SmsTemplateServiceImpl(SettingRepository settingsRepository) {
        super(settingsRepository);
    }

}
