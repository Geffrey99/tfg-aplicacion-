package com.webG.repository;

import com.webG.entity.OrderDetail;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrderId(Long orderId);
    List<OrderDetail> findByProduct_Id(Long productId);
    // Método para eliminar todos los detalles de una orden específica
    @Transactional
    void deleteAllByOrderId(Long orderId);
}
