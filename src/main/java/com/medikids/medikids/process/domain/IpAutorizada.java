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
@Table(name = "ip_autorizada")
public class IpAutorizada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ip_autorizada")
    private Integer idIpAutorizada;

    @Column(nullable = false, length = 45)
    private String ip;

    @Column(length = 150)
    private String descripcion;

    @Column(nullable = false, length = 1)
    private String visible;

    @Column(nullable = false)
    private Integer idUsuario;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private LocalDateTime fechaModificado;

    @PrePersist
    protected void onCreate() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaRegistro = ahora;
        this.fechaModificado = ahora;
        if (this.visible == null) this.visible = "1";
        if (this.activo == null) this.activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificado = LocalDateTime.now();
    }
}
