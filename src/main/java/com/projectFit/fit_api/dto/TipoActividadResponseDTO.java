package com.projectFit.fit_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoActividadResponseDTO {

    private Long id;
    private String nombreTipoActividad;
    private String descripcion;
}
