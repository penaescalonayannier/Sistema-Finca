package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.UsuarioDto;
import com.kynsoft.report.domain.services.IUsuarioService;
import com.kynsoft.report.infrastructure.entity.Usuario;
import com.kynsoft.report.infrastructure.repository.command.UsuarioWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.UsuarioReadDataJPARepository;
import com.kynsoft.report.infrastructure.security.TenantContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioWriteDataJPARepository writeRepository;
    private final UsuarioReadDataJPARepository readRepository;

    public UsuarioServiceImpl(UsuarioWriteDataJPARepository writeRepository,
                              UsuarioReadDataJPARepository readRepository) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    @Transactional
    public UsuarioDto create(UsuarioDto dto) {
        if (dto.getId() == null) {
            dto.setId(UUID.randomUUID());
        }
        dto.setCreatedAt(LocalDateTime.now());
        Usuario entity = new Usuario(dto);
        Usuario saved = writeRepository.save(entity);
        return readRepository.findById(saved.getId())
                .map(Usuario::toAggregate)
                .orElse(saved.toAggregate());
    }

    @Override
    @Transactional
    public UsuarioDto update(UsuarioDto dto) {
        Usuario existing = writeRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Usuario not found: " + dto.getId()));

        existing.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPassword(dto.getPassword());
        }
        existing.setRol(dto.getRol());
        existing.setActivo(dto.getActivo());

        Usuario saved = writeRepository.save(existing);
        return readRepository.findById(saved.getId())
                .map(Usuario::toAggregate)
                .orElse(saved.toAggregate());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Usuario existing = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found: " + id));
        existing.setActivo(false);
        writeRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto findById(UUID id) {
        return readRepository.findById(id)
                .map(Usuario::toAggregate)
                .orElseThrow(() -> new RuntimeException("Usuario not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto findByUsername(String username) {
        return readRepository.findByUsername(username)
                .map(Usuario::toAggregate)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto findByTrabajadorId(UUID trabajadorId) {
        return readRepository.findByTrabajadorId(trabajadorId)
                .map(Usuario::toAggregate)
                .orElseThrow(() -> new RuntimeException("Usuario not found for trabajador: " + trabajadorId));
    }

    @Override
    public boolean existsByUsername(String username) {
        return readRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByTrabajadorId(UUID trabajadorId) {
        return readRepository.existsByTrabajadorId(trabajadorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> findAll() {
        UUID fincaId = TenantContext.getEffectiveFincaId();
        if (fincaId != null) {
            return readRepository.findByFincaId(fincaId).stream()
                    .map(Usuario::toAggregate)
                    .collect(Collectors.toList());
        }
        // ADMIN without selection sees all
        return readRepository.findAllActivos().stream()
                .map(Usuario::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> findByFincaId(UUID fincaId) {
        return readRepository.findByFincaId(fincaId).stream()
                .map(Usuario::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateLastLogin(UUID id) {
        writeRepository.findById(id).ifPresent(usuario -> {
            usuario.setLastLogin(LocalDateTime.now());
            writeRepository.save(usuario);
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return readRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario not found: " + username));
    }
}
