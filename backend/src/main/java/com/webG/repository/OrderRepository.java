package com.webG.repository;

import com.webG.entity.Order;
import com.webG.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUsuarioId(Long usuarioId);
    List<Order> findAllByUsuario(Usuario usuario);
    List<Order> findAllByUsuarioAndEstadoIn(Usuario usuario, List<String> estados);
}



