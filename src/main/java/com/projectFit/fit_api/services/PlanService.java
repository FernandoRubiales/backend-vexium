package com.projectFit.fit_api.services;

import com.projectFit.fit_api.dto.PlanRequestDTO;
import com.projectFit.fit_api.dto.PlanResponseDTO;
import com.projectFit.fit_api.entity.Plan;
import com.projectFit.fit_api.entity.TipoActividad;
import com.projectFit.fit_api.exception.BusinessException;
import com.projectFit.fit_api.exception.ResourceNotFoundException;
import com.projectFit.fit_api.mappers.PlanMapper;
import com.projectFit.fit_api.repository.PlanRepository;
import com.projectFit.fit_api.repository.TipoActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;
    private final TipoActividadRepository tipoActividadRepository;

    //CREATE PLAN
    public PlanResponseDTO crearPlan(PlanRequestDTO planRequestDTO){
        if(planRepository.existsByNombrePlan(planRequestDTO.getNombrePlan())){
            throw new BusinessException("Ya existe un plan con ese nombre");
        }
        TipoActividad tipoActividad = tipoActividadRepository.findById(planRequestDTO.getTipoActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encuentra ese tipo de actividad"));

        Plan plan = planMapper.toEntity(planRequestDTO);
        plan.setClasesIncluidas(planRequestDTO.getDiasPorSemana() * 4 ); //tomamos 4 semanas del mes
        Plan planGuardado = planRepository.save(plan);

        return planMapper.toResponse(planGuardado);

    }

    //UPDATE PLAN
    public PlanResponseDTO actualizarPlan(Long id, PlanRequestDTO planRequestDTO){
        Plan planExistente = planRepository.findByIdAndFechaHoraBajaPlanIsNull(id).
                orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        TipoActividad tipoActividad = tipoActividadRepository.findByIdAndFechaHoraBajaActividadIsNull(planRequestDTO.getTipoActividadId()).
                orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        planExistente.setNombrePlan(planRequestDTO.getNombrePlan());
        planExistente.setDescripcion(planRequestDTO.getDescripcion());
        planExistente.setDiasPorSemana(planRequestDTO.getDiasPorSemana());
        planExistente.setPrecio(planRequestDTO.getPrecio());
        planExistente.setClasesIncluidas(planRequestDTO.getDiasPorSemana()* 4);
        planExistente.setTipoActividad(tipoActividad);

        Plan planGuardado = planRepository.save(planExistente);

        return planMapper.toResponse(planGuardado);
    }

    //DELETE PLAN
    public void darDeBajaPlan(Long id){
        Plan planExistente = planRepository.findByIdAndFechaHoraBajaPlanIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        planExistente.setFechaHoraBajaPlan(LocalDateTime.now());
        planRepository.save(planExistente);
    }

    //GET PLAN por ID
    @Transactional(readOnly = true)
    public PlanResponseDTO obtenerPorId(Long id){
        Plan planExistente = planRepository.findByIdAndFechaHoraBajaPlanIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        return planMapper.toResponse(planExistente);
    }

    //GET ALL PLAN
    @Transactional(readOnly = true)
    public List<PlanResponseDTO> obtenerTodos() {
        return planRepository.findByFechaHoraBajaPlanIsNull()
                .stream()
                .map(planMapper::toResponse)
                .toList();
    }

    //GET ALL PLAN por TipoActividad
    @Transactional(readOnly = true)
    public List<PlanResponseDTO> obtenerPorActividad(Long tipoActividadId) {
        return planRepository.planesActivosPorActividad(tipoActividadId)
                .stream()
                .map(planMapper::toResponse)
                .toList();
    }

}
