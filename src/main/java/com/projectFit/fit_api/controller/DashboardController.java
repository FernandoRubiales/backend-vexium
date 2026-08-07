package com.projectFit.fit_api.controller;

import com.projectFit.fit_api.dto.DashboardAdminResponseDTO;
import com.projectFit.fit_api.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAdminResponseDTO> obtenerDashboardAdmin() {
        return ResponseEntity.ok(dashboardService.obtenerDatosDashboardAdmin());
    }
}