package com.webG.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webG.entity.Provincia;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
}