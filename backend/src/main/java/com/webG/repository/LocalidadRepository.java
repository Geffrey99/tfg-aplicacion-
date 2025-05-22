package com.webG.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webG.entity.Localidad;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    List<Localidad> findByProvinciaId(Long provinciaId);
}