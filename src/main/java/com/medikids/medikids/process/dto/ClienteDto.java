package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class ClienteDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_cliente;
    private int id_usuario;
    private int dni_responsable;
    private String direccion;

    // Datos enriquecidos de la FK id_usuario
    private UsuarioDto usuario;
}
