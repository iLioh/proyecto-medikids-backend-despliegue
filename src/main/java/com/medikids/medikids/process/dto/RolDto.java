package com.medikids.medikids.process.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class RolDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_rol;
    private String nombre_rol;
}