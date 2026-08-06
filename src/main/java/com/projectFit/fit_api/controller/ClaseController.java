package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.ClaseRequestDTO;
import com.projectFit.fit_api.dto.ClaseResponseDTO;
import com.projectFit.fit_api.dto.TipoActividadResponseDTO;
import com.projectFit.fit_api.services.ClaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clases")
@RequiredArgsConstructor
public class ClaseController {
    private final ClaseService claseService;

    //CREATE CLASE
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<ClaseResponseDTO> crearClase(@Valid @RequestBody ClaseRequestDTO claseRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(claseService.crearClase(claseRequestDTO));
    }
    //UPDATE CLASE
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @PutMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> actualizarClase(@PathVariable Long id, @Valid @RequestBody ClaseRequestDTO claseRequestDTO){
        return ResponseEntity.ok(claseService.actualizarClase(id, claseRequestDTO));
    }

    //DAR DE BAJA UNA CLASE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<Void> darDeBajaClase(@PathVariable Long id){
        claseService.darDeBajaClase(id);
        return ResponseEntity.noContent().build();
    }

    //GET CLASE POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'SOCIO')")
    public ResponseEntity<ClaseResponseDTO> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(claseService.obtenerPorId(id));
    }

    //GET POR TIPO ACTIVIDAD
    @GetMapping("/tipo-actividad/{tipoActividadId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'SOCIO')")
    public ResponseEntity<List<ClaseResponseDTO>> obtenerPorTipoActividad(
            @PathVariable Long tipoActividadId) {
        return ResponseEntity.ok(claseService.obtenerPorTipoActividad(tipoActividadId));
    }

    //GET ALL CLASES
    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'SOCIO')")
    public ResponseEntity<List<ClaseResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(claseService.obtenerTodas());
    }

    //GET CLASES DISPONIBLES PARA RESERVAR SEGUN EL PLAN DEL SOCIO
    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('SOCIO')")
    public ResponseEntity<List<ClaseResponseDTO>> obtenerClasesDisponiblesParaSocio(
            @AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(claseService.obtenerClasesDisponiblesParaSocio(jwt.getSubject()));

    }
    //GET CLASES POR DIA PARA DASHBOARD
    @GetMapping("/dia/{diaSemana}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<List<ClaseResponseDTO>> obtenerClasesDelDia(@PathVariable String diaSemana) {
        return ResponseEntity.ok(claseService.obtenerClasesPorDia(diaSemana));
    }
}
