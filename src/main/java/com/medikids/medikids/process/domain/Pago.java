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
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id_pago;

    @Column(nullable = false)
    private double monto;

    @Column(name = "metodo_pago", nullable = false)
    private String metodo_pago;

    @Column(name = "estado_transaccion", nullable = false)
    private String estado_transaccion;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fecha_pago;

    @Column(name = "id_cita", nullable = true)
    private Integer id_cita;

    @PrePersist
    protected void onCreate() {
        if (this.fecha_pago == null) {
            this.fecha_pago = LocalDateTime.now();
        }
    }
}