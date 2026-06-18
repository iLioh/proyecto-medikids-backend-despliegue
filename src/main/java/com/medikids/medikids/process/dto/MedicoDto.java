package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import com.medikids.medikids.process.domain.Medico.EstadoMedico;

@Setter
@Getter
@Builder
public class MedicoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_medico;
    private String nro_colegiatura;
    private String url_foto;
    private String genero;
    private int id_usuario; // FK
    private int id_especialidad; // FK
    private char activo; // 0: No | 1: Sí
    private EstadoMedico estado; // "activo" | "inactivo"

    // Datos enriquecidos de la FK id_usuario
    private UsuarioDto usuario;

    // Datos enriquecidos de la FK id_especialidad
    private EspecialidadDto especialidad;
}