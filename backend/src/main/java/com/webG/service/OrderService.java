package com.webG.service;


import com.webG.entity.Order;
import com.webG.entity.OrderDetail;
import com.webG.entity.Usuario;
import com.webG.repository.OrderRepository;
import com.webG.repository.OrderDetailRepository;
import com.webG.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Order createOrder(Long userId, List<OrderDetail> orderDetails) {
        Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Order order = new Order();
        order.setUsuario(usuario);
        order.setFechaCompra(new java.sql.Date(new Date().getTime()));

        order.setEstado("PENDIENTE");
        order = orderRepository.save(order);

        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
            orderDetailRepository.save(detail);
        }

        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        order.setEstado(status);
        return orderRepository.save(order);
    }

    
}
