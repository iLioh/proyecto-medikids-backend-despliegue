package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MedicoConUsuarioRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private int telefono;
    private String nro_colegiatura;
    private String url_foto;
    private String genero;
    private String estado;
    private int id_especialidad;
}
