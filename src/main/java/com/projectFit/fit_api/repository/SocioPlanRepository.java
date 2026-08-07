package com.projectFit.fit_api.repository;

import com.projectFit.fit_api.entity.SocioPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocioPlanRepository extends JpaRepository<SocioPlan, Long> {

    // Query para verificar si el socio ya tiene ese plan activo o pendiente
    @Query(value = "SELECT sp.* FROM socio_plan sp " +
            "JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND sp.plan_id = :planId " +
            "AND esp.nombre_estado_socio_plan IN ('Activo', 'Pendiente')",
            nativeQuery = true)
    Optional<SocioPlan> planActivoyPendienteporSocioyPlanId(
            @Param("socioId") Long socioId,
            @Param("planId") Long planId);

    // Query para obtener los planes activos y pendientes del socio
    @Query(value = "SELECT sp.* FROM socio_plan sp " +
            "JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND esp.nombre_estado_socio_plan IN ('Activo', 'Pendiente')",
            nativeQuery = true)
    List<SocioPlan> planesActivosyPendientesBySocioId(@Param("socioId") Long socioId);

    // Query para obtener los planes pendientes del socio
    @Query(value = "SELECT sp.* FROM socio_plan sp " +
            "JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND esp.nombre_estado_socio_plan = 'Pendiente'",
            nativeQuery = true)
    List<SocioPlan> planesPendientesPorSocioId(@Param("socioId") Long socioId);

    // Query para verificar si el socio tiene un plan activo para ese tipo de actividad
    @Query(value = "SELECT sp.* FROM socio_plan sp " +
            "JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "JOIN plan p ON sp.plan_id = p.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND p.tipo_actividad_id = :tipoActividadId " +
            "AND esp.nombre_estado_socio_plan = 'Activo'",
            nativeQuery = true)
    Optional<SocioPlan> planActivoporSocioyActividadId(
            @Param("socioId") Long socioId,
            @Param("tipoActividadId") Long tipoActividadId);

    // Query para buscar plan activo del socio que no requiera reserva de clase
    @Query(value = "SELECT sp.* FROM socio_plan sp " +
            "JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "JOIN plan p ON sp.plan_id = p.id " +
            "JOIN tipo_actividad ta ON p.tipo_actividad_id = ta.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND esp.nombre_estado_socio_plan = 'Activo' " +
            "AND ta.requiere_reserva = false " +
            "AND sp.clases_disponibles > 0 " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<SocioPlan> planActivoporSocio(@Param("socioId") Long socioId);

    // Query para buscar planes activos que vencen en 3 días o menos, o que tengan 1 o 0 clases
    @Query(value = "SELECT sp.* FROM socio_plan sp " +
            "INNER JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "WHERE esp.nombre_estado_socio_plan = 'Activo' " +
            "AND (sp.fecha_vencimiento_socio_plan <= CURRENT_DATE + INTERVAL '3 days' " +
            "OR sp.clases_disponibles <= 1)", nativeQuery = true)
    List<SocioPlan> buscarVencimientosProximos();

    //Query para contar cantidad de socios activos
    @Query(value = "SELECT COUNT(DISTINCT sp.socio_id) FROM socio_plan sp " +
            "INNER JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "WHERE esp.nombre_estado_socio_plan = 'Activo'",
            nativeQuery = true)
    Integer contarSociosActivos();
}
