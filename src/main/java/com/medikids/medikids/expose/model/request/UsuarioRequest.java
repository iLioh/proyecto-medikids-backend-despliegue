package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UsuarioRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_rol;
    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private int telefono;
}
