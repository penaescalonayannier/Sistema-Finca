package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.TipoAnimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface TipoAnimalReadDataJPARepository extends JpaRepository<TipoAnimal, UUID>, JpaSpecificationExecutor<TipoAnimal> {

    @Override
    Page<TipoAnimal> findAll(Specification<TipoAnimal> specification, Pageable pageable);

    Optional<TipoAnimal> findByCodigo(String codigo);

    List<TipoAnimal> findByActivoTrueOrderByOrdenAsc();

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);
}
