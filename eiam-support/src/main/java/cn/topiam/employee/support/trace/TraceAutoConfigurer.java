/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
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
package cn.topiam.employee.support.trace;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 链路追中自动配置
 * @author TopIAM
 * Created by support@topiam.cn on 2019/10/10 21:28
 */
@Configuration
public class TraceAutoConfigurer implements Serializable {
    private static final long serialVersionUID = 7134677505437854819L;

    /**
     * taskExecutor
     * @return {@link AsyncTaskExecutor}
     */
    @Bean
    public AsyncTaskExecutor mdcThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = MdcThreadPoolTaskExecutor.newWithInheritedMdc(8, 32, 1,
            TimeUnit.MINUTES, 1000);
        executor.setMaxPoolSize(10);
        return executor;
    }
}
