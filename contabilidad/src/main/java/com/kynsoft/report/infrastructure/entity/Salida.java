package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DestinoSalida;
import com.kynsoft.report.domain.dto.SalidaDto;
import com.kynsoft.report.domain.dto.TipoSalida;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "salida")
public class Salida {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoSalida tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "destino", nullable = false)
    private DestinoSalida destino;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "finca_producto_id", nullable = false)
    private UUID fincaProductoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_producto_id", insertable = false, updatable = false)
    private FincaProducto fincaProducto;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @OneToMany(mappedBy = "salida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemSalida> items = new ArrayList<>();

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Salida(SalidaDto dto) {
        this.id = dto.getId();
        this.tipo = dto.getTipo();
        this.destino = dto.getDestino();
        this.numero = dto.getNumero();
        this.fincaProductoId = dto.getFincaProductoId();
        this.fecha = dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now();
        this.observaciones = dto.getObservaciones();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public SalidaDto toAggregate() {
        return SalidaDto.builder()
                .id(id)
                .tipo(tipo)
                .destino(destino)
                .numero(numero)
                .fincaProductoId(fincaProductoId)
                .fincaCode(fincaProducto != null && fincaProducto.getFinca() != null ? fincaProducto.getFinca().getCode() : null)
                .fincaName(fincaProducto != null && fincaProducto.getFinca() != null ? fincaProducto.getFinca().getName() : null)
                .productoCode(fincaProducto != null && fincaProducto.getProducto() != null ? fincaProducto.getProducto().getCode() : null)
                .productoName(fincaProducto != null && fincaProducto.getProducto() != null ? fincaProducto.getProducto().getName() : null)
                .unidadMedida(fincaProducto != null && fincaProducto.getProducto() != null && fincaProducto.getProducto().getUnidadMedida() != null
                        ? fincaProducto.getProducto().getUnidadMedida().name() : "UND")
                .stockActual(fincaProducto != null ? fincaProducto.getStock() : null)
                .fecha(fecha)
                .observaciones(observaciones)
                .cantidadTotal(items != null ? items.stream().mapToDouble(ItemSalida::getCantidad).sum() : 0.0)
                .activo(activo)
                .build();
    }
}
