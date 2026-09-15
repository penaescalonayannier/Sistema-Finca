package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.ConfiguracionEmpresaDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Configuración de la empresa para documentos oficiales cubanos.
 * Contiene los datos de uso obligatorio según Resolución 11/2007 y 55/2021 del MFP.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "configuracion_empresa")
public class ConfiguracionEmpresa {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nit", nullable = false)
    private String nit;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "municipio")
    private String municipio;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "cuenta_bancaria")
    private String cuentaBancaria;

    @Column(name = "banco")
    private String banco;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "activo")
    private Boolean activo = true;

    public ConfiguracionEmpresa(ConfiguracionEmpresaDto dto) {
        this.id = dto.getId();
        this.nombre = dto.getNombre();
        this.codigo = dto.getCodigo();
        this.nit = dto.getNit();
        this.direccion = dto.getDireccion();
        this.municipio = dto.getMunicipio();
        this.provincia = dto.getProvincia();
        this.cuentaBancaria = dto.getCuentaBancaria();
        this.banco = dto.getBanco();
        this.telefono = dto.getTelefono();
        this.email = dto.getEmail();
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
    }

    public ConfiguracionEmpresaDto toAggregate() {
        return ConfiguracionEmpresaDto.builder()
                .id(id)
                .nombre(nombre)
                .codigo(codigo)
                .nit(nit)
                .direccion(direccion)
                .municipio(municipio)
                .provincia(provincia)
                .cuentaBancaria(cuentaBancaria)
                .banco(banco)
                .telefono(telefono)
                .email(email)
                .activo(activo)
                .build();
    }
}
