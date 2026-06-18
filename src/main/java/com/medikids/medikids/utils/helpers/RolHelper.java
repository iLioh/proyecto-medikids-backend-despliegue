package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.RolRequest;
import com.medikids.medikids.process.domain.Rol;
import com.medikids.medikids.process.dto.RolDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class RolHelper implements Serializable {

    private RolHelper() {
        throw new IllegalStateException("RolHelper class");
    }

    public static RolDto mapRol(Rol rol) {
        return RolDto.builder()
                .id_rol(rol.getId_rol())
                .nombre_rol(rol.getNombre_rol())
                .build();
    }

    public static Rol buildRol(RolRequest rol) {
        return Rol.builder()
                .nombre_rol(rol.getNombre_rol())
                .build();
    }

    public static List<RolDto> mapAll(List<Rol> roles) {
        return roles.stream()
                .map(RolHelper::mapRol)
                .collect(Collectors.toList());
    }

    public static Page<RolDto> mapPage(Page<Rol> rolPage) {
        List<RolDto> roles = rolPage.getContent().stream()
                .map(RolHelper::mapRol)
                .collect(Collectors.toList());
        return new PageImpl<>(roles);
    }
}