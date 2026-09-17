package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.FormaPago;
import com.kynsoft.report.domain.dto.PagoDeudaDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "pago_deuda")
public class PagoDeuda {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "trabajador_id")
    private UUID trabajadorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id", insertable = false, updatable = false)
    private Trabajador trabajador;

    @Column(name = "monto")
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago")
    private FormaPago formaPago;

    @Column(name = "referencia_bancaria")
    private String referenciaBancaria;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "numero_recibo")
    private String numeroRecibo;

    @Column(name = "saldo_anterior")
    private Double saldoAnterior;

    @Column(name = "saldo_nuevo")
    private Double saldoNuevo;

    @Column(name = "concepto")
    private String concepto;

    @Column(name = "finca_id")
    private UUID fincaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id", insertable = false, updatable = false)
    private Finca finca;

    public PagoDeuda(PagoDeudaDto dto) {
        this.id = dto.getId();
        this.trabajadorId = dto.getTrabajadorId();
        this.monto = dto.getMonto();
        this.formaPago = dto.getFormaPago();
        this.referenciaBancaria = dto.getReferenciaBancaria();
        this.fecha = dto.getFecha();
        this.numeroRecibo = dto.getNumeroRecibo();
        this.saldoAnterior = dto.getSaldoAnterior();
        this.saldoNuevo = dto.getSaldoNuevo();
        this.concepto = dto.getConcepto();
        this.fincaId = dto.getFincaId();
    }

    public PagoDeudaDto toAggregate() {
        return PagoDeudaDto.builder()
                .id(id)
                .trabajadorId(trabajadorId)
                .trabajadorNombre(trabajador != null ? trabajador.getNombre() : null)
                .trabajadorRuc(trabajador != null ? trabajador.getRuc() : null)
                .monto(monto)
                .formaPago(formaPago)
                .referenciaBancaria(referenciaBancaria)
                .fecha(fecha)
                .numeroRecibo(numeroRecibo)
                .saldoAnterior(saldoAnterior)
                .saldoNuevo(saldoNuevo)
                .concepto(concepto)
                .fincaId(fincaId)
                .fincaName(finca != null ? finca.getName() : (trabajador != null && trabajador.getFinca() != null ? trabajador.getFinca().getName() : null))
                .build();
    }
}
