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
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.StringUtils;

import cn.topiam.employee.common.entity.setting.AdministratorEntity;
import cn.topiam.employee.common.enums.UserStatus;
import cn.topiam.employee.common.repository.setting.AdministratorRepository;
import cn.topiam.employee.support.init.Initializer;
import cn.topiam.employee.support.trace.TraceUtils;
import static cn.topiam.employee.support.constant.EiamConstants.DEFAULT_ADMIN_USERNAME;
import static cn.topiam.employee.support.constant.EiamConstants.TOPIAM_INIT_AUTHENTICATION;
import static cn.topiam.employee.support.lock.LockAspect.getTopiamLockKeyPrefix;
import static cn.topiam.employee.support.util.CreateFileUtil.createFile;

/**
 * DefaultAdministratorInitialize
 *
 * @author TopIAM
 * Created by support@topiam.cn on 2022/11/26 21:44
 */
@Order(2)
@Component
public class DefaultAdministratorInitializer implements Initializer {

    private final Logger        logger                      = LoggerFactory
        .getLogger(DefaultAdministratorInitializer.class);
    private static final String DIR_NAME                    = ".topiam";
    private static final String USER_HOME                   = "user.home";
    private static final String INITIAL_PASSWORD_VALUE_NAME = "initial.password.value";
    // generate: 自动生成，setting: 读取[INITIAL_PASSWORD_VALUE]设置的固定值，没有设置使用[INITIAL_PASSWORD_DEFAULT]的默认值
    private static final String INITIAL_PASSWORD_TYPE_NAME  = "initial.password.type";
    private static final String INITIAL_PASSWORD_DEFAULT    = "topiam.cn";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(ApplicationContext applicationContext) {
        //@formatter:off
        String traceId = idGenerator.generateId().toString();
        TraceUtils.put(traceId);
        RLock lock = redissonClient.getLock(getTopiamLockKeyPrefix());
        boolean tryLock = false;
        try {
            SecurityContextHolder.getContext().setAuthentication(TOPIAM_INIT_AUTHENTICATION);
            tryLock = lock.tryLock(1, TimeUnit.SECONDS);
             if (tryLock){
                 Optional<AdministratorEntity> optional = administratorRepository.findByUsername(DEFAULT_ADMIN_USERNAME);
                 if (optional.isEmpty()) {
                     String initPassword;
                     String initPasswordFileTips  ;
                     String passwordType = System.getProperty(INITIAL_PASSWORD_TYPE_NAME);
                     if (StringUtils.hasText(passwordType) && "generate".equals(passwordType)) {
                         initPassword = idGenerator.generateId().toString().replace("-", "").toLowerCase(Locale.ENGLISH);
                     }
                     else {
                         String passwordInitial = System.getProperty(INITIAL_PASSWORD_VALUE_NAME);
                         if (StringUtils.hasText(passwordInitial)) {
                             initPassword = passwordInitial;
                         }
                         else {
                             initPassword = INITIAL_PASSWORD_DEFAULT;
                         }
                     }
                     String initialAdminPasswordFilePath = getInitialAdminPasswordFilePath();createFile(initialAdminPasswordFilePath);
                     BufferedWriter stream = new BufferedWriter(new FileWriter(initialAdminPasswordFilePath));
                     initPasswordFileTips = "This may also be found at: " + initialAdminPasswordFilePath;
                     //清空
                     stream.write(initPassword);
                     stream.flush();
                     stream.close();
                     logger.info("""

                        *************************************************************
                        *************************************************************
                        *************************************************************

                        TOPIAM console initial setup is required. An admin user has been created and a initialize password.
                        Please use the following password to proceed to installation:

                        %s

                        %s\s
                        *************************************************************
                        *************************************************************
                        *************************************************************

                        """.formatted(initPassword, initPasswordFileTips));
                     //保存管理员
                     saveInitAdministrator(DEFAULT_ADMIN_USERNAME, initPassword);
                 }
             }

        } catch (Exception exception) {
            int exitCode = SpringApplication.exit(applicationContext,
                    () -> 0);
            System.exit(exitCode);
        } finally {
            if (tryLock && lock.isLocked()) {
                lock.unlock();
            }
            TraceUtils.remove();
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        }
        //@formatter:on
    }

    /**
     * 保存管理员
     *
     * @param username {@link String}
     * @param password {@link String}
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveInitAdministrator(String username, String password) {
        AdministratorEntity administrator = new AdministratorEntity();
        administrator.setUsername(username);
        administrator.setPassword(passwordEncoder.encode(password));
        administrator.setStatus(UserStatus.ENABLED);
        administrator.setNeedChangePassword(true);
        administrator.setRemark(
            "This administrator user is automatically created during system initialization.");
        administratorRepository.save(administrator);
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

    private final AlternativeJdkIdGenerator idGenerator = new AlternativeJdkIdGenerator();

    private final AdministratorRepository   administratorRepository;

    private final PasswordEncoder           passwordEncoder;

    private final RedissonClient            redissonClient;

    public DefaultAdministratorInitializer(AdministratorRepository administratorRepository,
                                           PasswordEncoder passwordEncoder,
                                           RedissonClient redissonClient) {
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.redissonClient = redissonClient;
    }

}
