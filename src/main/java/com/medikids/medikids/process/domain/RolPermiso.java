package com.medikids.medikids.process.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "rol_permiso")
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_permiso")
    private Integer idRolPermiso;

    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @Column(name = "id_permiso", nullable = false)
    private Integer idPermiso;
}
