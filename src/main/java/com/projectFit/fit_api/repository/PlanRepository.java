package com.projectFit.fit_api.repository;

import com.projectFit.fit_api.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByFechaHoraBajaPlanIsNull();
    Optional<Plan> findByIdAndFechaHoraBajaPlanIsNull(Long id);
    boolean existsByNombrePlan(String nombrePlan);

    @Query(value = "SELECT p.* FROM plan p " +
            "JOIN plan_actividad pa ON p.id = pa.plan_id " +
            "WHERE pa.tipo_actividad_id = :tipoActividadId AND p.fecha_hora_baja_plan IS NULL",
            nativeQuery = true)
    List<Plan> planesActivosPorActividad(@Param("tipoActividadId") Long tipoActividadId);

}
