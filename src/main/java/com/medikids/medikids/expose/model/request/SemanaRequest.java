package com.medikids.medikids.expose.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class SemanaRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_medico;
    private LocalDate inicio;
    private LocalDate fin;
    private List<HorarioBloque> bloques;

    @Setter
    @Getter
    public static class HorarioBloque implements Serializable {
        private static final long serialVersionUID = 1L;
        private LocalDate fecha;

        @JsonFormat(pattern = "HH:mm:ss")
        private Time hora_inicio;

        @JsonFormat(pattern = "HH:mm:ss")
        private Time hora_fin;
    }
}
