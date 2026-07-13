package com.projectFit.fit_api.services;

import com.projectFit.fit_api.dto.ReservaRequestDTO;
import com.projectFit.fit_api.dto.ReservaResponseDTO;
import com.projectFit.fit_api.entity.Clase;
import com.projectFit.fit_api.entity.Reserva;
import com.projectFit.fit_api.entity.Socio;
import com.projectFit.fit_api.entity.SocioPlan;
import com.projectFit.fit_api.exception.BusinessException;
import com.projectFit.fit_api.exception.ResourceNotFoundException;
import com.projectFit.fit_api.mappers.ReservaMapper;
import com.projectFit.fit_api.repository.ClaseRepository;
import com.projectFit.fit_api.repository.ReservaRepository;
import com.projectFit.fit_api.repository.SocioPlanRepository;
import com.projectFit.fit_api.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final SocioRepository socioRepository;
    private final ClaseRepository claseRepository;
    private final SocioPlanRepository socioPlanRepository;
    private final ReservaMapper reservaMapper;

    //REALIZAR UNA RESERVA
    public ReservaResponseDTO realizarReserva(ReservaRequestDTO reservaRequestDTO, String auth0Id){
        Socio socio = socioRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        Clase clase = claseRepository.findById(reservaRequestDTO.getClaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));

        SocioPlan socioPlan = socioPlanRepository.planActivoporSocioyActividadId(socio.getId(), clase.getTipoActividad().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No tenés un plan activo para esta actividad"));

        //Validar que le queden clases disponibles del plan para reservar
        if(socioPlan.getClasesDisponibles() <= 0){
            throw new BusinessException("No tenés clases disponibles en este plan");
        }
        //Validar que no haya hecho una reserva en esa clase antes
        reservaRepository.reservaPorSocioyClaseId(socio.getId(), clase.getId())
                .ifPresent(r -> {
            throw new BusinessException("Ya tenés una reserva para esta clase");
        });

        //Validar que no tenga reserva del mismo tipo de actividad, para el mismo dia
        List<Reserva> reservasMismaActividad = reservaRepository.reservasPorDiayTipoActividad(
                socio.getId(), clase.getTipoActividad().getId());

        if(!reservasMismaActividad.isEmpty()){
            throw new BusinessException("Ya tenés una reserva de " + clase.getTipoActividad().getNombreTipoActividad() + " ese día");
        }
        //Validar que en el mismo horario no tenga otra reserva ese dia
        List<Reserva> reservasMismoHorario = reservaRepository.reservasMismoHorario(
                socio.getId(), clase.getHoraFin().toString(), clase.getHoraInicio().toString());

        if (!reservasMismoHorario.isEmpty()) {
            throw new BusinessException("Ya tenés una reserva en ese horario");
        }
        //Validar que hayan cupos disponibles para la clase que quiere reservar
        int cuposDisponibles = claseRepository.cuposDisponibles(clase.getId());
        if(cuposDisponibles <= 0){
            throw new BusinessException("No hay mas cupos disponibles para esta clase");
        }
        Reserva reserva = new Reserva();
        reserva.setClase(clase);
        reserva.setSocioPlan(socioPlan);
        reserva.setFechaHoraReserva(LocalDateTime.now());
        reserva.setFechaClaseReservada(LocalDate.now());

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    //CANCELAR UNA RESERVA
    public void cancelarReserva(Long reservaId, String auth0Id){
        Socio socio = socioRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Verificar que la reserva pertenece al socio
        if (!reserva.getSocioPlan().getSocio().getId().equals(socio.getId())) {
            throw new BusinessException("No podés cancelar esta reserva");
        }

        //Verificar que falte 1 hora o mas para la clase, asi es posible cancelarla
        LocalTime horaMaximaPermitida = reserva.getClase().getHoraInicio().minusHours(1); //restamos 1 hora para el maximo limite
        if(LocalTime.now().isAfter(horaMaximaPermitida)){
            throw new BusinessException("No podes cancelar una reserva faltando menos de 1 hora para empezar la clase");
        }

        reservaRepository.delete(reserva);
    }

    //GET DE MIS RESERVAS
    public List<ReservaResponseDTO> obtenerMisReservas(String auth0Id){
        Socio socio = socioRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        return reservaRepository.obtenerTodasLasReservasDelSocio(socio.getId())
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    //GET RESERVAS DE UNA CLASE
    public List<ReservaResponseDTO> obtenerReservasDeClase(Long claseId){
        return reservaRepository.obtenerTodasLasReservasDeClase(claseId)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }
}
