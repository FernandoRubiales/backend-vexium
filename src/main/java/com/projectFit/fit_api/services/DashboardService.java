package com.projectFit.fit_api.services;

import com.projectFit.fit_api.dto.DashboardAdminResponseDTO;
import com.projectFit.fit_api.dto.DistribucionIngresoDTO;
import com.projectFit.fit_api.dto.RankingClaseDTO;
import com.projectFit.fit_api.mappers.ClaseMapper;
import com.projectFit.fit_api.mappers.PagoMapper;
import com.projectFit.fit_api.repository.ClaseRepository;
import com.projectFit.fit_api.repository.PagoRepository;
import com.projectFit.fit_api.repository.SocioPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final PagoRepository pagoRepository;
    private final ClaseRepository claseRepository;
    private final SocioPlanRepository socioPlanRepository;

    private final PagoMapper pagoMapper;
    private final ClaseMapper claseMapper;

    @Transactional(readOnly = true)
    public DashboardAdminResponseDTO obtenerDatosDashboardAdmin() {

        BigDecimal ingresosMes = pagoRepository.obtenerIngresosDelMes();
        Integer sociosActivos = socioPlanRepository.contarSociosActivos();

        List<DistribucionIngresoDTO> distribucion = pagoRepository.obtenerDistribucionIngresosMes()
                .stream()
                .map(pagoMapper::toDistribucionDTO)
                .toList();

        List<RankingClaseDTO> ranking = claseRepository.obtenerRankingClases()
                .stream()
                .map(claseMapper::toRankingDTO)
                .toList();

        return new DashboardAdminResponseDTO(ingresosMes, sociosActivos, distribucion, ranking);
    }
}