package com.projectFit.fit_api.mappers;

import com.projectFit.fit_api.dto.ReservaResponseDTO;
import com.projectFit.fit_api.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(source = "socioPlan.socio.nombre", target = "nombreSocio")
    @Mapping(source = "socioPlan.socio.apellido", target = "apellidoSocio")
    @Mapping(source = "clase.tipoActividad.nombreTipoActividad", target = "tipoActividad")
    @Mapping(source = "clase.diaSemana", target = "diaSemana")
    @Mapping(source = "clase.horaInicio", target = "horaInicio")
    @Mapping(source = "clase.horaFin", target = "horaFin")
    ReservaResponseDTO toResponse(Reserva reserva);
}
