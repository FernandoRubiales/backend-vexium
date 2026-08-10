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
        // Buscamos todas las actividades por los IDs recibidos
        List<TipoActividad> actividades = tipoActividadRepository.findAllById(planRequestDTO.getTiposActividadesIds());
        if (actividades.isEmpty()) {
            throw new BusinessException("Debe seleccionar al menos una actividad válida");
        }

        Plan plan = planMapper.toEntity(planRequestDTO);
        plan.setTiposActividades(actividades);
        int clasesPrecisas = (int) Math.ceil((planRequestDTO.getDiasPorSemana() * 30.0) / 7.0);
        plan.setClasesIncluidas(clasesPrecisas);
        Plan planGuardado = planRepository.save(plan);

        return planMapper.toResponse(planGuardado);

    }

    //UPDATE PLAN
    public PlanResponseDTO actualizarPlan(Long id, PlanRequestDTO planRequestDTO){
        Plan planExistente = planRepository.findByIdAndFechaHoraBajaPlanIsNull(id).
                orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        List<TipoActividad> actividades = tipoActividadRepository.findAllById(planRequestDTO.getTiposActividadesIds());
        if (actividades.isEmpty()) {
            throw new BusinessException("Debe seleccionar al menos una actividad válida");
        }

        planExistente.setNombrePlan(planRequestDTO.getNombrePlan());
        planExistente.setDescripcion(planRequestDTO.getDescripcion());
        planExistente.setDiasPorSemana(planRequestDTO.getDiasPorSemana());
        planExistente.setPrecio(planRequestDTO.getPrecio());
        int clasesPrecisas = (int) Math.ceil((planRequestDTO.getDiasPorSemana() * 30.0) / 7.0);
        planExistente.setClasesIncluidas(clasesPrecisas);
        planExistente.setTiposActividades(actividades);

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
