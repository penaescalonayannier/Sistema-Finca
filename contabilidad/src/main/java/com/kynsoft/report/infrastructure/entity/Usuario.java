package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.Rol;
import com.kynsoft.report.domain.dto.UsuarioDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "trabajador_id", nullable = false, unique = true)
    private UUID trabajadorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id", insertable = false, updatable = false)
    private Trabajador trabajador;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public Usuario(UsuarioDto dto) {
        this.id = dto.getId();
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.rol = dto.getRol() != null ? dto.getRol() : Rol.USER;
        this.activo = dto.getActivo() != null ? dto.getActivo() : true;
        this.trabajadorId = dto.getTrabajadorId();
        this.createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now();
        this.lastLogin = dto.getLastLogin();
    }

    public UsuarioDto toAggregate() {
        String fincaName = null;
        UUID fincaId = null;

        if (trabajador != null && Hibernate.isInitialized(trabajador)) {
            fincaId = trabajador.getFincaId();
            // Only access Finca if it's initialized (not a proxy)
            if (Hibernate.isInitialized(trabajador.getFinca()) && trabajador.getFinca() != null) {
                fincaName = trabajador.getFinca().getName();
            }
        }

        return UsuarioDto.builder()
                .id(id)
                .username(username)
                .password(password)
                .rol(rol)
                .activo(activo)
                .trabajadorId(trabajadorId)
                .trabajadorNombre(trabajador != null && Hibernate.isInitialized(trabajador) ? trabajador.getNombre() : null)
                .trabajadorRuc(trabajador != null && Hibernate.isInitialized(trabajador) ? trabajador.getRuc() : null)
                .fincaId(fincaId)
                .fincaName(fincaName)
                .createdAt(createdAt)
                .lastLogin(lastLogin)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
