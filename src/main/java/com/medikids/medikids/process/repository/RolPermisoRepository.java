package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Integer> {

    @Query("SELECT p.codigo FROM Permiso p WHERE p.idPermiso IN (SELECT rp.idPermiso FROM RolPermiso rp WHERE rp.idRol = :idRol)")
    List<String> findCodigosPermisoByIdRol(@Param("idRol") Integer idRol);
}
