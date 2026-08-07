package com.projectFit.fit_api.mappers;

import com.projectFit.fit_api.dto.DistribucionIngresoDTO;
import com.projectFit.fit_api.dto.PagoResponseDTO;
import com.projectFit.fit_api.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    default DistribucionIngresoDTO toDistribucionDTO(Object[] objeto) {
        if (objeto == null) return null;
        DistribucionIngresoDTO dto = new DistribucionIngresoDTO();
        dto.setMetodo((String) objeto[0]);
        dto.setTotal(new java.math.BigDecimal(objeto[1].toString()));
        return dto;
    }
    //Entidad Pago a PagoResponseDTO
    @Mapping(source = "socioPlan.socio.nombre", target = "nombreSocio")
    @Mapping(source = "socioPlan.socio.apellido", target = "apellidoSocio")
    @Mapping(source = "socioPlan.plan.nombrePlan", target = "nombrePlan")
    @Mapping(source = "metodoAbonado", target = "metodoAbonado")
    PagoResponseDTO toResponse(Pago pago);
}
