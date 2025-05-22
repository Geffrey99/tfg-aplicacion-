package com.webG.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webG.entity.Category;

import com.webG.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByCategoryId(Long categoryId);
  //  @Query("SELECT p.category FROM Product p WHERE p.id = :productId")
   // Category findCategoryByProductId(@Param("productId") Long productId);
    @Query("SELECT p.category FROM Product p WHERE p.id = :productId")
    Category findCategoryByProductId(@Param("productId") Long productId);
}
