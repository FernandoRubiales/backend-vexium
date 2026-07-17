package com.projectFit.fit_api.mappers;

import com.projectFit.fit_api.dto.SocioRequestDTO;
import com.projectFit.fit_api.dto.SocioResponseDTO;
import com.projectFit.fit_api.entity.Socio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SocioMapper {

    //SocioRequestDTO a Entidad Socio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "socioPlan", ignore = true)
    Socio toEntity(SocioRequestDTO socioRequestDTO);

    //Entidad Socio  a SocioResponseDTO
    @Mapping(source = "rol.nombreRol", target = "nombreRol")
    SocioResponseDTO toResponse(Socio socio);

}
