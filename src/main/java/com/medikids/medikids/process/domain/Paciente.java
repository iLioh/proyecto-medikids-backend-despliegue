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
@Table(name = "paciente")   // en minúscula, igual que la tabla en MySQL
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id_paciente;

    @Column(name = "nombre_completo", nullable = false)
    private String nombre_completo;

    @Column(name = "dni_menor", nullable = false, unique = true)
    private String dni_menor;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fecha_nacimiento;

    @Column(name = "id_cliente", nullable = false)
    private int id_cliente;
}