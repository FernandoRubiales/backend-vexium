package com.projectFit.fit_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class SocioResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private Long dni;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
}
