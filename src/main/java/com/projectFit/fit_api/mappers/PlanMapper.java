package com.projectFit.fit_api.mappers;

import com.projectFit.fit_api.dto.PlanRequestDTO;
import com.projectFit.fit_api.dto.PlanResponseDTO;
import com.projectFit.fit_api.entity.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    //PlanRequestDTO a entidad Plan
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaHoraBajaPlan", ignore = true)
    @Mapping(target = "clasesIncluidas", ignore = true)
    @Mapping(target = "tiposActividades", ignore = true)
    Plan toEntity(PlanRequestDTO planRequestDTO);

    //Entidad Plan a PlanResponseDTO
    @Mapping(target = "tiposActividades", expression = "java(plan.getTiposActividades().stream().map(a -> a.getNombreTipoActividad()).toList())")
    PlanResponseDTO toResponse(Plan plan);

}
