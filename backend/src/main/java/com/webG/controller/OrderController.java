package com.webG.controller;

import com.webG.entity.Order;
import com.webG.entity.OrderDetail;
import com.webG.entity.Product;
import com.webG.entity.Usuario;
import com.webG.repository.OrderRepository;
import com.webG.repository.ProductRepository;
import com.webG.repository.OrderDetailRepository;
import com.webG.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  //  @Autowired
   // private OrderService orderService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody List<OrderDetailDTO> orderDetailsDTO, @RequestParam("userId") Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Order order = new Order();
        order.setUsuario(usuario);
        order.setFechaCompra(new java.sql.Date(new Date().getTime()));
        order.setEstado("PENDIENTE");
        order = orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailDTO detailDTO : orderDetailsDTO) {
            if (detailDTO.getProductId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El productId no puede ser null");
            }
            Product product = productRepository.findById(detailDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con ID: " + detailDTO.getProductId()));

            // Verificar si hay suficiente stock antes de añadir al detalle de la orden
            if (product.getStock() < detailDTO.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay suficiente stock para el producto con ID: " + detailDTO.getProductId());
            }

            // Reducir el stock del producto
            product.setStock(product.getStock() - detailDTO.getCantidad());
            productRepository.save(product);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setCantidad(detailDTO.getCantidad());
            detail.setPrecio(detailDTO.getPrecio());
            orderDetails.add(detail);
        }
        orderDetailRepository.saveAll(orderDetails);

        return ResponseEntity.ok(order);
    }



    // Obtener todas las órdenes de un usuario
    @GetMapping("/user/{userId}")
    public List<Order> getAllOrdersByUser(@PathVariable Long userId) {
        return orderRepository.findAllByUsuarioId(userId);
    }
    
    
    @GetMapping("/{orderId}/details")
    public ResponseEntity<?> getOrderDetailsWithProduct(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        List<OrderDetailConProductDTO> orderDetailsConProductDTO = orderDetailRepository.findAllByOrderId(orderId)
            .stream()
            .map(detail -> {
            	 Product product = detail.getProduct();
                 return new OrderDetailConProductDTO(
                     detail.getId(),
                     product.getId(),
                     product.getName(),
                     product.getDescription(),
                     product.getPrice(),
                     detail.getCantidad(),
                     detail.getPrecio()
                 );
             })
             .collect(Collectors.toList());

         return ResponseEntity.ok(orderDetailsConProductDTO);
     }



    // Cambiar el estado de una orden
    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestParam("status") String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        order.setEstado(status);
        return orderRepository.save(order);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if(orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

  

}
