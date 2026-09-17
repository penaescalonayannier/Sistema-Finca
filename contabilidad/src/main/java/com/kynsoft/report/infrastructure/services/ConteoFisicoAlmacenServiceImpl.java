package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.*;
import com.kynsoft.report.domain.services.IConteoFisicoAlmacenService;
import com.kynsoft.report.domain.services.IMovimientoStockService;
import com.kynsoft.report.domain.services.INumeracionService;
import com.kynsoft.report.infrastructure.entity.*;
import com.kynsoft.report.infrastructure.repository.command.*;
import com.kynsoft.report.infrastructure.repository.query.*;
import com.kynsoft.report.infrastructure.security.TenantContext;
import com.kynsoft.report.infrastructure.security.TenantValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Expediente SC-2-15. El saldo teórico se congela al abrir; si el almacén se
 * mueve antes del cierre, se rechaza el cierre para evitar ajustar contra una
 * base vencida. Los únicos ajustes que crea son SC-2-16, trazados al conteo.
 */
@Service
public class ConteoFisicoAlmacenServiceImpl implements IConteoFisicoAlmacenService {
    private static final double EPS = .000001d;
    private final ConteoFisicoAlmacenWriteDataJPARepository conteoWrite;
    private final ConteoFisicoAlmacenReadDataJPARepository conteoRead;
    private final ConteoFisicoLineaWriteDataJPARepository lineaWrite;
    private final ConteoFisicoLineaReadDataJPARepository lineaRead;
    private final AlmacenReadDataJPARepository almacenRead;
    private final AlmacenFincaProductoReadDataJPARepository afpRead;
    private final AlmacenFincaProductoWriteDataJPARepository afpWrite;
    private final FincaProductoReadDataJPARepository fincaProductoRead;
    private final FincaProductoWriteDataJPARepository fincaProductoWrite;
    private final INumeracionService numeracion;
    private final IMovimientoStockService movimiento;
    private final AuditoriaTransaccionalService auditoria;

    public ConteoFisicoAlmacenServiceImpl(ConteoFisicoAlmacenWriteDataJPARepository conteoWrite, ConteoFisicoAlmacenReadDataJPARepository conteoRead,
      ConteoFisicoLineaWriteDataJPARepository lineaWrite, ConteoFisicoLineaReadDataJPARepository lineaRead, AlmacenReadDataJPARepository almacenRead,
      AlmacenFincaProductoReadDataJPARepository afpRead, AlmacenFincaProductoWriteDataJPARepository afpWrite,
      FincaProductoReadDataJPARepository fincaProductoRead, FincaProductoWriteDataJPARepository fincaProductoWrite,
      INumeracionService numeracion, IMovimientoStockService movimiento, AuditoriaTransaccionalService auditoria) {
      this.conteoWrite=conteoWrite; this.conteoRead=conteoRead; this.lineaWrite=lineaWrite; this.lineaRead=lineaRead; this.almacenRead=almacenRead;
      this.afpRead=afpRead; this.afpWrite=afpWrite; this.fincaProductoRead=fincaProductoRead; this.fincaProductoWrite=fincaProductoWrite;
      this.numeracion=numeracion; this.movimiento=movimiento; this.auditoria=auditoria;
    }

