package com.kynsoft.report.domain.services;
import com.kynsoft.report.domain.dto.*;
import java.util.*;
public interface IConteoFisicoAlmacenService {
 UUID abrir(CrearConteoFisicoRequest request);
 void cerrar(UUID id, CerrarConteoFisicoRequest request);
 ConteoFisicoDetalleDto detalle(UUID id);
 List<ConteoFisicoDetalleDto> listar(UUID fincaId);
}
