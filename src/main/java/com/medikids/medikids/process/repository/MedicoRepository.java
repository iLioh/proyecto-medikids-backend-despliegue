package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    @Query("SELECT m FROM Medico m WHERE m.id_usuario = :idUsuario")
    Optional<Medico> findByIdUsuario(int idUsuario);
}