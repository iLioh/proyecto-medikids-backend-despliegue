package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class PagoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_pago;
    private double monto;
    private String metodo_pago;
    private String estado_transaccion;
}