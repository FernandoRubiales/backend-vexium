package com.projectFit.fit_api.config;

import com.projectFit.fit_api.entity.EstadoSocioPlan;
import com.projectFit.fit_api.entity.Rol;
import com.projectFit.fit_api.repository.EstadoSocioPlanRepository;
import com.projectFit.fit_api.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final EstadoSocioPlanRepository estadoSocioPlanRepository;
    private final RolRepository rolRepository;

    @Override
    public void run(ApplicationArguments args) {
        inicializarEstadosSocioPlan();
        inicializarRoles();
    }

    private void inicializarEstadosSocioPlan() {
        if (estadoSocioPlanRepository.count() == 0) {
            estadoSocioPlanRepository.save(new EstadoSocioPlan(null,"Pendiente"));
            estadoSocioPlanRepository.save(new EstadoSocioPlan(null,"Activo"));

            System.out.println("Estados de SocioPlan inicializados");
        }
    }

    private void inicializarRoles() {
        if (rolRepository.count() == 0) {
            rolRepository.save(new Rol(null,"ADMIN"));
            rolRepository.save(new Rol(null,"SOCIO"));
            rolRepository.save(new Rol(null,"RECEPCIONISTA"));
            System.out.println("Roles inicializados");
        }
    }

}
