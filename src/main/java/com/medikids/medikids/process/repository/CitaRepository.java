package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// La libreria provee a las consultas, no es necesario hacerlas.
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    // Busca todas las citas asociadas a un paciente (hijo del cliente)
    // nativeQuery=true usa SQL puro para evitar conflictos del parser JPQL con snake_case
    @Query(value = "SELECT * FROM cita WHERE id_paciente = :idPaciente", nativeQuery = true)
    List<Cita> findByIdPaciente(@Param("idPaciente") int idPaciente);

    // Busca todas las citas de todos los hijos de un cliente (JOIN con paciente)
    @Query(value = "SELECT c.* FROM cita c INNER JOIN paciente p ON c.id_paciente = p.id_paciente WHERE p.id_cliente = :idCliente", nativeQuery = true)
    List<Cita> findByCliente(@Param("idCliente") int idCliente);

    @Query(value = "SELECT * FROM cita WHERE id_medico = :idMedico", nativeQuery = true)
    List<Cita> findByIdMedico(@Param("idMedico") int idMedico);

    @Query(value = "SELECT c.id_cita FROM cita c INNER JOIN paciente p ON c.id_paciente = p.id_paciente WHERE p.id_cliente = :idCliente", nativeQuery = true)
    List<Integer> findCitaIdsByCliente(@Param("idCliente") int idCliente);
}

