package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.ClaseRequestDTO;
import com.projectFit.fit_api.dto.ClaseResponseDTO;
import com.projectFit.fit_api.services.ClaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ClaseResponseDTO> crearClase(@Valid @RequestBody ClaseRequestDTO claseRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(claseService.crearClase(claseRequestDTO));
    }
    //UPDATE CLASE
    @PutMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> actualizarClase(@PathVariable Long id, @Valid @RequestBody ClaseRequestDTO claseRequestDTO){
        return ResponseEntity.ok(claseService.actualizarClase(id, claseRequestDTO));
    }

    //DAR DE BAJA UNA CLASE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBajaClase(@PathVariable Long id){
        claseService.darDeBajaClase(id);
        return ResponseEntity.noContent().build();
    }

    //GET CLASE POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ClaseResponseDTO> obtenerPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(claseService.obtenerPorId(id));
    }

    //GET POR TIPO ACTIVIDAD
    @GetMapping("/tipo-actividad/{tipoActividadId}")
    public ResponseEntity<List<ClaseResponseDTO>> obtenerPorTipoActividad(
            @PathVariable Long tipoActividadId) {
        return ResponseEntity.ok(claseService.obtenerPorTipoActividad(tipoActividadId));
    }

    //GET CLASES DISPONIBLES PARA RESERVAR SEGUN EL PLAN DEL SOCIO
    @GetMapping("/disponibles")
    public ResponseEntity<List<ClaseResponseDTO>> obtenerClasesDisponiblesParaSocio(
            @AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(claseService.obtenerClasesDisponiblesParaSocio(jwt.getSubject()));

    }
}
