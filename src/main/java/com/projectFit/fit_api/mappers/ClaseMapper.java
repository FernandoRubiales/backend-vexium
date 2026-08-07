package com.projectFit.fit_api.mappers;

import com.projectFit.fit_api.dto.ClaseRequestDTO;
import com.projectFit.fit_api.dto.ClaseResponseDTO;
import com.projectFit.fit_api.dto.RankingClaseDTO;
import com.projectFit.fit_api.entity.Clase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaseMapper {

    // Adentro de tu interface ClaseMapper
    default RankingClaseDTO toRankingDTO(Object[] objeto) {
        if (objeto == null) return null;
        RankingClaseDTO dto = new RankingClaseDTO();
        dto.setActividad((String) objeto[0]);
        dto.setCantidadReservas(Integer.parseInt(objeto[1].toString()));
        return dto;
    }
    //ClaseRequestDTO a Entidad Clase
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaHoraBajaClase", ignore = true)
    @Mapping(target = "tipoActividad", ignore = true)
    Clase toEntity(ClaseRequestDTO claseRequestDTO);

    //Entidad Clase a ClaseResponseDTO
    @Mapping(source = "tipoActividad.nombreTipoActividad", target = "nombreTipoActividad")
    @Mapping(target = "cuposDisponibles", ignore = true) //se ignora debido a que es un valor dinamico por las reservas, no pertenece a la entidad
    ClaseResponseDTO toResponse(Clase clase);
}
