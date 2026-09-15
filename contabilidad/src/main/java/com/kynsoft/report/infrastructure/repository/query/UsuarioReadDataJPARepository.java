package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface UsuarioReadDataJPARepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByTrabajadorId(UUID trabajadorId);

    boolean existsByUsername(String username);

    boolean existsByTrabajadorId(UUID trabajadorId);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.username")
    List<Usuario> findAllActivos();

    @Query("SELECT u FROM Usuario u JOIN u.trabajador t WHERE t.fincaId = :fincaId AND u.activo = true")
    List<Usuario> findByFincaId(@Param("fincaId") UUID fincaId);
}
