package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "historial_clinico")
public class HistorialClinico {

    @Id
    @Column(nullable = false)
    private int id_historial_clinico;

    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "tratamiento", nullable = false, columnDefinition = "TEXT")
    private String tratamiento;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fecha_registro;

    @Column(name = "id_cita", nullable = false)
    private Integer id_cita;

    @Column(name = "id_paciente", nullable = false)
    private Integer id_paciente;
}