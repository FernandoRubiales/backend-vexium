package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.SocioPlanRequestDTO;
import com.projectFit.fit_api.dto.SocioPlanResponseDTO;
import com.projectFit.fit_api.services.SocioPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/socio_plan")
@RequiredArgsConstructor
public class SocioPlanController {

    private final SocioPlanService socioPlanService;

    //ELEGIR PLAN
    @PostMapping
    public ResponseEntity<SocioPlanResponseDTO> elegirPlan(
            @Valid @RequestBody SocioPlanRequestDTO socioPlanRequestDTO,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(socioPlanService.elegirPlan(socioPlanRequestDTO, jwt.getSubject()));
    }

    //CUANDO SE ACEPTE EL PAGO, SE ACTIVA EL PLAN DEL SOCIO
    @PutMapping("/{id}/activar")
    public ResponseEntity<SocioPlanResponseDTO> activarPlan(
            @PathVariable Long id) {
        return ResponseEntity.ok(socioPlanService.activarPlan(id));
    }

    //GET DE LOS PLANES ACTIVOS DEL SOCIO
    @GetMapping("/activos")
    public ResponseEntity<List<SocioPlanResponseDTO>> obtenerPlanesActivos(
            @AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(socioPlanService.obtenerPlanesActivos(jwt.getSubject()));
    }

    //GET DE PLANES PENDIENTES DEL SOCIO POR DNI
    @GetMapping("/pendientes/dni/{dni}")
    public ResponseEntity<List<SocioPlanResponseDTO>> obtenerPlanesPendientesPorDni(
            @PathVariable Long dni) {
        return ResponseEntity.ok(
                socioPlanService.obtenerPlanesPendientesPorDni(dni));
    }
}
