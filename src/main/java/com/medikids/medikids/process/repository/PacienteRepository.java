package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// La librería provee las consultas básicas, no es necesario implementarlas.
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    @Query("SELECT p FROM Paciente p WHERE p.id_cliente = :idCliente")
    List<Paciente> findByIdCliente(@Param("idCliente") int idCliente);
}