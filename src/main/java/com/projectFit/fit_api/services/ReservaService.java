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
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        // Validar que la clase no sea de un día pasado o con horario pasado
        int diaActualNumero = LocalDate.now().getDayOfWeek().getValue();
        int diaClaseNumero = numeroDiaSemana(clase.getDiaSemana());

        if (diaClaseNumero < diaActualNumero) {
            throw new BusinessException(
                    "No podés reservar una clase que ya pasó");
        }

        if (diaClaseNumero == diaActualNumero &&
                LocalTime.now().isAfter(clase.getHoraInicio())) {
            throw new BusinessException(
                    "No podés reservar una clase cuyo horario ya pasó");
        }
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
                socio.getId(), clase.getDiaSemana(), clase.getTipoActividad().getId());

        if(!reservasMismaActividad.isEmpty()){
            throw new BusinessException("Ya tenés una reserva de " + clase.getTipoActividad().getNombreTipoActividad() + " el dia " + clase.getDiaSemana());
        }
        //Validar que en el mismo horario no tenga otra reserva ese dia
        List<Reserva> reservasMismoHorario = reservaRepository.reservasMismoHorario(
                socio.getId(),clase.getDiaSemana(), clase.getHoraFin().toString(), clase.getHoraInicio().toString());

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

        //Descontamos  el cupo del plan del socio
        socioPlan.setClasesDisponibles(socioPlan.getClasesDisponibles() - 1);
        socioPlanRepository.save(socioPlan);

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }
    //Conversion
    private int numeroDiaSemana(String diaSemanaEspanol) {
        Map<String, Integer> dias = Map.of(
                "Lunes", 1,
                "Martes", 2,
                "Miercoles", 3,
                "Jueves", 4,
                "Viernes", 5,
                "Sábado", 6,
                "Domingo", 7
        );
        Integer numero = dias.get(diaSemanaEspanol);
        if (numero == null) {
            throw new BusinessException("Día de semana inválido: " + diaSemanaEspanol);
        }
        return numero;
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

        String diaActual =  LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es"));
        String diaFormateado = diaActual.substring(0,1).toUpperCase() + diaActual.substring(1);

        if(reserva.getClase().getDiaSemana().equalsIgnoreCase(diaFormateado)){
            LocalTime horaMaximaPermitida = reserva.getClase().getHoraInicio().minusMinutes(10);
            if (LocalTime.now().isAfter(horaMaximaPermitida)) {
                throw new BusinessException("No podés cancelar una reserva faltando menos de 10 minutos para empezar la clase");
            }
        }

        //Si cancela, devolvemos el cupo mensual al socio
        SocioPlan socioPlan = reserva.getSocioPlan();
        socioPlan.setClasesDisponibles(socioPlan.getClasesDisponibles() + 1);
        socioPlanRepository.save(socioPlan);

        reservaRepository.delete(reserva);
    }

    //GET DE MIS RESERVAS
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerMisReservas(String auth0Id){
        Socio socio = socioRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        return reservaRepository.obtenerTodasLasReservasDelSocio(socio.getId())
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    //GET RESERVAS DE UNA CLASE
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerReservasDeClase(Long claseId){
        return reservaRepository.obtenerTodasLasReservasDeClase(claseId)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }
}
