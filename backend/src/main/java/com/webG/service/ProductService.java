package com.webG.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.webG.entity.Category;
import com.webG.entity.Product;
import com.webG.repository.ProductRepository;

@Service
public class ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    
    


    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public Category getCategoryOfProduct(Long productId) {
        return productRepository.findCategoryByProductId(productId);
    }


 //   public Product saveProduct(Product product, MultipartFile photo) throws IOException {
        // Asignar la foto al producto si se proporciona
   //     if (photo != null && !photo.isEmpty()) {
     //       product.setPhotoPath(photo.getBytes());
      //  }

        // Guardar el producto en la base de datos
       // return productRepository.save(product);
    //}

    // Otros métodos del servicio según tus necesidades, como actualizar o eliminar productos.
}
