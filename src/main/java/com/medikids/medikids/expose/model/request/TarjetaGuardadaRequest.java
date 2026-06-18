package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TarjetaGuardadaRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String alias;
    private String ultimos_digitos;
    private String marca;
    private String nombre_titular;
    private int mes_vencimiento;
    private int anio_vencimiento;
}