    @Override @Transactional(transactionManager="writeTransactionManager")
    public UUID abrir(CrearConteoFisicoRequest r) {
      if (r == null || r.getAlmacenId()==null || vacio(r.getResponsableConteo())) throw new IllegalArgumentException("Almacén y responsable del conteo son obligatorios.");
      Almacen almacen=almacenRead.findByIdWithProductos(r.getAlmacenId()).orElseThrow(()->new IllegalArgumentException("Almacén no encontrado."));
      if (!Boolean.TRUE.equals(almacen.getActivo())) throw new IllegalArgumentException("No se puede inventariar un almacén inactivo.");
      UUID fincaId=almacen.getFinca().getId(); TenantValidator.validateWriteAccess(fincaId);
      if (conteoWrite.existsByAlmacenIdAndEstado(almacen.getId(), EstadoConteoFisico.ABIERTO)) throw new IllegalArgumentException("Ya existe un conteo físico abierto para este almacén.");
      List<AlmacenFincaProducto> productos=afpRead.findByAlmacenIdAndActivoTrue(almacen.getId());
      if(productos.isEmpty()) throw new IllegalArgumentException("El almacén no tiene productos activos para inventariar.");
      ConteoFisicoAlmacen c=new ConteoFisicoAlmacen(); c.setId(UUID.randomUUID()); c.setFincaId(fincaId); c.setAlmacen(almacen);
      c.setNumero(numeracion.generarSiguienteNumero(fincaId, TipoDocumento.CONTEO_FISICO)); c.setEstado(EstadoConteoFisico.ABIERTO); c.setFechaApertura(LocalDateTime.now());
      c.setResponsableConteo(texto(r.getResponsableConteo())); c.setVerificadoPor(texto(r.getVerificadoPor())); c.setObservacionesApertura(texto(r.getObservaciones())); c.setUsuarioId(TenantContext.getUsuarioId()); conteoWrite.save(c);
      for(AlmacenFincaProducto p:productos){ ConteoFisicoLinea l=new ConteoFisicoLinea(); l.setId(UUID.randomUUID());l.setConteo(c);l.setAlmacenFincaProductoId(p.getId());
        l.setProductoCodigo(texto(p.getFincaProducto().getProducto().getCode()));l.setProductoNombre(p.getFincaProducto().getProducto().getName());l.setUnidadMedida(p.getFincaProducto().getProducto().getUnidadMedida() == null ? null : p.getFincaProducto().getProducto().getUnidadMedida().getNombre());l.setExistenciaTeorica(valor(p.getStock())); lineaWrite.save(l); }
      auditoria.registrarDespuesDeConfirmar(TipoAccion.CREATE,"CONTEO_FISICO_ALMACEN",c.getId(),"Apertura de inventario físico SC-2-15 "+c.getNumero(),null,Map.of("almacenId",almacen.getId(),"lineas",productos.size(),"numero",c.getNumero()));
      return c.getId();
    }

