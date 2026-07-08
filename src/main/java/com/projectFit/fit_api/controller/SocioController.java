package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.SocioRequestDTO;
import com.projectFit.fit_api.dto.SocioResponseDTO;
import com.projectFit.fit_api.services.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    //CREATE SOCIO
    @PostMapping
    public ResponseEntity<SocioResponseDTO> crearSocio(@Valid @RequestBody SocioRequestDTO socioRequestDTO){
        SocioResponseDTO socioResponse = socioService.crearSocio(socioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(socioResponse);

    }

    //UPDATE SOCIO
    @PutMapping("/{id}")
    public ResponseEntity<SocioResponseDTO> actualizarSocio(
            @PathVariable Long id,
            @Valid @RequestBody SocioRequestDTO socioRequestDTO) {
        return ResponseEntity.ok(socioService.actualizarSocio(id, socioRequestDTO));
    }

    //DELETE SOCIO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSocio(@PathVariable Long id) {
        socioService.eliminarSocio(id);
        return ResponseEntity.noContent().build();
    }

    //GET SOCIO ID
    @GetMapping("/{id}")
    public ResponseEntity<SocioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(socioService.obtenerPorId(id));
    }

    //GET ALL SOCIOS
    @GetMapping
    public ResponseEntity<List<SocioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(socioService.obtenerTodos());
    }

    //GENERAR IMAGEN QR
    @GetMapping("/qr")
    public ResponseEntity<byte[]> obtenerQr(
            @AuthenticationPrincipal Jwt jwt) throws Exception {
        byte[] qrImage = socioService.generarImagenQr(jwt.getSubject());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }

}
