package com.webG.service;

import com.webG.entity.Usuario;

import com.webG.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.security.Key;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    // Cargar la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String secretKey;
    // Clave secreta fija

    /**
     * Método para autenticar al usuario por email y contraseña.
     */
    public Usuario authenticate(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null && bCryptPasswordEncoder.matches(password, usuario.getPassword())) {
            return usuario; // Usuario autenticado correctamente
        }
        return null; // Credenciales incorrectas
    }

    /**
     * Método para generar un token JWT que incluye el rol del usuario.
     */
    public String generateToken(Usuario usuario) {
        // Convertir la clave cargada desde application.properties en un SecretKey válido
        SecretKey signingKey = new SecretKeySpec(Base64.getEncoder().encode(secretKey.getBytes()), // Convertir la clave en Base64
                SignatureAlgorithm.HS256.getJcaName() // Algoritmo HS256 o el que uses (HS384, etc.)

        );


        // Crear el token JWT con el rol incluido en el payload
        long tiempoExpiracion = 1000 * 60 * 60; // 1 hora
        return Jwts.builder().setSubject(usuario.getEmail()) // Email del usuario
                .claim("rol", usuario.getRol()) // Agregar el rol del usuario al payload Esto asegura que el rol (`Admin`, `User`, etc.) estará disponible en el token para validaciones futuras.
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + tiempoExpiracion)) // Fecha de expiración
                .signWith(signingKey) // Firmar el token con la clave secreta
                .compact();
    }


}
