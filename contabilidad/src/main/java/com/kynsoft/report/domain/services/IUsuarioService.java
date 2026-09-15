package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.UsuarioDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface IUsuarioService extends UserDetailsService {

    UsuarioDto create(UsuarioDto dto);

    UsuarioDto update(UsuarioDto dto);

    void delete(UUID id);

    UsuarioDto findById(UUID id);

    UsuarioDto findByUsername(String username);

    UsuarioDto findByTrabajadorId(UUID trabajadorId);

    boolean existsByUsername(String username);

    boolean existsByTrabajadorId(UUID trabajadorId);

    List<UsuarioDto> findAll();

    List<UsuarioDto> findByFincaId(UUID fincaId);

    void updateLastLogin(UUID id);
}
