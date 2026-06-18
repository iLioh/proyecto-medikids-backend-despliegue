package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "medico")
public class Medico {

    public enum EstadoMedico {
        activo, inactivo
    }

    public enum Genero {
        masculino, femenino, otro
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id_medico;

    @Column(name = "nro_colegiatura", nullable = false, unique = true, length = 20)
    private String nro_colegiatura;

    @Column(name = "url_foto", length = 255)
    private String url_foto;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", length = 20)
    private Genero genero;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoMedico estado;

    @Column(name = "id_usuario", nullable = false)
    private int id_usuario;

    @Column(name = "id_especialidad", nullable = false)
    private int id_especialidad;

    @Column(name = "activo", nullable = false)
    private Character activo;
}