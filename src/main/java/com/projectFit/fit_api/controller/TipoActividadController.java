package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.TipoActividadRequestDTO;
import com.projectFit.fit_api.dto.TipoActividadResponseDTO;
import com.projectFit.fit_api.services.TipoActividadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tipo-actividad")
public class TipoActividadController {

    private final TipoActividadService tipoActividadService;

    //CREATE TipoActividad
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoActividadResponseDTO> crearActividad(@Valid @RequestBody TipoActividadRequestDTO tipoActividadRequestDTO){
        TipoActividadResponseDTO tipoActividadResponse = tipoActividadService.crearActividad(tipoActividadRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoActividadResponse);
    }

    //UPDATE TipoActividad
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TipoActividadResponseDTO> actualizarActividad(@PathVariable Long id, @Valid @RequestBody TipoActividadRequestDTO tipoActividadRequestDTO){
        TipoActividadResponseDTO tipoActividadResponse = tipoActividadService.actualizarActividad(id, tipoActividadRequestDTO);
        return ResponseEntity.ok(tipoActividadResponse);
    }

    //DELETE TipoActividad
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> darDeBajaActividad(@PathVariable Long id){
        tipoActividadService.darDeBajaActividad(id);
        return ResponseEntity.noContent().build();
    }

    //GET TipoActividad por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'SOCIO')")
    public ResponseEntity<TipoActividadResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(tipoActividadService.obtenerPorId(id));
    }

    //GET ALL TipoActividad
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'SOCIO')")
    public ResponseEntity<List<TipoActividadResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(tipoActividadService.obtenerTodas());
    }
}
