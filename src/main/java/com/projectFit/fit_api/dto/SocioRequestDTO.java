package com.projectFit.fit_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class SocioRequestDTO {

    @NotBlank(message = "Nombre obligatorio")
    private String nombre;

    @NotBlank(message = "Apellido obligatorio")
    private String apellido;

    @NotNull(message = "DNI obligatorio")
    private Long dni;

    @NotBlank(message = "Email obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Teléfono obligatorio")
    private String telefono;

    private LocalDate fechaNacimiento;

    @NotBlank(message = "ID de Auth0 obligatorio")
    private String auth0Id;
}
