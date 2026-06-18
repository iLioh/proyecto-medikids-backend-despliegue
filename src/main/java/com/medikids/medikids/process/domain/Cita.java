package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "cita")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(nullable = false)
    private int id_cita;

    @Column(nullable = false)
    private String motivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date fecha_registro;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = true)
    private Character asistencia;

    @Column(nullable = true)
    private String comentarios;

    @Column(nullable = false)
    private int id_horario;

    @Column(nullable = false)
    private int id_medico;

    @Column(nullable = false)
    private int id_paciente;

    @Column(name = "fecha_cita")
    private LocalDate fecha_cita;

    @Column(name = "hora_cita", length = 10)
    private String hora_cita;

    @Column(name = "id_pago", nullable = true)
    private Integer id_pago;

    @PrePersist
    protected void onCreate() {
        this.fecha_registro = new Date();
    }
}
