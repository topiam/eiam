package cn.topiam.employee.openapi.converter.app;

import cn.topiam.employee.common.entity.permission.PermissionActionEntity;
import cn.topiam.employee.common.entity.permission.PermissionResourceEntity;
import cn.topiam.employee.openapi.pojo.request.app.save.AppPermissionResourceCreateParam;
import cn.topiam.employee.openapi.pojo.request.app.update.AppPermissionResourceUpdateParam;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionResourceGetResult;
import cn.topiam.employee.openapi.pojo.response.app.AppPermissionResourceListResult;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-09-18T13:44:04+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Azul Systems, Inc.)"
)
@Component
public class PermissionResourceConverterImpl implements PermissionResourceConverter {

    @Override
    public PermissionResourceEntity resourceCreateParamConvertToEntity(AppPermissionResourceCreateParam param) {
        if ( param == null ) {
            return null;
        }

        PermissionResourceEntity permissionResourceEntity = new PermissionResourceEntity();

        permissionResourceEntity.setCode( param.getCode() );
        permissionResourceEntity.setName( param.getName() );
        permissionResourceEntity.setAppId( param.getAppId() );
        permissionResourceEntity.setDesc( param.getDesc() );
        permissionResourceEntity.setEnabled( param.getEnabled() );

        return permissionResourceEntity;
    }

    @Override
    public PermissionResourceEntity resourceUpdateParamConvertToEntity(AppPermissionResourceUpdateParam param) {
        if ( param == null ) {
            return null;
        }

        PermissionResourceEntity permissionResourceEntity = new PermissionResourceEntity();

        if ( param.getId() != null ) {
            permissionResourceEntity.setId( Long.parseLong( param.getId() ) );
        }
        permissionResourceEntity.setName( param.getName() );
        permissionResourceEntity.setDesc( param.getDesc() );

        permissionResourceEntity.setEnabled( Boolean.TRUE );

        return permissionResourceEntity;
    }

    @Override
    public AppPermissionResourceListResult entityConvertToResourceListResult(PermissionResourceEntity data) {
        if ( data == null ) {
            return null;
        }

        AppPermissionResourceListResult appPermissionResourceListResult = new AppPermissionResourceListResult();

        if ( data.getId() != null ) {
            appPermissionResourceListResult.setId( String.valueOf( data.getId() ) );
        }
        appPermissionResourceListResult.setName( data.getName() );
        appPermissionResourceListResult.setCode( data.getCode() );
        if ( data.getAppId() != null ) {
            appPermissionResourceListResult.setAppId( String.valueOf( data.getAppId() ) );
        }
        appPermissionResourceListResult.setDesc( data.getDesc() );

        return appPermissionResourceListResult;
    }

    @Override
    public AppPermissionResourceGetResult entityConvertToResourceGetResult(PermissionResourceEntity resource) {
        if ( resource == null ) {
            return null;
        }

        AppPermissionResourceGetResult appPermissionResourceGetResult = new AppPermissionResourceGetResult();

        appPermissionResourceGetResult.setActions( appPermissionActionEntityListToAppPermissionsActionList( resource.getActions() ) );
        appPermissionResourceGetResult.setName( resource.getName() );
        appPermissionResourceGetResult.setCode( resource.getCode() );
        appPermissionResourceGetResult.setDesc( resource.getDesc() );
        appPermissionResourceGetResult.setAppId( resource.getAppId() );

        return appPermissionResourceGetResult;
    }

    protected AppPermissionResourceGetResult.AppPermissionsAction appPermissionActionEntityToAppPermissionsAction(PermissionActionEntity permissionActionEntity) {
        if ( permissionActionEntity == null ) {
            return null;
        }

        AppPermissionResourceGetResult.AppPermissionsAction appPermissionsAction = new AppPermissionResourceGetResult.AppPermissionsAction();

        if ( permissionActionEntity.getId() != null ) {
            appPermissionsAction.setId( String.valueOf( permissionActionEntity.getId() ) );
        }
        appPermissionsAction.setType( permissionActionEntity.getType() );
        appPermissionsAction.setValue( permissionActionEntity.getValue() );
        appPermissionsAction.setName( permissionActionEntity.getName() );

        return appPermissionsAction;
    }

    protected List<AppPermissionResourceGetResult.AppPermissionsAction> appPermissionActionEntityListToAppPermissionsActionList(List<PermissionActionEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<AppPermissionResourceGetResult.AppPermissionsAction> list1 = new ArrayList<AppPermissionResourceGetResult.AppPermissionsAction>( list.size() );
        for ( PermissionActionEntity permissionActionEntity : list ) {
            list1.add( appPermissionActionEntityToAppPermissionsAction(permissionActionEntity) );
        }

        return list1;
    }
}
