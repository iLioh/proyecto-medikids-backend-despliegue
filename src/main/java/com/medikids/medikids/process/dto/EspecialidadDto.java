package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class EspecialidadDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_especialidad;
    private String nombre;
    private String descripcion;
    private Double precio;
}