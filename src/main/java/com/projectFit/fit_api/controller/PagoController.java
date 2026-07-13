package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.PagoRequestDTO;
import com.projectFit.fit_api.dto.PagoResponseDTO;
import com.projectFit.fit_api.entity.Pago;
import com.projectFit.fit_api.services.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    //PAGO EN EFECTIVO, REALIZADO POR RECEPCIONISTA
    @PostMapping("/efectivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<PagoResponseDTO> realizarPagoEfectivo(
            @Valid @RequestBody PagoRequestDTO pagoRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.realizarPagoEfectivo(pagoRequestDTO));
    }
    
    //PAGO CON MERCADO PAGO REALIZADO POR LA APP, mp llama a este endpoint
    @PostMapping("/webhook/mercadopago")
    public ResponseEntity<Void> webhookMercadoPago(@RequestBody Map<String, Object> payload){
        if (payload.get("id") == null || payload.get("external_reference") == null) {
            return ResponseEntity.badRequest().build();
        }
        String mpPaymentId = payload.get("id").toString();
        Long socioPlanId = Long.parseLong(
                payload.get("external_reference").toString());
        pagoService.procesarWebhookMercadoPago(mpPaymentId, socioPlanId);
        return ResponseEntity.ok().build();
    }

}
