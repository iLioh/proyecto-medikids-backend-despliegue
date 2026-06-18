package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class TarjetaGuardadaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_tarjeta;
    private int id_usuario;
    private String alias;
    private String ultimos_digitos;
    private String marca;
    private String nombre_titular;
    private int mes_vencimiento;
    private int anio_vencimiento;
    private boolean es_predeterminada;
    private LocalDateTime fecha_creacion;
}
