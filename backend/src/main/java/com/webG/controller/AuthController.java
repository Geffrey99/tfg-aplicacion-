package com.webG.controller;

import com.webG.entity.Usuario;
import com.webG.service.AuthService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Usuario authenticatedUser = authService.authenticate(email, password);
        if (authenticatedUser != null) {
            String token = authService.generateToken(authenticatedUser);
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", authenticatedUser);
            response.put("token", token);
            response.put("message", "OK, has pasado el login"); // Mensaje de todo Okkkkk
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Credenciales incorrectas, INTENTALO")
            );
        }
    }

}
