package com.medikids.medikids.process.repository;
import java.util.List;
import com.medikids.medikids.process.domain.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Integer> {
    @Query("SELECT i FROM Incidente i WHERE i.id_medico = :idMedico")
    List<Incidente> findByIdMedico(@Param("idMedico") int idMedico);
}