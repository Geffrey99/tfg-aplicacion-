package com.webG.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webG.entity.Localidad;
import com.webG.entity.Provincia;
import com.webG.repository.LocalidadRepository;
import com.webG.repository.ProvinciaRepository;

@RestController
@RequestMapping("/api/localizacion")
public class LocalidadProvinciaController {
	  	
		@Autowired
		private final LocalidadRepository localidadRepository;
		@Autowired
	    private final ProvinciaRepository provinciaRepository;

	    public LocalidadProvinciaController(LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository) {
	        this.localidadRepository = localidadRepository;
	        this.provinciaRepository = provinciaRepository;
	    }

	    @GetMapping("/provincias")
	    public ResponseEntity<List<Provincia>> getAllProvincias() {
	        return new ResponseEntity<>(provinciaRepository.findAll(), HttpStatus.OK);
	    }

	    @GetMapping("/provincias/{provinciaId}/localidades")
	    public ResponseEntity<List<Localidad>> getLocalidadesByProvincia(@PathVariable Long provinciaId) {
	        List<Localidad> localidades = localidadRepository.findByProvinciaId(provinciaId);
	        if (localidades.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(localidades, HttpStatus.OK);
	    }
} //Ok controlador de mis Localidades/provincias