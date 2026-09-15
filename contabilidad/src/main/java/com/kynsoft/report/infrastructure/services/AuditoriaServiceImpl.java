package com.kynsoft.report.infrastructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.AuditoriaResponse;
import com.kynsoft.report.domain.dto.AuditoriaDto;
import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.domain.services.IAuditoriaService;
import com.kynsoft.report.infrastructure.entity.Auditoria;
import com.kynsoft.report.infrastructure.entity.Usuario;
import com.kynsoft.report.infrastructure.repository.command.AuditoriaWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.AuditoriaReadDataJPARepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditoriaServiceImpl implements IAuditoriaService {

    private final AuditoriaWriteDataJPARepository writeRepository;
    private final AuditoriaReadDataJPARepository readRepository;
    private final ObjectMapper objectMapper;

    public AuditoriaServiceImpl(AuditoriaWriteDataJPARepository writeRepository,
                                 AuditoriaReadDataJPARepository readRepository) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(TipoAccion accion, String entidad, UUID entidadId,
                          String descripcion, Object valorAnterior, Object valorNuevo) {
        try {
            UUID usuarioId = null;
            String username = "SYSTEM";

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Usuario) {
                Usuario usuario = (Usuario) auth.getPrincipal();
                usuarioId = usuario.getId();
                username = usuario.getUsername();
            }

            String ipAddress = getClientIpAddress();

            AuditoriaDto dto = AuditoriaDto.builder()
                    .id(UUID.randomUUID())
                    .usuarioId(usuarioId)
                    .username(username)
                    .accion(accion)
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .descripcion(descripcion)
                    .valorAnterior(toJson(valorAnterior))
                    .valorNuevo(toJson(valorNuevo))
                    .ipAddress(ipAddress)
                    .createdAt(LocalDateTime.now())
                    .build();

            writeRepository.save(new Auditoria(dto));
        } catch (Exception e) {
            // Log error but don't fail the main transaction
            System.err.println("Error registering audit: " + e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(UUID usuarioId, String username, TipoAccion accion,
                          String entidad, UUID entidadId, String descripcion,
                          Object valorAnterior, Object valorNuevo, String ipAddress) {
        try {
            AuditoriaDto dto = AuditoriaDto.builder()
                    .id(UUID.randomUUID())
                    .usuarioId(usuarioId)
                    .username(username)
                    .accion(accion)
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .descripcion(descripcion)
                    .valorAnterior(toJson(valorAnterior))
                    .valorNuevo(toJson(valorNuevo))
                    .ipAddress(ipAddress)
                    .createdAt(LocalDateTime.now())
                    .build();

            writeRepository.save(new Auditoria(dto));
        } catch (Exception e) {
            System.err.println("Error registering audit: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaDto findById(UUID id) {
        return readRepository.findById(id)
                .map(Auditoria::toAggregate)
                .orElseThrow(() -> new RuntimeException("Audit record not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Auditoria> specifications = new GenericSpecificationsBuilder<>(filterCriteria);

        // Default sort by createdAt DESC
        Specification<Auditoria> spec = Specification.where(specifications);

        Page<Auditoria> data = readRepository.findAll(spec, pageable);
        return createPaginatedResponse(data);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDto> getHistorialEntidad(String entidad, UUID entidadId) {
        return readRepository.findByEntidadAndEntidadIdOrderByCreatedAtDesc(entidad, entidadId)
                .stream()
                .map(Auditoria::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getEntidades() {
        return readRepository.findDistinctEntidades();
    }

    private PaginatedResponse createPaginatedResponse(Page<Auditoria> data) {
        List<AuditoriaResponse> responses = data.getContent().stream()
                .map(Auditoria::toAggregate)
                .map(AuditoriaResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
