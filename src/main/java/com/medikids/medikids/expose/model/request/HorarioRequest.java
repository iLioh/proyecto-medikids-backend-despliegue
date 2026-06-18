package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

@Setter
@Getter
public class HorarioRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date fecha;
    private Time hora_inicio;
    private Time hora_fin;
    private char disponible;
    private int id_medico;
}
