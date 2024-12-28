package dev.kofe.kengine.controller;

import dev.kofe.kengine.service.SnapshotService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/snapshot")
public class SnapshotController {

    private final SnapshotService snapshotService;

    public SnapshotController(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

     @GetMapping("/products")
     @PreAuthorize("hasAuthority('Admin')")
     public ResponseEntity<Resource> exportProductsToJson() {
        Resource resource = snapshotService.productsSnapshot();
        // Set the content type and attachment disposition
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Return ResponseEntity with the Resource and headers
        return ResponseEntity.ok()
                .headers(headers)
              .body(resource);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Resource> exportCategoriesToJson() {
        Resource resource = snapshotService.categoriesSnapshot();
        // Set the content type and attachment disposition
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Return ResponseEntity with the Resource and headers
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

}
