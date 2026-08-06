package com.projectFit.fit_api.mappers;

import com.projectFit.fit_api.dto.SocioPlanResponseDTO;
import com.projectFit.fit_api.entity.SocioPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SocioPlanMapper {

    //Entidad SocioPlan a SocioPlanResponseDTO
    @Mapping(source = "socio.nombre", target = "nombreSocio")
    @Mapping(source = "socio.apellido", target = "apellidoSocio")
    @Mapping(source = "plan.nombrePlan", target = "nombrePlan")
    @Mapping(source = "plan.tipoActividad.nombreTipoActividad", target = "tipoActividad")
    @Mapping(source = "plan.clasesIncluidas", target = "clasesIncluidas")
    @Mapping(source = "estadoSocioPlan.nombreEstadoSocioPlan", target = "estadoSocioPlan")
    @Mapping(source = "plan.precio", target = "precio")
    SocioPlanResponseDTO toResponse(SocioPlan socioPlan);
}
