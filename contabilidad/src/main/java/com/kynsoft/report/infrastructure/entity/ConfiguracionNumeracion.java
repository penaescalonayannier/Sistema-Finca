package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ConfiguracionNumeracionDto;
import com.kynsoft.report.domain.dto.TipoDocumento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "configuracion_numeracion",
       uniqueConstraints = @UniqueConstraint(columnNames = {"finca_id", "tipo", "anio"}))
public class ConfiguracionNumeracion {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "finca_id", nullable = false)
    private UUID fincaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id", insertable = false, updatable = false)
    private Finca finca;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoDocumento tipo;

    @Column(name = "prefijo", nullable = false, length = 10)
    private String prefijo;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero;

    @Version
    @Column(name = "version")
    private Long version;

    public ConfiguracionNumeracion(ConfiguracionNumeracionDto dto) {
        this.id = dto.getId();
        this.fincaId = dto.getFincaId();
        this.tipo = dto.getTipo();
        this.prefijo = dto.getPrefijo();
        this.anio = dto.getAnio();
        this.ultimoNumero = dto.getUltimoNumero();
    }

    public ConfiguracionNumeracionDto toAggregate() {
        return ConfiguracionNumeracionDto.builder()
                .id(id)
                .fincaId(fincaId)
                .fincaCode(finca != null ? finca.getCode() : null)
                .fincaName(finca != null ? finca.getName() : null)
                .tipo(tipo)
                .prefijo(prefijo)
                .anio(anio)
                .ultimoNumero(ultimoNumero)
                .build();
    }

    public String generarSiguienteNumero() {
        this.ultimoNumero++;
        return String.format("%s-%d-%05d", prefijo, anio, ultimoNumero);
    }
}
