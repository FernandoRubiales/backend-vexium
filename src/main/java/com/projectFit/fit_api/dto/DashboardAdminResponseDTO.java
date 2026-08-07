package com.projectFit.fit_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAdminResponseDTO {
    private BigDecimal ingresosDelMes;
    private Integer sociosActivos;
    private List<DistribucionIngresoDTO> distribucionIngresos;
    private List<RankingClaseDTO> rankingClases;
}