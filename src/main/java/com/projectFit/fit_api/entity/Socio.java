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
    private String nombre;
    private String apellido;

    @Column(unique = true)
    private Long dni;

    @Column(unique = true)
    private String auth0Id;

    @NotBlank(message = "email obligatorio")
    @Email(message = "Email inválido")
    @Column(nullable = false)
    private String email;


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
