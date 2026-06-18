package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Builder
public class IpAutorizadaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_ip_autorizada;
    private int id_usuario;
    private String ip;
    private String descripcion;
    private boolean activo;
    private Date fecha_registro;
    private Date fecha_modificado;
}