package com.projectFit.fit_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class SocioPlanResponseDTO {

    private Long id;
    private String nombreSocio;
    private String apellidoSocio;
    private String nombrePlan;
    private String tipoActividad;
    private int clasesDisponibles;
    private int clasesIncluidas;
    private LocalDateTime fechaInicioSocioPlan;
    private LocalDateTime fechaVencimientoSocioPlan;
    private String estadoSocioPlan;
    private BigDecimal precio;
}
