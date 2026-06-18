package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class IncidenteDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_incidente;
    private String tipo_incidente;
    private String descripcion;
    private String respuesta_admin;
    private LocalDateTime fecha_registro;
    private int id_medico; // FK

    // Datos enriquecidos de la FK id_medico
    private MedicoDto medico;
}