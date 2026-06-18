package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Builder
public class CitaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_cita;
    private String motivo;
    private Date fecha_registro;
    private String estado;
    private Character asistencia; //0: NO | 1:Sí
    private String comentarios;
    private int id_horario;
    private int id_medico; // FK
    private int id_paciente; // FK
    private String fecha_cita;
    private String hora_cita;
    private int id_pago; // FK

    // Datos enriquecidos de las FKs
    private MedicoDto medico;
    private PacienteDto paciente;
    private PagoDto pago;
}
