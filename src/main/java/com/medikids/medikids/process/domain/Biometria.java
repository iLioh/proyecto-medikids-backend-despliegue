package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "biometria")
public class Biometria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int id_biometria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "JSON")
    private String face_descriptor;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private int muestra;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date fecha_registro;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    protected void onCreate() {
        this.fecha_registro = new Date();
        this.activo = true;
    }
}
