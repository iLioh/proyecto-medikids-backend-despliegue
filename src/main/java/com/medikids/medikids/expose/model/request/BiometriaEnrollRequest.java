package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class BiometriaEnrollRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idUsuario;
    private List<DescriptorEntry> descriptors;

    @Setter
    @Getter
    public static class DescriptorEntry implements Serializable {
        private String tipo;
        private List<Double> descriptor;
    }
}
