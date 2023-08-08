/*
 * eiam-core - Employee Identity and Access Management
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
package cn.topiam.employee.core.configuration;

import java.io.Serializable;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;

import liquibase.integration.spring.SpringLiquibase;

/**
 * liquibase配置
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2020/7/28 21:52
 */
@Configuration
public class EiamLiquibaseConfiguration implements Serializable {

    /**
     * TopIAM Liquibase
     *
     * @param dataSource {@link DataSource}
     * @return {@link SpringLiquibase}
     */
    @Bean
    public SpringLiquibase topIamLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath*:db/eiam-changelog-master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);
        liquibase.setResourceLoader(new DefaultResourceLoader());
        liquibase.setDatabaseChangeLogTable("eiam_changelog_table");
        liquibase.setDatabaseChangeLogLockTable("eiam_changelog_lock_table");
        return liquibase;
    }
}
