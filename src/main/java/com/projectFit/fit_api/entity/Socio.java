package com.projectFit.fit_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "nombre obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "apellido obligatorio")
    @Column(nullable = false)
    private String apellido;

    @NotNull(message = "dni obligatorio")
    @Column(unique = true, nullable = false)
    private Long dni;

    @Column(unique = true)
    private String auth0Id;

    @NotBlank(message = "email obligatorio")
    @Email(message = "Email inválido")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "telefono obligatorio")
    @Column(nullable = false)
    private String telefono;

    //@Column(nullable = false)
    private LocalDate fechaNacimiento;

    //RELACIONES
    @OneToMany(mappedBy = "socio", fetch = FetchType.LAZY)
    private List<SocioPlan> socioPlan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;
}
