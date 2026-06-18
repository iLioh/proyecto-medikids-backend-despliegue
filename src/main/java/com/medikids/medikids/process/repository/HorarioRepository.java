package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {
    @Query("SELECT h FROM Horario h WHERE h.medico_id = :idMedico")
    List<Horario> findByMedico(@Param("idMedico") int idMedico);

    @Query("SELECT h FROM Horario h WHERE h.medico_id = :idMedico AND h.disponible = '1' ORDER BY h.fecha ASC, h.hora_inicio ASC")
    List<Horario> findDisponiblesByMedico(@Param("idMedico") int idMedico);

    @Query("SELECT h FROM Horario h WHERE h.medico_id = :idMedico AND h.fecha BETWEEN :inicio AND :fin ORDER BY h.fecha ASC, h.hora_inicio ASC")
    List<Horario> findByMedicoAndFechaBetween(@Param("idMedico") int idMedico, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Modifying
    @Transactional
    @Query("DELETE FROM Horario h WHERE h.medico_id = :idMedico AND h.fecha BETWEEN :inicio AND :fin AND h.disponible = '1'")
    void deleteDisponiblesByMedicoAndFechaBetween(@Param("idMedico") int idMedico, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
