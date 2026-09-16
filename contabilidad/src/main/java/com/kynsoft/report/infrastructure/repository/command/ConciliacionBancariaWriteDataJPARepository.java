package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.*; import org.springframework.data.jpa.repository.*; import org.springframework.stereotype.Repository; import java.util.UUID;
@Repository public interface ConciliacionBancariaWriteDataJPARepository extends JpaRepository<ConciliacionBancaria,UUID> { @Query(value="SELECT nextval('conciliacion_bancaria_numero_seq')",nativeQuery=true) Long siguienteNumero(); }
