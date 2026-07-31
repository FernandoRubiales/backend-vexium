package com.projectFit.fit_api.repository;

import com.projectFit.fit_api.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    //Query para buscar pago por el ID de mercado pago
    @Query(value = "SELECT * FROM pago WHERE mp_payment_id = :mpPaymentId", nativeQuery = true)
    Optional<Pago> findByMpPaymentId(@Param("mpPaymentId") String mpPaymentId);

    //Query para buscar todos los pagos realizados por el socio
    @Query(value = "SELECT p.* FROM pago p " +
            "JOIN socio_plan sp ON p.socio_plan_id = sp.id " +
            "WHERE sp.socio_id = :socioId " +
            "ORDER BY p.fecha_hora_pago DESC",
            nativeQuery = true)
    List<Pago> findHistorialPagosBySocioId(@Param("socioId") Long socioId);

    //Query para traer todos los pagos por fecha descendente
    @Query(value = "SELECT p.* FROM pago p " +
            "JOIN socio_plan sp ON p.socio_plan_id = sp.id " +
            "JOIN socio s ON sp.socio_id = s.id " +
            "ORDER BY p.fecha_hora_pago DESC",
            countQuery = "SELECT COUNT(*) FROM pago",
            nativeQuery = true)
    Page<Pago> findAllPagos(Pageable pageable);
}

