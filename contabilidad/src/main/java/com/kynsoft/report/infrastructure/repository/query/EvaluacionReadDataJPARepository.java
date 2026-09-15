package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Evaluacion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface EvaluacionReadDataJPARepository extends JpaRepository<Evaluacion, UUID>,
        JpaSpecificationExecutor<Evaluacion> {

    List<Evaluacion> findByMesAndYear(String mes, Integer year);

    @Query("SELECT e FROM Evaluacion e WHERE e.year = :year AND e.mes IN :meses")
    List<Evaluacion> findByYearAndMesIn(@Param("year") Integer year, @Param("meses") List<String> meses);

    @Query("SELECT DISTINCT e.mes FROM Evaluacion e WHERE e.year = :year ORDER BY e.mes")
    List<String> findDistinctMesesByYear(@Param("year") Integer year);

    @Query("SELECT DISTINCT e.year FROM Evaluacion e ORDER BY e.year DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT e FROM Evaluacion e WHERE e.trabajadorId = :trabajadorId AND e.mes = :mes AND e.year = :year")
    java.util.Optional<Evaluacion> findByTrabajadorIdAndMesAndYear(
            @Param("trabajadorId") UUID trabajadorId,
            @Param("mes") String mes,
            @Param("year") Integer year);
}
