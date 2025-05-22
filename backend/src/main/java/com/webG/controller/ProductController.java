package com.webG.controller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.webG.entity.Category;
import com.webG.entity.OrderDetail;
import com.webG.entity.Product;
import com.webG.repository.CategoryRepository;
import com.webG.repository.OrderDetailRepository;
import com.webG.repository.ProductRepository;

import jakarta.transaction.Transactional;


    @RestController
    @RequestMapping("/api/products")
    public class ProductController {
        @Autowired
        private ProductRepository productRepository;
        
        @Autowired
        private CategoryRepository categoryRepository; 
        
        @Autowired
        private OrderDetailRepository orderDetailRepository;

      //  @Autowired
       // private final String imageBaseUrl;
        
        @Value("${upload.path}")
        private String uploadPath; 

        public ProductController(ProductRepository productRepository, @Value("${upload.path}") String uploadPath) {
            this.productRepository = productRepository;
         //   this.imageBaseUrl = uploadPath; // Aquí asumimos que uploadPath ya contiene la URL base
        }

        @GetMapping("/todos")
        public ResponseEntity<List<ProductDTO>> getAllProducts() {
            List<Product> products = productRepository.findAll();
            List<ProductDTO> productDTOs = new ArrayList<>();
            for (Product product : products) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId(product.getId());
                productDTO.setName(product.getName());
                productDTO.setPrice(product.getPrice());
                productDTO.setPhotoUrl(product.getPhotoPath());
                productDTO.setDescription(product.getDescription());
                productDTO.setStock(product.getStock());
             
                // Obtener la categoría del producto
                Category category = productRepository.findCategoryByProductId(product.getId());
                CategoryDTO categoryDTO = new CategoryDTO(category);

                // Asignar la categoría al DTO del producto
                productDTO.setCategory(categoryDTO);

                // Añadir el ProductDTO a la lista
                productDTOs.add(productDTO);
            }
            return ResponseEntity.ok(productDTOs);
        }


    // Obtener un producto por su ID
        @GetMapping("/{id}")
        public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
            java.util.Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                // Obtener la categoría asociada al producto
                Category category = productRepository.findCategoryByProductId(id);
                
                // Crear un objeto JSON que incluya los datos del producto y la categoría
                Map<String, Object> response = new HashMap<>();
                response.put("id", product.getId());
                response.put("name", product.getName());
                response.put("price", product.getPrice());
                response.put("description", product.getDescription());
                response.put("stock", product.getStock());
                response.put("photoPath", product.getPhotoPath());
                
                // Agregar solo el ID y el nombre de la categoría al objeto de respuesta
                Map<String, Object> categoryInfo = new HashMap<>();
                categoryInfo.put("id", category.getId());
                categoryInfo.put("name", category.getName());
                response.put("category", categoryInfo);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    
   


    
