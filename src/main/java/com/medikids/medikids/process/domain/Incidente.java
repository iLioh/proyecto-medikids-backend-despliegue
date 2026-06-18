package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "incidente")
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id_incidente;

    @Column(name = "tipo_incidente", nullable = false, length = 100)
    private String tipo_incidente;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "respuesta_admin", columnDefinition = "TEXT")
    private String respuesta_admin;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fecha_registro;

    @Column(name = "id_medico", nullable = false)
    private int id_medico;
}