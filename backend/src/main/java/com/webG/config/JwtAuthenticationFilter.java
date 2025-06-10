package com.webG.config;

import com.webG.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Clase JwtAuthenticationFilter:
 * Esta clase representa un filtro de seguridad que se ejecuta una vez por solicitud
 * (OncePerRequestFilter). Su objetivo es interceptar solicitudes HTTP, verificar el token JWT
 * en la cabecera "Authorization", validar su contenido y autenticar al usuario en el contexto
 * de seguridad si el token es válido.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Llave secreta para firmar y verificar el token JWT
    @Value("${jwt.secret}")
    private String secretKey;

    // Repositorio de usuarios, inyectado mediante la anotación @Autowired.
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método principal del filtro (doFilterInternal):
     * Este método intercepta cada solicitud HTTP entrante, valida el token JWT contenido en
     * la cabecera "Authorization" (si existe) y configura el usuario autenticado en el contexto
     * de seguridad de Spring.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Obtiene la cabecera Authorization de la solicitud HTTP.
        String authHeader = request.getHeader("Authorization");

        // Si no existe la cabecera o no comienza con "Bearer ", se permite continuar con la cadena de filtros.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extrae el token JWT quitando el prefijo "Bearer " de la cabecera
        String token = authHeader.substring(7); // quita el "Bearer" ok
        Claims claims;
        try {
            // Intenta descifrar y validar el token JWT usando la llave secretaa
            claims = Jwts.parserBuilder()
                    .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes())) // Codifica la clave secreta
                    .build()
                    .parseClaimsJws(token)
                    .getBody(); // Obtiene los claims (datos contenidos en el token)
        } catch (Exception e) {
            // Si el token no es válido o ocurre un error, la solicitud se pasa al siguiente filtro sin autenticar.
            filterChain.doFilter(request, response);
            return;
        }

        // Extrae información del token JWT: email del usuario y su rol
        String email = claims.getSubject(); // El email se almacena como el "subject" del token.

        String rol = claims.get("rol", String.class); // Se recupera el rol del usuario, por ejemplo, "Admin".
        // Prefija el rol con "ROLE_" para cumplir con el formato esperado en Spring Security
        String prefixedRole = "ROLE_" + rol; // → "ROLE_Admin"

        // Verifica que el email y el rol no sean nulos y que no exista ya un usuario autenticado en el contexto de seguridad.
        if (email != null && rol != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Crea una lista de autoridades (roles) para el contexto de seguridad
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));

            // Crea un token de autenticación con el email del usuario, sin credenciales (contraseñas)
            // y con las autoridades asociadas.
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            // Configura el token de autenticación en el contexto de seguridad de Spring.
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Continúa con el siguiente filtro en la cadena, permitiendo que la solicitud avance.
        filterChain.doFilter(request, response);
    }
}
