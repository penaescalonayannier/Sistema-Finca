package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.DocumentoCaja; import org.springframework.data.jpa.repository.*; import org.springframework.stereotype.Repository; import java.util.UUID;
@Repository public interface DocumentoCajaWriteDataJPARepository extends JpaRepository<DocumentoCaja,UUID> { @Query(value="SELECT nextval('documento_caja_numero_seq')",nativeQuery=true) Long siguienteNumero(); }
