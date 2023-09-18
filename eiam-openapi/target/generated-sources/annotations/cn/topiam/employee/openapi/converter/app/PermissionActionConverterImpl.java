package cn.topiam.employee.openapi.converter.app;

import cn.topiam.employee.common.entity.permission.PermissionActionEntity;
import cn.topiam.employee.openapi.pojo.request.app.AppPermissionsActionParam;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-09-18T13:28:50+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Azul Systems, Inc.)"
)
@Component
public class PermissionActionConverterImpl implements PermissionActionConverter {

    @Override
    public PermissionActionEntity toEntity(AppPermissionsActionParam dto) {
        if ( dto == null ) {
            return null;
        }

        PermissionActionEntity permissionActionEntity = new PermissionActionEntity();

        permissionActionEntity.setValue( dto.getValue() );
        permissionActionEntity.setName( dto.getName() );
        permissionActionEntity.setType( dto.getType() );

        return permissionActionEntity;
    }

    @Override
    public AppPermissionsActionParam toDTO(PermissionActionEntity entities) {
        if ( entities == null ) {
            return null;
        }

        AppPermissionsActionParam appPermissionsActionParam = new AppPermissionsActionParam();

        appPermissionsActionParam.setType( entities.getType() );
        appPermissionsActionParam.setValue( entities.getValue() );
        appPermissionsActionParam.setName( entities.getName() );

        return appPermissionsActionParam;
    }
}
