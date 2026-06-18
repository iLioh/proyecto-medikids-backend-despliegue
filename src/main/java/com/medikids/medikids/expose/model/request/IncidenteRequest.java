package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class IncidenteRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipo_incidente;
    private String descripcion;
    private int id_medico;
}
