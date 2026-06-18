package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.Biometria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiometriaRepository extends JpaRepository<Biometria, Integer> {

    @Query("SELECT b FROM Biometria b WHERE b.usuario.id_usuario = :idUsuario AND b.activo = true")
    List<Biometria> findByUsuarioIdAndActivoTrue(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Query("DELETE FROM Biometria b WHERE b.usuario.id_usuario = :idUsuario")
    void deleteByUsuarioId(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT COUNT(b) > 0 FROM Biometria b WHERE b.usuario.id_usuario = :idUsuario AND b.activo = true")
    boolean existsByUsuarioIdAndActivoTrue(@Param("idUsuario") Integer idUsuario);
}
