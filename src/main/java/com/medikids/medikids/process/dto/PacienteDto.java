package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@Builder
public class PacienteDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_paciente;
    private String nombre_completo;
    private String dni_menor;
    private LocalDate fecha_nacimiento;
    private int id_cliente; // FK

    // Datos enriquecidos de la FK id_cliente
    private ClienteDto cliente;
}