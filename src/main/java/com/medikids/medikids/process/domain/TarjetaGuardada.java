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
@Table(name = "tarjeta_guardada")
public class TarjetaGuardada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id_tarjeta;

    @Column(name = "id_usuario", nullable = false)
    private int id_usuario;

    @Column(nullable = false, length = 50)
    private String alias;

    @Column(name = "ultimos_digitos", nullable = false, length = 4)
    private String ultimos_digitos;

    @Column(nullable = false, length = 20)
    private String marca;

    @Column(name = "nombre_titular", nullable = false)
    private String nombre_titular;

    @Column(name = "mes_vencimiento", nullable = false)
    private int mes_vencimiento;

    @Column(name = "anio_vencimiento", nullable = false)
    private int anio_vencimiento;

    @Column(name = "es_predeterminada", nullable = false)
    private boolean es_predeterminada;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fecha_creacion;

    @PrePersist
    protected void onCreate() {
        this.fecha_creacion = LocalDateTime.now();
        this.activo = true;
    }
}
