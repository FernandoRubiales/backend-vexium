package com.projectFit.fit_api.services;

import com.projectFit.fit_api.dto.ClaseRequestDTO;
import com.projectFit.fit_api.dto.ClaseResponseDTO;
import com.projectFit.fit_api.dto.TipoActividadResponseDTO;
import com.projectFit.fit_api.entity.Clase;
import com.projectFit.fit_api.entity.Socio;
import com.projectFit.fit_api.entity.TipoActividad;
import com.projectFit.fit_api.exception.ResourceNotFoundException;
import com.projectFit.fit_api.mappers.ClaseMapper;
import com.projectFit.fit_api.repository.ClaseRepository;
import com.projectFit.fit_api.repository.SocioRepository;
import com.projectFit.fit_api.repository.TipoActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final TipoActividadRepository tipoActividadRepository;
    private final ClaseMapper claseMapper;
    private final SocioRepository socioRepository;

    //CREATE CLASE
    public ClaseResponseDTO crearClase(ClaseRequestDTO claseRequestDTO){
        TipoActividad tipoActividad = tipoActividadRepository.findByIdAndFechaHoraBajaActividadIsNull(claseRequestDTO.getTipoActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        Clase clase = claseMapper.toEntity(claseRequestDTO);
        clase.setTipoActividad(tipoActividad);
        return calcularCuposDisponibles(claseRepository.save(clase));
    }

    //UPDATE CLASE
    public ClaseResponseDTO actualizarClase(Long id, ClaseRequestDTO claseRequestDTO){
        Clase claseExistente = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));

        TipoActividad tipoActividad = tipoActividadRepository.findByIdAndFechaHoraBajaActividadIsNull(claseRequestDTO.getTipoActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        claseExistente.setDiaSemana(claseRequestDTO.getDiaSemana());
        claseExistente.setCupoMaximo(claseRequestDTO.getCupoMaximo());
        claseExistente.setHoraInicio(claseRequestDTO.getHoraInicio());
        claseExistente.setHoraFin(claseRequestDTO.getHoraFin());
        claseExistente.setTipoActividad(tipoActividad);

        return calcularCuposDisponibles(claseRepository.save(claseExistente));
    }

    //DAR DE BAJA UNA CLASE
    public void darDeBajaClase(Long id){
        Clase claseExistente = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));
        claseExistente.setFechaHoraBajaClase(LocalDateTime.now());
        claseRepository.save(claseExistente);
    }

    //GET CLASE POR ID
    @Transactional(readOnly = true)
    public ClaseResponseDTO obtenerPorId(Long id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));
        return calcularCuposDisponibles(clase);
    }

    //GET CLASES POR TIPO ACTIVIDAD
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> obtenerPorTipoActividad(Long tipoActividadId) {
        return claseRepository.clasesDisponiblesPorTipoActividad(tipoActividadId)
                .stream()
                .map(this::calcularCuposDisponibles)
                .toList();
    }

    //Agrega cupos disponibles al dto
    public ClaseResponseDTO calcularCuposDisponibles(Clase clase){
        ClaseResponseDTO claseResponseDTO = claseMapper.toResponse(clase);
        claseResponseDTO.setCuposDisponibles(claseRepository.cuposDisponibles(clase.getId()));
        return claseResponseDTO;
    }

    //GET CLASES DISPONIBLES PARA RESERVAR SEGUN EL PLAN DEL SOCIO
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> obtenerClasesDisponiblesParaSocio(String auth0Id){

        Socio socio = socioRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        String diaActual = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es"));
        String diaFormateado = diaActual.substring(0,1).toUpperCase() + diaActual.substring(1);

        return claseRepository.clasesDisponiblesParaSocio(socio.getId(), diaFormateado)
                .stream()
                .map(this::calcularCuposDisponibles)
                .toList();
    }


    //GET ALL CLASES
    @Transactional(readOnly = true)
    public List<ClaseResponseDTO> obtenerTodas(){
        return claseRepository.findByFechaHoraBajaClaseIsNull()
                .stream()
                .map(this::calcularCuposDisponibles)
                .toList();
    }
}