    @Override @Transactional(transactionManager="writeTransactionManager")
    public void cerrar(UUID id, CerrarConteoFisicoRequest r) {
      if(r==null || r.getLineas()==null || r.getLineas().isEmpty() || vacio(r.getAutorizadoPor())) throw new IllegalArgumentException("Debe informar todas las cantidades contadas y quién autoriza el cierre.");
      ConteoFisicoAlmacen c=conteoWrite.findById(id).orElseThrow(()->new IllegalArgumentException("Conteo físico no encontrado.")); TenantValidator.validateWriteAccess(c.getFincaId());
      if(c.getEstado()!=EstadoConteoFisico.ABIERTO) throw new IllegalArgumentException("El conteo físico ya fue cerrado.");
      List<ConteoFisicoLinea> lineas=lineaRead.findByConteoIdOrderByProductoNombreAsc(id); Map<UUID,LineaConteoFisicoRequest> capturadas=r.getLineas().stream().filter(x->x.getLineaId()!=null).collect(Collectors.toMap(LineaConteoFisicoRequest::getLineaId,x->x,(a,b)->{throw new IllegalArgumentException("Hay líneas repetidas en el conteo.");}));
      if(capturadas.size()!=lineas.size() || !lineas.stream().allMatch(x->capturadas.containsKey(x.getId()))) throw new IllegalArgumentException("Debe contar y declarar cada producto del expediente.");
      for(ConteoFisicoLinea l:lineas){ LineaConteoFisicoRequest x=capturadas.get(l.getId()); if(x.getCantidadContada()==null||x.getCantidadContada()<0) throw new IllegalArgumentException("La cantidad física no puede ser negativa."); AlmacenFincaProducto actual=afpRead.findById(l.getAlmacenFincaProductoId()).orElseThrow(()->new IllegalStateException("Producto de almacén inexistente.")); if(Math.abs(valor(actual.getStock())-valor(l.getExistenciaTeorica()))>EPS) throw new IllegalStateException("El stock de "+l.getProductoNombre()+" cambió desde la apertura. Cancele y abra un nuevo conteo para conservar la trazabilidad."); }
      boolean hayDiferencia=lineas.stream().anyMatch(l->Math.abs(capturadas.get(l.getId()).getCantidadContada()-l.getExistenciaTeorica())>EPS);
      String numeroAjuste=hayDiferencia?numeracion.generarSiguienteNumero(c.getFincaId(),TipoDocumento.AJUSTE_INVENTARIO):null;
      for(ConteoFisicoLinea l:lineas){ LineaConteoFisicoRequest x=capturadas.get(l.getId()); double fisico=x.getCantidadContada(), diferencia=fisico-valor(l.getExistenciaTeorica()); l.setExistenciaFisica(fisico);l.setDiferencia(diferencia);l.setObservaciones(texto(x.getObservaciones()));lineaWrite.save(l);
        if(Math.abs(diferencia)>EPS){ AlmacenFincaProducto afp=afpRead.findById(l.getAlmacenFincaProductoId()).orElseThrow(); FincaProducto fp=fincaProductoRead.findByIdWithDetails(afp.getFincaProducto().getId()).orElseThrow(); double anterior=valor(afp.getStock()); afp.setStock(fisico); afpWrite.save(afp); fp.setStock(valor(fp.getStock())+diferencia); if(fp.getStock()<-EPS) throw new IllegalStateException("El ajuste dejaría la existencia de finca negativa."); fincaProductoWrite.save(fp);
          movimiento.registrar(MovimientoStockDto.builder().id(UUID.randomUUID()).fincaProductoId(fp.getId()).fincaId(c.getFincaId()).productoId(fp.getProducto().getId()).almacenId(c.getAlmacen().getId()).tipo(diferencia>0?TipoMovimientoStock.ENTRADA_AJUSTE:TipoMovimientoStock.SALIDA_AJUSTE).cantidad(Math.abs(diferencia)).stockAnterior(anterior).stockNuevo(fisico).referenciaId(c.getId()).referenciaTabla("CONTEO_FISICO_ALMACEN").descripcion("Ajuste "+numeroAjuste+" derivado del conteo "+c.getNumero()).observaciones(texto(x.getObservaciones())).build()); }
      }
      c.setNumeroAjuste(numeroAjuste);c.setEstado(EstadoConteoFisico.CERRADO);c.setFechaCierre(LocalDateTime.now());c.setAutorizadoPor(texto(r.getAutorizadoPor()));c.setObservacionesCierre(texto(r.getObservaciones()));conteoWrite.save(c);
      auditoria.registrarDespuesDeConfirmar(TipoAccion.STOCK_ADJUSTMENT,"CONTEO_FISICO_ALMACEN",c.getId(),"Cierre SC-2-15"+(numeroAjuste==null?" sin diferencias":" y ajuste SC-2-16 "+numeroAjuste),Map.of("estado","ABIERTO"),Map.of("estado","CERRADO","numeroAjuste",numeroAjuste==null?"SIN AJUSTE":numeroAjuste));
    }
    @Override @Transactional(readOnly=true,transactionManager="readTransactionManager") public ConteoFisicoDetalleDto detalle(UUID id){ ConteoFisicoAlmacen c=conteoRead.findById(id).orElseThrow(()->new IllegalArgumentException("Conteo físico no encontrado.")); TenantValidator.validateReadAccess(c.getFincaId()); return dto(c); }
    @Override @Transactional(readOnly=true,transactionManager="readTransactionManager") public List<ConteoFisicoDetalleDto> listar(UUID fincaId){ TenantValidator.validateReadAccess(fincaId); return conteoRead.findByFincaIdOrderByFechaAperturaDesc(fincaId).stream().map(this::dto).toList(); }
    private ConteoFisicoDetalleDto dto(ConteoFisicoAlmacen c){return ConteoFisicoDetalleDto.builder().id(c.getId()).fincaId(c.getFincaId()).almacenId(c.getAlmacen().getId()).almacenNombre(c.getAlmacen().getNombre()).numero(c.getNumero()).estado(c.getEstado()).fechaApertura(c.getFechaApertura()).fechaCierre(c.getFechaCierre()).responsableConteo(c.getResponsableConteo()).verificadoPor(c.getVerificadoPor()).autorizadoPor(c.getAutorizadoPor()).observacionesApertura(c.getObservacionesApertura()).observacionesCierre(c.getObservacionesCierre()).numeroAjuste(c.getNumeroAjuste()).lineas(lineaRead.findByConteoIdOrderByProductoNombreAsc(c.getId()).stream().map(l->ConteoFisicoLineaDto.builder().id(l.getId()).almacenFincaProductoId(l.getAlmacenFincaProductoId()).productoCodigo(l.getProductoCodigo()).productoNombre(l.getProductoNombre()).unidadMedida(l.getUnidadMedida()).existenciaTeorica(l.getExistenciaTeorica()).existenciaFisica(l.getExistenciaFisica()).diferencia(l.getDiferencia()).observaciones(l.getObservaciones()).build()).toList()).build();}
    private double valor(Double x){return x==null?0:x;} private boolean vacio(String x){return texto(x)==null;} private String texto(String x){return x==null||x.isBlank()?null:x.trim();}
}
