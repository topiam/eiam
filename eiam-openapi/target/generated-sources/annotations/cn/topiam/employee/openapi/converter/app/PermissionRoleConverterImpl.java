package cn.topiam.employee.openapi.converter.app;

import cn.topiam.employee.common.entity.permission.PermissionRoleEntity;
import cn.topiam.employee.openapi.pojo.request.app.save.AppPermissionRoleCreateParam;
import cn.topiam.employee.openapi.pojo.request.app.update.PermissionRoleUpdateParam;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionRoleListResult;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionRoleResult;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-09-18T13:44:04+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Azul Systems, Inc.)"
)
@Component
public class PermissionRoleConverterImpl implements PermissionRoleConverter {

    @Override
    public AppPermissionRoleListResult entityConvertToRolePaginationResult(PermissionRoleEntity page) {
        if ( page == null ) {
            return null;
        }

        AppPermissionRoleListResult appPermissionRoleListResult = new AppPermissionRoleListResult();

        if ( page.getId() != null ) {
            appPermissionRoleListResult.setId( String.valueOf( page.getId() ) );
        }
        appPermissionRoleListResult.setName( page.getName() );
        appPermissionRoleListResult.setCode( page.getCode() );
        if ( page.getAppId() != null ) {
            appPermissionRoleListResult.setAppId( String.valueOf( page.getAppId() ) );
        }
        appPermissionRoleListResult.setEnabled( page.getEnabled() );
        appPermissionRoleListResult.setRemark( page.getRemark() );

        return appPermissionRoleListResult;
    }

    @Override
    public PermissionRoleEntity roleCreateParamConvertToEntity(AppPermissionRoleCreateParam param) {
        if ( param == null ) {
            return null;
        }

        PermissionRoleEntity permissionRoleEntity = new PermissionRoleEntity();

        permissionRoleEntity.setRemark( param.getRemark() );
        permissionRoleEntity.setName( param.getName() );
        permissionRoleEntity.setCode( param.getCode() );
        permissionRoleEntity.setAppId( param.getAppId() );

        permissionRoleEntity.setEnabled( Boolean.TRUE );

        return permissionRoleEntity;
    }

    @Override
    public PermissionRoleEntity roleUpdateParamConvertToEntity(PermissionRoleUpdateParam param) {
        if ( param == null ) {
            return null;
        }

        PermissionRoleEntity permissionRoleEntity = new PermissionRoleEntity();

        if ( param.getId() != null ) {
            permissionRoleEntity.setId( Long.parseLong( param.getId() ) );
        }
        permissionRoleEntity.setRemark( param.getRemark() );
        permissionRoleEntity.setName( param.getName() );
        permissionRoleEntity.setCode( param.getCode() );

        return permissionRoleEntity;
    }

    @Override
    public AppPermissionRoleResult entityConvertToRoleDetailResult(PermissionRoleEntity role) {
        if ( role == null ) {
            return null;
        }

        AppPermissionRoleResult appPermissionRoleResult = new AppPermissionRoleResult();

        if ( role.getId() != null ) {
            appPermissionRoleResult.setId( String.valueOf( role.getId() ) );
        }
        if ( role.getAppId() != null ) {
            appPermissionRoleResult.setAppId( String.valueOf( role.getAppId() ) );
        }
        appPermissionRoleResult.setName( role.getName() );
        appPermissionRoleResult.setCode( role.getCode() );
        appPermissionRoleResult.setEnabled( role.getEnabled() );
        appPermissionRoleResult.setRemark( role.getRemark() );

        return appPermissionRoleResult;
    }
}
