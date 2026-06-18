package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.IntentoLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntentoLoginRepository extends JpaRepository<IntentoLogin, Integer> {
    List<IntentoLogin> findTop50ByOrderByFechaIntentoDesc();
    List<IntentoLogin> findByTipoOrderByFechaIntentoDesc(String tipo);
}
