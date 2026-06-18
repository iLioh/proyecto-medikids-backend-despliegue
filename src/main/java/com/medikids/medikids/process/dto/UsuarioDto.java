package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Builder
public class UsuarioDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_usuario;
    private int id_rol;
    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private int telefono;
    private Date fecha_registro;
    private Date fecha_modificado;
    private char visible; //1: Sí, 0: No
    private Boolean activo;

    // Datos enriquecidos de la FK id_rol
    private RolDto rol;
}
