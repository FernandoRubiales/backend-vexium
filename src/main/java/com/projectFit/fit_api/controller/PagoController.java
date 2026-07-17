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
    
    //PAGO CON MERCADO PAGO REALIZADO POR LA APP, mp llama a este endpoint
    @PostMapping("/webhook/mercadopago")
    public ResponseEntity<Void> webhookMercadoPago(
            @RequestBody Map<String, Object> payload){

        System.out.println("====== WEBHOOK RECIBIDO ======");
        System.out.println("Payload completo: " + payload);
        //Solo importa las de tipo "payment"
        if ("payment".equals(payload.get("type"))) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            if (data != null && data.get("id") != null) {
                String mpPaymentId = data.get("id").toString();
                System.out.println("Procesando Pago ID Real: " + mpPaymentId);
                pagoService.procesarWebhookMercadoPago(mpPaymentId);
            } else {
                System.out.println("Aviso: El payload no contiene el objeto 'data' o el 'id' del pago.");
            }
        }else{
            System.out.println("Notificación descartada (Type no es 'payment'): " + payload.get("type"));
        }
        return ResponseEntity.ok().build();
    }
}
