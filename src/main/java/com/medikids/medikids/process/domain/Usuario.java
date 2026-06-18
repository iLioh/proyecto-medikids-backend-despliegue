package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int id_usuario;
    @Column(nullable = false)
    private int id_rol;
    @Column(nullable = false)
    private String nombres;
    @Column(nullable = false)
    private String apellidos;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private int telefono;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date fecha_registro;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha_modificado;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable = false)
    private char visible; //1: Sí, 0: No

    // ── Campos para 2FA ──
    @Column(nullable = true)
    private String codigoVerificacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date codigoExpiracion;

    @PrePersist
    protected void onCreate() {
        Date ahora = new Date();
        this.fecha_registro = ahora;
        this.fecha_modificado = ahora;
        this.activo = true;
        this.visible = '1';
    }

    @PreUpdate
    protected void onUpdate() {
        this.fecha_modificado = new Date();
    }
}
