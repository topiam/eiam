package cn.topiam.employee.openapi.endpoint.app;

import cn.topiam.employee.audit.annotation.Audit;
import cn.topiam.employee.audit.event.type.EventType;
import cn.topiam.employee.common.entity.app.query.AppAccountQuery;
import cn.topiam.employee.openapi.common.OpenApiResponse;
import cn.topiam.employee.openapi.pojo.result.app.AppAccountListResult;
import cn.topiam.employee.openapi.pojo.save.app.AppAccountCreateParam;
import cn.topiam.employee.openapi.service.app.AppAccountService;
import cn.topiam.employee.support.lock.Lock;
import cn.topiam.employee.support.preview.Preview;
import cn.topiam.employee.support.repository.page.domain.Page;
import cn.topiam.employee.support.repository.page.domain.PageModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.topiam.employee.openapi.constant.OpenApiV1Constants.APP_ACCOUNT_PATH;

/**
 * 应用账户
 *
 * @author xlsea
 * @since 2024-01-15
 */
@Validated
@Tag(name = "应用账户")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = APP_ACCOUNT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AppAccountController {

    /**
     * 获取应用账户列表
     *
     * @param page  {@link PageModel}
     * @param query {@link AppAccountQuery}
     * @return {@link AppAccountListResult}
     */
    @Operation(summary = "获取应用账户列表")
    @GetMapping(value = "/list")
    public OpenApiResponse<Page<AppAccountListResult>> getAppAccountList(PageModel page,
                                                                         @Validated AppAccountQuery query) {
        return OpenApiResponse.success((appAccountService.getAppAccountList(page, query)));
    }

    /**
     * 创建应用账户
     *
     * @param param {@link AppAccountCreateParam}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @PostMapping(value = "/create")
    @Audit(type = EventType.ADD_APP_ACCOUNT)
    @Operation(summary = "创建应用账户")
    public OpenApiResponse<Void> createAppAccount(@RequestBody @Validated AppAccountCreateParam param) {
        appAccountService.createAppAccount(param);
        return OpenApiResponse.success();
    }

    /**
     * 删除应用账户
     *
     * @param id {@link String}
     * @return {@link Boolean}
     */
    @Lock
    @Preview
    @Operation(summary = "删除应用账户")
    @Audit(type = EventType.DELETE_APP_ACCOUNT)
    @DeleteMapping(value = "/delete/{id}")
    public OpenApiResponse<Void> deleteAppAccount(@PathVariable(value = "id") String id) {
        appAccountService.deleteAppAccount(id);
        return OpenApiResponse.success();
    }

    /**
     * 应用账户服务类
     */
    private final AppAccountService appAccountService;
}
