package com.projectFit.fit_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class SocioUpdateDTO {
    @NotBlank(message = "Nombre obligatorio")
    private String nombre;

    @NotBlank(message = "Apellido obligatorio")
    private String apellido;

    @NotNull(message = "DNI obligatorio")
    private Long dni;

    @NotBlank(message = "Teléfono obligatorio")
    private String telefono;

    private LocalDate fechaNacimiento;
}
