package com.projectFit.fit_api.config;

import com.projectFit.fit_api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ReservaScheduler {

    private final ReservaRepository reservaRepository;

    @Scheduled(cron = "0 59 23 * * SAT")
    @Transactional
    public void resetearReserva(){
        reservaRepository.deleteAll();
        System.out.println("Reseteo de reservas para la semana siguiente");
    }
}
