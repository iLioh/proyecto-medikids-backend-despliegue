package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.AdminConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminConfigRepository extends JpaRepository<AdminConfig, Integer> {
    Optional<AdminConfig> findByClave(String clave);
}
