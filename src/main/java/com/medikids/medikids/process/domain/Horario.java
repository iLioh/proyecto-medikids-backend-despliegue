package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "horario")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ELIMINADO: @Column(nullable = false)
    private Integer id_horario;

    @Column(nullable = false)
    private Integer medico_id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private Time hora_inicio;

    @Column(name = "hora_fin", nullable = false)
    private Time hora_fin;

    @Column(name = "disponible", nullable = false)
    private char disponible;

    @Column(name = "id_medico", nullable = false)
    private int id_medico;
}
