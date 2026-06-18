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
@Table(name = "intento_login")
public class IntentoLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intento")
    private Integer idIntento;

    @Column(nullable = false)
    private String email;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(nullable = false)
    private LocalDateTime fechaIntento;

    @Column(nullable = false)
    private Boolean exitoso;

    @Column(nullable = false, length = 20)
    private String tipo;

    @PrePersist
    protected void onCreate() {
        this.fechaIntento = LocalDateTime.now();
    }
}
