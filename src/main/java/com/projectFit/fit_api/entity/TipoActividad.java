package com.projectFit.fit_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TipoActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "descripcion obligatoria")
    @Column(nullable = false)
    private String descripcion;

    @NotBlank(message = "nombre obligatorio")
    @Column(nullable = false)
    private String nombreTipoActividad;

    private LocalDateTime fechaHoraBajaActividad;
}
