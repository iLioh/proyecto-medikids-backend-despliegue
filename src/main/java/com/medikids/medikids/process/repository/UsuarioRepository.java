package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// La libreria provee a las consultas, no es necesario hacerlas.
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
