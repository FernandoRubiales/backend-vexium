package com.projectFit.fit_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaResponseDTO {

    private Long id;
    private String tipoActividad;
    private String diaSemana;
    private LocalDate fechaClaseReservada;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
