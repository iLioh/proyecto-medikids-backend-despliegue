package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CrearAdminRequest {
    private int idRol;
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
    private int telefono;
}
