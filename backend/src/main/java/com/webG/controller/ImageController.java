package com.webG.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

// Indica que esta clase es un controlador REST, y los métodos de esta clase devolverán
// datos directamente en lugar de una vista. Además, marca la clase para que Spring la
// gestione como un componente dentro del contexto de la aplicación.
@RestController
@RequestMapping("/api/product-images")
public class ImageController {

    // Este valor corresponde al directorio donde se almacenarán las imágenes subidas
    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            // Verifica si el archivo existe y si puede ser leído
            if (resource.exists() || resource.isReadable()) {
                // Construye una respuesta HTTP con el archivo como cuerpo de la respuesta.
                // También incluye un encabezado para indicar que el contenido se mostrará "inline" (directamente en el navegador si es posible)
                // y establece el nombre del archivo en el encabezado de la respuesta.
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                // Si el archivo no existe o no puede leerse, retorna un estado HTTP 404 (Not Found).
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            //En caso de que de Error, retorna un estado HTTP 400 (Bad Request)
            return ResponseEntity.badRequest().build();
        }
    }
}

