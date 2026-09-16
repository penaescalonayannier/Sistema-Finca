package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.ChequeTransferencia; import org.springframework.data.jpa.repository.*; import org.springframework.stereotype.Repository; import java.util.UUID;
@Repository public interface ChequeTransferenciaWriteDataJPARepository extends JpaRepository<ChequeTransferencia,UUID> { @Query(value="SELECT nextval('cheque_transferencia_numero_seq')",nativeQuery=true) Long siguienteNumero(); }
