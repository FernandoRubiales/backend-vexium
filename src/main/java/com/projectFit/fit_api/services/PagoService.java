package com.projectFit.fit_api.services;

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

@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {

    private final PagoRepository pagoRepository;
    private final SocioPlanRepository socioPlanRepository;
    private final SocioPlanService socioPlanService;
    private final PagoMapper pagoMapper;

    //PAGO EN EFECTIVO, REALIZADO POR RECEPCIONISTA
    public PagoResponseDTO realizarPagoEfectivo(PagoRequestDTO pagoRequestDTO){
        SocioPlan socioPlan = socioPlanRepository.findById(pagoRequestDTO.getSocioPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("SocioPlan no encontrado"));

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
    public void procesarWebhookMercadoPago(String mpPaymentId, Long socioPlanId){
        pagoRepository.findByMpPaymentId(mpPaymentId).ifPresent(p -> {
            throw new BusinessException("Pago ya procesado");
        });
        SocioPlan socioPlan = socioPlanRepository.findById(socioPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("SocioPlan no encontrado"));

        Pago pago = new Pago();
        pago.setSocioPlan(socioPlan);
        pago.setMetodoAbonado("MercadoPago");
        pago.setMontoPago(socioPlan.getPlan().getPrecio());
        pago.setFechaHoraPago(LocalDateTime.now());
        pago.setMpPaymentId(mpPaymentId);

        pagoRepository.save(pago);
        //Una vez realizado el pago se debe activar el plan del socio
        socioPlanService.activarPlan(socioPlan.getId());
    }
}
