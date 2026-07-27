package com.projectFit.fit_api.repository;

import com.projectFit.fit_api.entity.Clase;
import com.projectFit.fit_api.entity.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaseRepository extends JpaRepository <Clase,Long> {

    //Cupos disponibles para la clase (cupo maximo - cantidad de reservas realizadas)
    @Query(value = "SELECT c.cupo_maximo - COUNT(r.id) FROM clase c " +
            "LEFT JOIN reserva r ON r.clase_id = c.id " +
            "WHERE c.id = :claseId", nativeQuery = true)
    int cuposDisponibles(@Param("claseId") Long claseId);


    //CLASES DISPONIBLES PARA UN TIPO DE ACTIVIDAD
    @Query(value = "SELECT * FROM clase WHERE tipo_actividad_id = :tipoActividadId"+
    "AND fecha_hora_baja_clase IS NULL", nativeQuery = true)
    List<Clase> clasesDisponiblesPorTipoActividad(@Param("tipoActividadId") Long tipoActividadId);


    //CLASES DONDE EL SOCIO PUEDE RESERVAR SEGUN SU PLAN ACTIVO
    @Query(value = "SELECT DISTINCT c.* FROM clase c " +
            "JOIN tipo_actividad ta ON c.tipo_actividad_id = ta.id " +
            "JOIN plan p ON p.tipo_actividad_id = ta.id " +
            "JOIN socio_plan sp ON sp.plan_id = p.id " +
            "JOIN estado_socio_plan esp ON sp.estado_id = esp.id " +
            "WHERE c.dia_semana = :diaActual " +
            "AND c.fecha_hora_baja_clase IS NULL " +
            "AND ta.requiere_reserva = true " +
            "AND esp.nombre_estado_socio_plan = 'Activo' " +
            "AND sp.socio_id = :socioId " +
            "AND (c.cupo_maximo - (SELECT COUNT(r.id) FROM reserva r " +
            "     WHERE r.clase_id = c.id)) > 0",
            nativeQuery = true)
    List<Clase> clasesDisponiblesParaSocio(@Param("socioId") Long socioId,
                                           @Param("diaActual") String diaActual);

    List<Clase> findByFechaHoraBajaClaseIsNull();
}
