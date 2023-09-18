package cn.topiam.employee.openapi.converter.app;

import cn.topiam.employee.common.entity.permission.PermissionPolicyEntity;
import cn.topiam.employee.openapi.pojo.request.app.save.AppPermissionPolicyCreateParam;
import cn.topiam.employee.openapi.pojo.request.app.update.AppPermissionPolicyUpdateParam;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-09-18T13:28:50+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Azul Systems, Inc.)"
)
@Component
public class PermissionPolicyConverterImpl implements PermissionPolicyConverter {

    @Override
    public PermissionPolicyEntity policyCreateParamConvertToEntity(AppPermissionPolicyCreateParam param) {
        if ( param == null ) {
            return null;
        }

        PermissionPolicyEntity permissionPolicyEntity = new PermissionPolicyEntity();

        permissionPolicyEntity.setAppId( param.getAppId() );
        permissionPolicyEntity.setSubjectId( param.getSubjectId() );
        permissionPolicyEntity.setSubjectType( param.getSubjectType() );
        permissionPolicyEntity.setObjectId( param.getObjectId() );
        permissionPolicyEntity.setObjectType( param.getObjectType() );
        permissionPolicyEntity.setEffect( param.getEffect() );

        return permissionPolicyEntity;
    }

    @Override
    public PermissionPolicyEntity policyUpdateParamConvertToEntity(AppPermissionPolicyUpdateParam param) {
        if ( param == null ) {
            return null;
        }

        PermissionPolicyEntity permissionPolicyEntity = new PermissionPolicyEntity();

        permissionPolicyEntity.setId( param.getId() );
        permissionPolicyEntity.setAppId( param.getAppId() );
        permissionPolicyEntity.setSubjectId( param.getSubjectId() );
        permissionPolicyEntity.setSubjectType( param.getSubjectType() );
        permissionPolicyEntity.setObjectId( param.getObjectId() );
        permissionPolicyEntity.setObjectType( param.getObjectType() );
        permissionPolicyEntity.setEffect( param.getEffect() );

        return permissionPolicyEntity;
    }
}
