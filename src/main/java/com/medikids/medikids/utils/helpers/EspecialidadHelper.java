package com.medikids.medikids.utils.helpers;

import com.medikids.medikids.expose.model.request.EspecialidadRequest;
import com.medikids.medikids.process.domain.Especialidad;
import com.medikids.medikids.process.dto.EspecialidadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class EspecialidadHelper implements Serializable {

    private EspecialidadHelper() {
        throw new IllegalStateException("EspecialidadHelper class");
    }

    public static EspecialidadDto mapEspecialidad(Especialidad especialidad) {
        return EspecialidadDto.builder()
                .id_especialidad(especialidad.getId_especialidad())
                .nombre(especialidad.getNombre())
                .descripcion(especialidad.getDescripcion())
                .precio(especialidad.getPrecio())
                .build();
    }

    public static Especialidad buildEspecialidad(EspecialidadRequest especialidad) {
        return Especialidad.builder()
                .nombre(especialidad.getNombre())
                .descripcion(especialidad.getDescripcion())
                .precio(especialidad.getPrecio())
                .build();
    }

    public static List<EspecialidadDto> mapAll(List<Especialidad> especialidades) {
        return especialidades.stream()
                .map(EspecialidadHelper::mapEspecialidad)
                .collect(Collectors.toList());
    }

    public static Page<EspecialidadDto> mapPage(Page<Especialidad> especialidadPage) {
        List<EspecialidadDto> especialidades = especialidadPage.getContent().stream()
                .map(EspecialidadHelper::mapEspecialidad)
                .collect(Collectors.toList());
        return new PageImpl<>(especialidades);
    }
}