package com.projectFit.fit_api.repository;

import com.projectFit.fit_api.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository <Reserva, Long> {

    // Query para verificar si el socio ya reservo esa clase para esta semana
    @Query(value = "SELECT r.* FROM reserva r " +
            "JOIN socio_plan sp ON r.socio_plan_id = sp.id " +
            "JOIN clase c ON r.clase_id = c.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND r.clase_id = :claseId",
            nativeQuery = true)
    Optional<Reserva> reservaPorSocioyClaseId(@Param("socioId") Long socioId,
                                              @Param("claseId") Long claseId);

    // Query para verificar si el socio tiene reservas del mismo tipo de actividad para el mismo dia
    @Query(value = "SELECT r.* FROM reserva r " +
            "JOIN socio_plan sp ON r.socio_plan_id = sp.id " +
            "JOIN clase c ON r.clase_id = c.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND c.dia_semana = :diaSemana " +
            "AND c.tipo_actividad_id = :tipoActividadId", nativeQuery = true)
    List<Reserva> reservasPorDiayTipoActividad(@Param("socioId") Long socioId,
                                               @Param("diaSemana") String diaSemana,
                                               @Param("tipoActividadId") Long tipoActividadId);

    // Query para verificar si el socio tiene otra reserva en el mismo horario para el mismo dia
    @Query(value = "SELECT r.* FROM reserva r " +
            "JOIN socio_plan sp ON r.socio_plan_id = sp.id " +
            "JOIN clase c ON r.clase_id = c.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND c.dia_semana = :diaSemana " +
            "AND c.hora_inicio < CAST(:horaFin AS TIME) " +
            "AND c.hora_fin > CAST(:horaInicio AS TIME)", nativeQuery = true)
    List<Reserva> reservasMismoHorario(@Param("socioId") Long socioId,
                                       @Param("diaSemana") String diaSemana,
                                       @Param("horaInicio") String horaInicio,
                                       @Param("horaFin") String horaFin);

    // Query para buscar todas las reservas del socio de esta semana
    @Query(value = "SELECT r.* FROM reserva r " +
            "JOIN socio_plan sp ON r.socio_plan_id = sp.id " +
            "WHERE sp.socio_id = :socioId", nativeQuery = true)
    List<Reserva> obtenerTodasLasReservasDelSocio(@Param("socioId") Long socioId);

    // Query para buscar todas las reservas de una clase
    @Query(value = "SELECT * FROM reserva " +
            "WHERE clase_id = :claseId", nativeQuery = true)
    List<Reserva> obtenerTodasLasReservasDeClase(@Param("claseId") Long claseId);

    // Query para buscar reserva activa del socio para hoy
    @Query(value = "SELECT r.* FROM reserva r " +
            "JOIN socio_plan sp ON r.socio_plan_id = sp.id " +
            "JOIN clase c ON r.clase_id = c.id " +
            "WHERE sp.socio_id = :socioId " +
            "AND c.dia_semana = :diaSemana " +
            "AND c.hora_inicio <= CAST(:horaActual AS TIME) " +
            "AND c.hora_fin >= CAST(:horaActual AS TIME) " +
            "LIMIT 1", nativeQuery = true)
    Optional<Reserva> reservaActivaPorSocio(@Param("socioId") Long socioId, @Param("diaSemana") String diaHoy, @Param("horaActual") String horaActual);
}
