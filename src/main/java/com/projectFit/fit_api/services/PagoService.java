package com.projectFit.fit_api.services;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.projectFit.fit_api.dto.PagoRequestDTO;
import com.projectFit.fit_api.dto.PagoResponseDTO;
import com.projectFit.fit_api.entity.Pago;
import com.projectFit.fit_api.entity.SocioPlan;
import com.projectFit.fit_api.exception.BusinessException;
import com.projectFit.fit_api.exception.ResourceNotFoundException;
import com.projectFit.fit_api.mappers.PagoMapper;
import com.projectFit.fit_api.repository.PagoRepository;
import com.projectFit.fit_api.repository.SocioPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {

    private final PagoRepository pagoRepository;
    private final SocioPlanRepository socioPlanRepository;
    private final SocioPlanService socioPlanService;
    private final PagoMapper pagoMapper;

    private final String mpAccessToken = "APP_USR-1756198843857397-071316-5dd3feb3465105c2fe46030449156be4-3538197051";

    //Creacion de preferencia de pago para el socioPlan
    public String creacionPreferenciaPago(Long socioPlanId){
        MercadoPagoConfig.setAccessToken(mpAccessToken);
        SocioPlan socioPlan = socioPlanRepository.findById(socioPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("SocioPlan no encontrado"));

        if (!socioPlan.getEstadoSocioPlan()
                .getNombreEstadoSocioPlan().equals("Pendiente")) {
            throw new BusinessException(
                    "Este plan no está pendiente de pago");
        }

        //Creamos el item para la pantalla de MP
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title("Plan: " + socioPlan.getPlan().getNombrePlan())
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(socioPlan.getPlan().getPrecio())
                .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(item);

        PreferenceRequest preferenceRequest  = PreferenceRequest.builder()
                .items(items)
                .notificationUrl("https://mountable-maroon-breezy.ngrok-free.dev/vexium/pagos/webhook/mercadopago") //url del webhook cuando el pago se confirme
                .externalReference(socioPlanId.toString())
                .backUrls(PreferenceBackUrlsRequest.builder()
                        .success("https://www.google.com/") //redireccion si el pago fue exitoso
                        .failure("https://www.google.com/")
                        .pending("https://www.google.com/")
                        .build())
                .autoReturn("approved")
                .build();

        try {
            //Le mandamos la preferencia a Mercado pago y obtenemos la url del pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getInitPoint(); //url donde va a pagar
        } catch (MPException | MPApiException e) {
            e.printStackTrace();
            throw new BusinessException("Error al crear preferencia de pago: " + e.getMessage());
        }
    }

    //PAGO EN EFECTIVO, REALIZADO POR RECEPCIONISTA
    public PagoResponseDTO realizarPagoEfectivo(PagoRequestDTO pagoRequestDTO){
        SocioPlan socioPlan = socioPlanRepository.findById(pagoRequestDTO.getSocioPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("SocioPlan no encontrado"));

        // Verificar que esté PENDIENTE
        if (!socioPlan.getEstadoSocioPlan()
                .getNombreEstadoSocioPlan().equals("Pendiente")) {
            throw new BusinessException(
                    "Este plan no está pendiente de pago");
        }

        Pago pago = new Pago();
        pago.setSocioPlan(socioPlan);
        pago.setMetodoAbonado("Efectivo");
        pago.setFechaHoraPago(LocalDateTime.now());
        pago.setMontoPago(pagoRequestDTO.getMontoPago());
        pago.setMpPaymentId(null); //null cuando es efectivo el metodo abonado
        pagoRepository.save(pago);
        //Una vez realizado el pago se debe activar el plan del socio
        socioPlanService.activarPlan(socioPlan.getId());
        return pagoMapper.toResponse(pago);
    }

    //PAGO CON MERCADO PAGO REALIZADO POR LA APP
    public void procesarWebhookMercadoPago(String mpPaymentId){
        MercadoPagoConfig.setAccessToken(mpAccessToken);
        pagoRepository.findByMpPaymentId(mpPaymentId).ifPresent(p -> {
            throw new BusinessException("Pago ya procesado");
        });

        try{
            //Consultamos el detalle del pago
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(mpPaymentId));

            //Se procesa pagos aprobados
            if (!"approved".equals(payment.getStatus())) {
                return;
            }
            // Recuperamos el socioPlanId del externalReference
            Long socioPlanId = Long.parseLong(payment.getExternalReference());
            SocioPlan socioPlan = socioPlanRepository.findById(socioPlanId)
                    .orElseThrow(() -> new ResourceNotFoundException("SocioPlan no encontrado"));

            Pago pago = new Pago();
            pago.setSocioPlan(socioPlan);
            pago.setMetodoAbonado("MercadoPago");
            pago.setMontoPago(payment.getTransactionAmount());
            pago.setFechaHoraPago(LocalDateTime.now());
            pago.setMpPaymentId(mpPaymentId);
            pagoRepository.save(pago);
            //Una vez realizado el pago se debe activar el plan del socio
            socioPlanService.activarPlan(socioPlan.getId());
        }catch (MPException | MPApiException e){
            throw new BusinessException(
                    "Error al procesar webhook: " + e.getMessage());
        }

    }
}
