package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import com.medikids.medikids.process.domain.Medico.EstadoMedico;

@Setter
@Getter
public class MedicoRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nro_colegiatura;
    private String url_foto;
    private String genero;
    private EstadoMedico estado;
    private int id_usuario;
    private int id_especialidad;
}