// Ruta donde se guardarán las imágenes
    
    
    @PostMapping("/create")
    public ResponseEntity<?> createProduct( @ModelAttribute Product product, BindingResult bindingResult,@RequestParam("photo") MultipartFile photo) throws IOException {
        // Validar los datos del producto
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        if (photo.isEmpty()) {
            return new ResponseEntity<>("La imagen está vacía", HttpStatus.BAD_REQUEST);
        }
      
        // Verificar si existeee la categoría
        if (product.getCategory() != null) {
            // Si se proporciona el ID de la categoría, intenta buscarla en la base de datos
            if (product.getCategory().getId() != null) {
                java.util.Optional<Category> optionalCategory = categoryRepository.findById(product.getCategory().getId());
                if (optionalCategory.isPresent()) {
                    // Si la categoría existe, establece la categoría del producto
                    product.setCategory(optionalCategory.get());
                } else {
                    // Si la categoría no existe, devuelve un error
                    return ResponseEntity.badRequest().body("La categoría proporcionada no existe");
                }
            } else if (product.getCategory().getName() != null) {
            	// Si se proporciona el nombre de la categoría, intenta buscarla en la base de datos
            	java.util.Optional<Category> optionalCategory = categoryRepository.findByName(product.getCategory().getName());
            	if (optionalCategory.isPresent()) {
            	    // Si la categoría existe, establece la categoría del producto
            	    product.setCategory(optionalCategory.get());
            	} else {
            	    // Si la categoría no existe, crea una nueva categoría y la guarda en la base de datos 
            	    Category newCategory = new Category();
            	    newCategory.setName(product.getCategory().getName());
            	    // Guardar la nueva categoría
            	    categoryRepository.save(newCategory);
            	    // Establecer la categoría del producto
            	    product.setCategory(newCategory);
            	}
            }
        } else {
            // Si no se proporciona información de la categoría, devuelve un error
            return ResponseEntity.badRequest().body("Se requiere información de la categoría");
        }
        

        // Guardar la imagen en el sistema de archivos
		String fileName = photo.getOriginalFilename();
		String cleanedFileName = cleanFileName(fileName);
		String filePath = uploadPath + File.separator + cleanedFileName;

		
		// Establecer la ruta del archivo en el campo photoPath
		//product.setPhotoPath(filePath.toString());
		 // Guardar la imagen en el sistema de archivos
        File file = new File(filePath);
        file.getParentFile().mkdirs(); 
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(photo.getBytes());
        outputStream.close();
        product.setPhotoPath(cleanedFileName);
		Product savedProduct = productRepository.save(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    private String cleanFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
    		@ModelAttribute ProductDTO productDTO,
    		BindingResult bindingResult,
    		@RequestParam(value = "photo", required = false) MultipartFile photo,
    		@RequestParam(value = "categoryId", required = false) Long categoryId) throws IOException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setDescription(productDTO.getDescription());
            product.setStock(productDTO.getStock());

            // Solo actualiza la categoría si ya existe
            if (categoryId != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
                if (categoryOptional.isPresent()) {
                    product.setCategory(categoryOptional.get());
                } else {
                    return ResponseEntity.badRequest().body("La categoría con el ID proporcionado no existe");
                }
            }
            // Actualiza la imagen si se proporciona una nueva
            if (photo != null && !photo.isEmpty()) {
                String fileName = photo.getOriginalFilename();
                String cleanedFileName = cleanFileName(fileName);
                String filePath = uploadPath + File.separator + cleanedFileName;
                File file = new File(filePath);
                file.getParentFile().mkdirs();
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    outputStream.write(photo.getBytes());
                }
                product.setPhotoPath(cleanedFileName);
            }

            product = productRepository.save(product);
            Category category = product.getCategory();
            CategoryDTO categoryDTO = category != null ? new CategoryDTO(category.getId(), category.getName()) : null;

            productDTO.setId(product.getId());
            productDTO.setCategory(categoryDTO);
            productDTO.setPhotoUrl(product.getPhotoPath());

            return ResponseEntity.ok(productDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    @DeleteMapping("/eliminar/{id}")
    @Transactional
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                if (product.getStock() > 2) {
                    return ResponseEntity.badRequest().body("{\"message\":\"No se puede eliminar el producto porque aún tiene stock.\"}");
                } else {
                    // Verificar si hay detalles de pedido que referencian este producto
                    List<OrderDetail> orderDetails = orderDetailRepository.findByProduct_Id(product.getId());
                    // Verificar el estado de las órdenes asociadas
                    boolean allOrdersReceived = orderDetails.stream()
                        .allMatch(detail -> "Recibido".equals(detail.getOrder().getEstado()));
                    
                    if (!orderDetails.isEmpty() && allOrdersReceived) {
                        // Eliminar los detalles de pedido
                        orderDetailRepository.deleteAll(orderDetails);
                        productRepository.delete(product);
                        return ResponseEntity.ok().body("{\"message\":\"Producto eliminado con éxito.\"}");
                       // return ResponseEntity.ok().body("{\"message\":\"Producto eliminado con éxito.\"}");
                    } else {
                        return ResponseEntity.badRequest().body("{\"message\":\"No se puede eliminar el producto porque tiene órdenes pendientes o no recibidas.\"}");
                    }
                }
            } else {
                return ((BodyBuilder) ResponseEntity.notFound()).body("{\"message\":\"Producto no encontrado.\"}");
            }
        } catch (Exception e) {

            //Logger log = LoggerFactory.getLogger(TuClase.class); //
            log.error("Error al eliminar el producto: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Error interno al eliminar el producto.\"}");
        }
    }

    
}