package com.projectFit.fit_api.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PlanRequestDTO {

    @NotNull(message = "nombre obligatorio")
    private String nombrePlan;

    @NotNull(message = "descripcion obligatorio")
    private String descripcion;

    @NotNull(message = "precio obligatorio")
    private BigDecimal precio;

    @NotNull(message = "dias obligatorio")
    private int diasPorSemana;

    @NotEmpty(message = "Debe seleccionar al menos un tipo de actividad")
    private List<Long> tiposActividadesIds;

}
