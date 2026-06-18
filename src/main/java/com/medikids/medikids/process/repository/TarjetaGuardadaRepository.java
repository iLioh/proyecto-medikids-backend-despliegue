package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.TarjetaGuardada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TarjetaGuardadaRepository extends JpaRepository<TarjetaGuardada, Integer> {

    @Query("SELECT t FROM TarjetaGuardada t WHERE t.id_usuario = :idUsuario AND t.activo = true")
    List<TarjetaGuardada> findByIdUsuarioAndActivoTrue(@Param("idUsuario") int idUsuario);

    @Modifying
    @Transactional
    @Query("UPDATE TarjetaGuardada t SET t.es_predeterminada = false WHERE t.id_usuario = :idUsuario")
    void clearPredeterminada(@Param("idUsuario") int idUsuario);
}
