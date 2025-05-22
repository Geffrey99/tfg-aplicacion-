package com.webG.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping
    public ResponseEntity<?> debug() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Usuario autenticado: " + SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(auth.getAuthorities());

    }

}
