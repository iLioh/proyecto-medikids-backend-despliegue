package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class BiometriaVerifyRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private List<Double> descriptor;
    private String preAuthToken;
}
