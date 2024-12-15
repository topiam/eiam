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
package cn.topiam.employee.console.initializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.StringUtils;

import cn.topiam.employee.common.entity.account.OrganizationMemberEntity;
import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.account.OrganizationMemberRepository;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.support.config.AbstractSystemInitializer;
import cn.topiam.employee.support.config.InitializationException;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_ADMIN_USERNAME;
import static cn.topiam.employee.support.constant.EiamConstants.ROOT_NODE;

/**
 * DefaultAdministratorInitialize
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/11/26 21:44
 */
@Component
public class DefaultAdministratorInitializer extends AbstractSystemInitializer {

    private final Logger        logger                      = LoggerFactory
        .getLogger(DefaultAdministratorInitializer.class);
    private static final String DIR_NAME                    = ".topiam";
    private static final String USER_HOME                   = "user.home";
    private static final String INITIAL_PASSWORD_VALUE_NAME = "initial.password.value";
    // generate: 自动生成，setting: 读取[INITIAL_PASSWORD_VALUE]设置的固定值，没有设置使用[INITIAL_PASSWORD_DEFAULT]的默认值
    private static final String INITIAL_PASSWORD_TYPE_NAME  = "initial.password.type";
    private static final String INITIAL_PASSWORD_DEFAULT    = "topiam.cn";

    @Override
    public void init() throws InitializationException {
        try {
            Optional<AdministratorEntity> optional = administratorRepository
                .findByUsername(DEFAULT_ADMIN_USERNAME);
            if (optional.isEmpty()) {
                String initPassword;
                String initPasswordFileTips;
                String passwordType = System.getProperty(INITIAL_PASSWORD_TYPE_NAME);
                if (StringUtils.hasText(passwordType) && "generate".equals(passwordType)) {
                    initPassword = idGenerator.generateId().toString().replace("-", "")
                        .toLowerCase(Locale.ENGLISH);
                } else {
                    String passwordInitial = System.getProperty(INITIAL_PASSWORD_VALUE_NAME);
                    if (StringUtils.hasText(passwordInitial)) {
                        initPassword = passwordInitial;
                    } else {
                        initPassword = INITIAL_PASSWORD_DEFAULT;
                    }
                }
                String initialAdminPasswordFilePath = getInitialAdminPasswordFilePath();
                FileUtils.writeStringToFile(new File(initialAdminPasswordFilePath), initPassword,
                    "UTF-8");
                BufferedWriter stream = new BufferedWriter(
                    new FileWriter(initialAdminPasswordFilePath));
                initPasswordFileTips = "This may also be found at: " + initialAdminPasswordFilePath;
                //清空
                stream.write(initPassword);
                stream.flush();
                stream.close();
                logger.info(
                    """

                            *************************************************************
                            *************************************************************
                            *************************************************************

                            TOPIAM console initial setup is required. An admin administrator has been created and a initialize password.
                            Please use the following password to proceed to installation:

                            %s

                            %s\s
                            *************************************************************
                            *************************************************************
                            *************************************************************

                            """
                        .formatted(initPassword, initPasswordFileTips));
                //保存管理员
                AdministratorEntity administrator = new AdministratorEntity();
                administrator.setUsername(DEFAULT_ADMIN_USERNAME);
                administrator.setFullName("管理员");
                administrator.setPassword(passwordEncoder.encode(initPassword));
                administrator.setStatus(UserStatus.ENABLED);
                administrator.setNeedChangePassword(true);
                administrator.setRemark(
                    "This administrator is automatically created during system initialization.");
                administratorRepository.save(administrator);
                OrganizationMemberEntity member = new OrganizationMemberEntity();
                member.setOrgId(ROOT_NODE);
                member.setUserId(administrator.getId());
                organizationMemberRepository.save(member);
            }
        } catch (Exception e) {
            throw new InitializationException(e);
        }
    }

    public static String addSeparator(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }
        return dir;
    }

    /**
     * 获取初始化管理员密码文件路径
     *
     * @return {@link String}
     */
    public static String getInitialAdminPasswordFilePath() {
        String path = addSeparator(System.getProperty(USER_HOME)) + DIR_NAME + File.separator
                      + "secrets" + File.separator;
        return path + "initialAdminPassword";
    }

    @Override
    public int getOrder() {
        return 3;
    }

    private final AlternativeJdkIdGenerator    idGenerator = new AlternativeJdkIdGenerator();

    private final OrganizationMemberRepository organizationMemberRepository;

    private final AdministratorRepository      administratorRepository;

    private final PasswordEncoder              passwordEncoder;

    public DefaultAdministratorInitializer(OrganizationMemberRepository organizationMemberRepository,
                                           AdministratorRepository administratorRepository,
                                           PasswordEncoder passwordEncoder) {
        this.organizationMemberRepository = organizationMemberRepository;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
