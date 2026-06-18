package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;

@Setter
@Getter
@Builder
public class HorarioDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_horario;
    private LocalDate fecha;
    private Time hora_inicio;
    private Time hora_fin;
    private char disponible;
    private int id_medico;

    // Datos enriquecidos de la FK id_medico
    private MedicoDto medico;
}
