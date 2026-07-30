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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    //Iniciar el pago con Mercado Pago
    @PostMapping("/checkout/{socioPlanId}")
    @PreAuthorize("hasRole('SOCIO')")
    public ResponseEntity<String> iniciarPagoMp(@PathVariable Long socioPlanId, @AuthenticationPrincipal Jwt jwt){

        String urlPago = pagoService.creacionPreferenciaPago(socioPlanId);
        return ResponseEntity.ok(urlPago);
    }

    //PAGO EN EFECTIVO, REALIZADO POR RECEPCIONISTA
    @PostMapping("/efectivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<PagoResponseDTO> realizarPagoEfectivo(
            @Valid @RequestBody PagoRequestDTO pagoRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.realizarPagoEfectivo(pagoRequestDTO));
    }

    @PostMapping("/webhook/mercadopago")
    public ResponseEntity<Void> webhookMercadoPago(
            @RequestBody Map<String, Object> payload){

        System.out.println("====== WEBHOOK RECIBIDO ======");
        System.out.println("Payload completo: " + payload);

        try {
            String mpPaymentId = null;

            String type = (String) payload.get("type");
            String topic = (String) payload.get("topic");

            if ("payment".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                if (data != null && data.get("id") != null) {
                    mpPaymentId = data.get("id").toString();
                }
            } else if ("payment".equals(topic)) {
                Object resource = payload.get("resource");
                if (resource != null) {
                    mpPaymentId = resource.toString();
                }
            }
            if (mpPaymentId != null) {
                System.out.println("Procesando Pago ID Real: " + mpPaymentId);
                pagoService.procesarWebhookMercadoPago(mpPaymentId);
            } else {
                System.out.println("Notificación recibida pero sin ID de pago directo.");
            }
        } catch (Exception e) {
            // Esto evita el 502 Bad Gateway y te imprime el error real en tu consola de Spring Boot
            System.err.println("ERROR CRÍTICO EN WEBHOOK: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }

    //GET DE TODOS LOS PAGOS DEL SOCIO PARA HISTORIAL
    @GetMapping("/mis-pagos")
    @PreAuthorize("hasRole('SOCIO')")
    public ResponseEntity<List<PagoResponseDTO>> obtenerHistorial(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pagoService.obtenerHistorialPagos(jwt.getSubject()));
    }
}
