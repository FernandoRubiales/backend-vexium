package com.projectFit.fit_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoActividadRequestDTO {

    @NotBlank(message = "Nombre obligatorio")
    private String nombreTipoActividad;

    @NotBlank(message = "Descripción obligatoria")
    private String descripcion;

}
