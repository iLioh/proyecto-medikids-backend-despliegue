package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class IpAutorizadaRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_usuario;
    private String ip;
    private String descripcion;
    private Boolean activo;
}