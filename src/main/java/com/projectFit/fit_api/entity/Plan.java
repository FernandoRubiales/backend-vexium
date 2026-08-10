package com.projectFit.fit_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "descripcion obligatoria")
    private String descripcion;

    @NotNull(message = "nombre obligatorio")
    @Column(nullable = false)
    private String nombrePlan;

    private LocalDateTime fechaHoraBajaPlan;

    @NotNull(message = "precio obligatorio")
    @Column(nullable = false)
    private BigDecimal precio;

    @NotNull(message = "dias obligatorio")
    @Column(nullable = false)
    private int diasPorSemana;

    @Column(nullable = false)
    private int clasesIncluidas;

    //RELACIONES
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "plan_actividad",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_actividad_id")
    )
    private List<TipoActividad> tiposActividades;


}
