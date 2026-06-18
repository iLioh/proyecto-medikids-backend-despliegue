package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// La libreria provee a las consultas, no es necesario hacerlas.
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    @Query("SELECT c FROM Cliente c WHERE c.usuario.id_usuario = :idUsuario")
    Optional<Cliente> findByIdUsuario(@Param("idUsuario") int idUsuario);

    // Busca un cliente por su DNI del responsable
    // nativeQuery=true evita conflictos del parser JPQL con el campo dni_responsable
    @Query(value = "SELECT * FROM Cliente WHERE dni_responsable = :dni", nativeQuery = true)
    Optional<Cliente> findByDni(@Param("dni") String dni);
}
