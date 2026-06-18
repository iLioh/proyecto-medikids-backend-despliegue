package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Integer> {
}