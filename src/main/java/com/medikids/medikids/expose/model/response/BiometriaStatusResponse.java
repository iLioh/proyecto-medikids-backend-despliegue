package com.medikids.medikids.expose.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class BiometriaStatusResponse {
    private boolean registrado;
    private Date fechaRegistro;
}
