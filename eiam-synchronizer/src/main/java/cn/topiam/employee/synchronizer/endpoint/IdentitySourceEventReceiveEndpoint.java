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
package cn.topiam.employee.synchronizer.endpoint;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.topiam.employee.common.entity.identitysource.IdentitySourceEntity;
import cn.topiam.employee.common.repository.identitysource.IdentitySourceRepository;
import cn.topiam.employee.identitysource.core.IdentitySource;
import cn.topiam.employee.identitysource.core.IdentitySourceConfig;
import cn.topiam.employee.support.trace.Trace;
import cn.topiam.employee.synchronizer.configuration.IdentitySourceBeanUtils;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import static cn.topiam.employee.synchronizer.constants.SynchronizerConstants.EVENT_RECEIVE_PATH;

/**
 * 身份源回调事件端点
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/9/20 21:24
 */
@Slf4j
@RestController
@RequestMapping(EVENT_RECEIVE_PATH)
@SuppressWarnings("unchecked")
public class IdentitySourceEventReceiveEndpoint {

    /**
     * 事件通知处理
     *
     * @param request {@link  HttpServletRequest}
     * @param response {@link  HttpServletRequest}
     * @return {@link  ResponseEntity}
     */
    @Trace
    @RequestMapping(value = "/{code}")
    public ResponseEntity<?> receive(HttpServletRequest request, @PathVariable String code,
                                     @RequestBody(required = false) String body) {
        Optional<IdentitySourceEntity> optional = identitySourceRepository.findByCode(code);
        if (optional.isPresent()) {
            String beanName = IdentitySourceBeanUtils
                .getSourceBeanName(optional.get().getId().toString());
            IdentitySource<IdentitySourceConfig> identitySource = (IdentitySource<IdentitySourceConfig>) applicationContext
                .getBean(beanName);
            Object event = identitySource.event(request, body);
            return ResponseEntity.ok(event);
        }
        return ResponseEntity.ok().build();
    }

    private final ApplicationContext       applicationContext;

    private final IdentitySourceRepository identitySourceRepository;

    public IdentitySourceEventReceiveEndpoint(ApplicationContext applicationContext,
                                              IdentitySourceRepository identitySourceRepository) {
        this.applicationContext = applicationContext;
        this.identitySourceRepository = identitySourceRepository;
    }
}
