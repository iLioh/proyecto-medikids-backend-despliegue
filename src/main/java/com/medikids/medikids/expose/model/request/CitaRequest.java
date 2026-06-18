package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CitaRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String motivo;
    private String estado;
    private Character asistencia; // 0: No | 1: Sí
    private String comentarios;
    private int id_horario;
    private int id_medico;
    private int id_paciente;
    private String fecha_cita;
    private String hora_cita;
    private int id_pago;

    // Datos del comprobante de pago
    private String tipoComprobante;    // "boleta" o "factura"
    private String numeroDocumento;    // DNI (boleta) o RUC (factura)
    private String nombreRazonSocial;  // Nombre del responsable o razón social
    private String metodoPago;         // "Efectivo", "Tarjeta", "Transferencia"
}
