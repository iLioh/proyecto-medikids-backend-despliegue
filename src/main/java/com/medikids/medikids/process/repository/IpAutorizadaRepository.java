package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.IpAutorizada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpAutorizadaRepository extends JpaRepository<IpAutorizada, Integer> {
    Optional<IpAutorizada> findByIpAndActivoTrue(String ip);
    boolean existsByIpAndActivoTrue(String ip);
    List<IpAutorizada> findByIdUsuario(Integer idUsuario);
}
