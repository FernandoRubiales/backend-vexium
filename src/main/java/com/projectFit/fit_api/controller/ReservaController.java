package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.ReservaRequestDTO;
import com.projectFit.fit_api.dto.ReservaResponseDTO;
import com.projectFit.fit_api.services.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    //REALIZAR UNA RESERVA
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> realizarReserva(
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO,
            @AuthenticationPrincipal Jwt jwt){

        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.realizarReserva(reservaRequestDTO, jwt.getSubject()));
    }

    //CANCELAR UNA RESERVA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id,  @AuthenticationPrincipal Jwt jwt){
        reservaService.cancelarReserva(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    //GET DE MIS RESERVAS
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerMisReservas(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok().body(reservaService.obtenerMisReservas(jwt.getSubject()));
    }

    //GET RESERVAS DE UNA CLASE
    @GetMapping("/clase/{claseId}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerReservasDeClase(@PathVariable Long claseId){
        return ResponseEntity.ok().body(reservaService.obtenerReservasDeClase(claseId));
    }
}
