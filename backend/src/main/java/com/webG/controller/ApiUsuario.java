package com.webG.controller;

import com.webG.entity.Localidad;
import com.webG.entity.Order;
import com.webG.entity.Usuario;
import com.webG.repository.LocalidadRepository;
import com.webG.repository.OrderDetailRepository;
import com.webG.repository.OrderRepository;
import com.webG.repository.UsuarioRepository;


//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/usuario")
public class ApiUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private LocalidadRepository localidadRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired 
    private OrderDetailRepository orderDetailRepository;

    //Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
        	
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    
    @PostMapping("/register")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        // Verifica si el correo electrónico ya existe
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            return new ResponseEntity<>("El correo electrónico ya está registrado.", HttpStatus.BAD_REQUEST);
        }

        usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));

        usuario.setRol("USER");

        // Guarda el nuevo usuario
        try {
            Usuario nuevoUsuario = usuarioRepository.save(usuario);
            // No se incluye la contraseña en la respuesta -- Ok
            nuevoUsuario.setPassword(null);

            //TODO -NO ME FUNCIONA- NO IMPLEMENTADO
            //  SimpleMailMessage mailMessage = new SimpleMailMessage();
            //  mailMessage.setTo(usuario.getEmail());
            //  mailMessage.setSubject("Bienvenido a Nuestra Aplicación");
            // mailMessage.setText("Hola " + usuario.getNombre() + ",\n\nTu registro se ha completado con éxito.\n\nSaludos,\nEl Equipo de la Aplicación");
            // mailSender.send(mailMessage);

            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Ocurrió un error al registrar el usuario.", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElse(null);
        if (usuarioExistente != null) {
            // Comprueba y actualiza solo los campos proporcionados del 'usuario'
            if (usuario.getNombre() != null) {
                usuarioExistente.setNombre(usuario.getNombre());
            }
            if (usuario.getApellido() != null) {
                usuarioExistente.setApellido(usuario.getApellido());
            }
            if (usuario.getEmail() != null) {
                usuarioExistente.setEmail(usuario.getEmail());
            }
            if (usuario.getPassword() != null) {
                usuarioExistente.setPassword(usuario.getPassword());
            }
            if (usuario.getDireccionEnvio() != null) {
                usuarioExistente.setDireccionEnvio(usuario.getDireccionEnvio());
            }
            if (usuario.getMetodoPago() != null) {
                usuarioExistente.setMetodoPago(usuario.getMetodoPago());
            }
          //  if (usuario.getRol() != null) {
            //    usuarioExistente.setRol(usuario.getRol());
           // }
         // Actualiza la localidad solo si se proporciona un ID válido
            if (usuario.getLocalidad() != null && usuario.getLocalidad().getId() != null) {
                Localidad localidad = localidadRepository.findById(usuario.getLocalidad().getId()).orElse(null);
                if (localidad != null) {
                    usuarioExistente.setLocalidad(localidad);
                } else {
                    // Si la localidad no existe, devuelve un error
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarUsuario(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                
                // Obtener todas las órdenes del usuario
                List<Order> ordenesDelUsuario = orderRepository.findAllByUsuario(usuario);

                // Comprobar si el usuario tiene órdenes en estado 'PENDIENTE' o 'ENVIADO'
                boolean tieneOrdenesNoEliminables = ordenesDelUsuario.stream()
                    .anyMatch(order -> Arrays.asList("PENDIENTE", "Enviado").contains(order.getEstado()));

                if (!tieneOrdenesNoEliminables) {
                    // No hay órdenes en estado 'PENDIENTE' o 'ENVIADO', se puede eliminar el usuario
                    // Eliminar los detalles de las órdenes y luego las órdenes
                    ordenesDelUsuario.forEach(order -> {
                        if (Arrays.asList("Recibido", "Cancelado").contains(order.getEstado())) {
                            // Eliminar los detalles de la orden
                            orderDetailRepository.deleteAllByOrderId(order.getId());
                            // Eliminar la orden
                            orderRepository.delete(order);
                        }
                    });
                    // Eliminar el usuario
                    usuarioRepository.delete(usuario);
                    System.out.println("Usuario eliminado con éxito: " + usuario.getId());
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    // El usuario tiene órdenes en estado 'PENDIENTE' o 'ENVIADO', no se puede eliminar
                    System.out.println("No se puede eliminar el usuario, tiene órdenes en estado 'PENDIENTE' o 'ENVIADO': " + usuario.getId());
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                // El usuario no existe
                System.out.println("Intento de eliminación de un usuario no existente con ID: " + id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Error interno del servidor
            System.out.println("Error interno del servidor al intentar eliminar el usuario con ID: " + id);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    
    // Manejo de excepciones para cualquier excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @PostMapping("/encriptar-contra")
    public ResponseEntity<String> encriptarContraseñasUsuariosExistentes() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario usuario : usuarios) {
            // Si la contraseña no está cifrada (no empieza con $2a$):
            if (!usuario.getPassword().startsWith("$2a$")) {
                String contraseñaEncriptada = bCryptPasswordEncoder.encode(usuario.getPassword());
                usuario.setPassword(contraseñaEncriptada);
                usuarioRepository.save(usuario);
            }
        }

        return ResponseEntity.ok("Contraseñas actualizadas correctamente.");
    }

    @GetMapping("/debug-user")
    public ResponseEntity<?> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Usuario autenticado: " + auth);
        return ResponseEntity.ok(auth);
    }
}
