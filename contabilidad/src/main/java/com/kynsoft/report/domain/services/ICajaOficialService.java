package com.kynsoft.report.domain.services;
import com.kynsoft.report.domain.dto.*;
import java.util.*;
public interface ICajaOficialService { FondoCajaDto guardarFondo(FondoCajaRequest request); List<FondoCajaDto> listarFondos(UUID fincaId); UUID crearActa(ActaResponsabilidadCajaRequest request); void cerrarActa(UUID id, String observaciones); List<ActaResponsabilidadCajaDto> listarActas(UUID fincaId); UUID crearIncidencia(CrearIncidenciaArqueoCajaRequest request); void actualizarIncidencia(UUID id, ActualizarIncidenciaArqueoCajaRequest request); List<IncidenciaArqueoCajaDto> listarIncidencias(UUID fincaId); TableroControlCajaDto tablero(UUID fincaId, int anio, int mes); }
