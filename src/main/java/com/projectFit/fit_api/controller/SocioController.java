package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.SocioRequestDTO;
import com.projectFit.fit_api.dto.SocioResponseDTO;
import com.projectFit.fit_api.entity.Socio;
import com.projectFit.fit_api.mappers.SocioMapper;
import com.projectFit.fit_api.services.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;
    private final SocioMapper socioMapper;

    //CREATE SOCIO
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<SocioResponseDTO> crearSocio(@Valid @RequestBody SocioRequestDTO socioRequestDTO){
        SocioResponseDTO socioResponse = socioService.crearSocio(socioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(socioResponse);

    }

    //LOG IN
    @GetMapping("/perfil")
    public ResponseEntity<SocioResponseDTO> obtenerPerfil(
            @AuthenticationPrincipal Jwt jwt) {
        Socio socio = socioService.obtenerOCrearSocio(jwt);
        return ResponseEntity.ok(socioMapper.toResponse(socio));
    }

    //Solo admin puede cambiar roles
    @PatchMapping("/{id}/cambiar-rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarRol(
            @PathVariable Long id,
            @RequestParam String nuevoRol) {
        socioService.cambiarRol(id, nuevoRol);
        return ResponseEntity.noContent().build();
    }

    //UPDATE SOCIO
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<SocioResponseDTO> actualizarSocio(
            @PathVariable Long id,
            @Valid @RequestBody SocioRequestDTO socioRequestDTO) {
        return ResponseEntity.ok(socioService.actualizarSocio(id, socioRequestDTO));
    }

    //DELETE SOCIO
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> eliminarSocio(@PathVariable Long id) {
        socioService.eliminarSocio(id);
        return ResponseEntity.noContent().build();
    }

    //GET SOCIO ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<SocioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(socioService.obtenerPorId(id));
    }

    //GET ALL SOCIOS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<List<SocioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(socioService.obtenerTodos());
    }


}
