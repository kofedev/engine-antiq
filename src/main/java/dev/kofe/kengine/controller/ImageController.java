package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.ImageDTO;
import dev.kofe.kengine.mapper.ImageMapper;
import dev.kofe.kengine.model.Image;
import dev.kofe.kengine.model.Product;
import dev.kofe.kengine.repository.ImageRepository;
import dev.kofe.kengine.repository.ProductRepository;
import dev.kofe.kengine.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageMapper imageMapper;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final ProductRepository productRepository;

    @Value("${engine.base.server}") String baseServer;
    @Value("${engine.base.upload.directory}") String baseUploadDirectory;

    @Autowired
    public ImageController(ImageMapper imageMapper,
                           ImageRepository imageRepository,
                           ImageService imageService,
                           ProductRepository productRepository) {
        this.imageMapper = imageMapper;
        this.imageRepository = imageRepository;
        this.imageService = imageService;
        this.productRepository = productRepository;
    }

    @GetMapping("/common/{imageId}")
    public ImageDTO getImageById(@PathVariable Long imageId) {
        return imageMapper.fromImage(imageRepository.findById(imageId).orElse(null));
    }

    @PostMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ImageDTO uploadImage (@RequestParam("file") MultipartFile file, @PathVariable Long productId) throws IOException {

        if (file == null) return null;

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return null;

        String uniqPartName = UUID.randomUUID().toString().substring(0, 5);
        String fileName = uniqPartName + "__" + file.getOriginalFilename();

        try {


//            String uploadDirectory = UPLOAD_DIRECTORY;
            String uploadDirectory = baseUploadDirectory;

            // Check if the directory exists, and create it if not
            if (!Files.exists(Paths.get(uploadDirectory))) {
                Files.createDirectories(Paths.get(uploadDirectory));
            }

            Path filePath = Paths.get(uploadDirectory, fileName);
            Files.write(filePath, file.getBytes());

            // **** CREATE NEW IMAGE IN THE DB ****
            Image image = new Image();
            image.setActive(true);
//            image.setImageUrl(SERVER_NAME + "/img/" + fileName);
            image.setImageUrl(baseServer + "/img/" + fileName);
            if (product.getImages().size() == 0) {
                image.setMainImage(true);
            } else {
                image.setMainImage(false);
            }
            imageRepository.save(image);
            product.addImage(image);
            productRepository.save(product);
            //imageRepository.save(image);

            return imageMapper.fromImage(image);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @PutMapping("/mainstatus/{imageId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ImageDTO setMainImage(@PathVariable Long imageId, @RequestBody ImageDTO imageDTO) {
        return imageService.setImageAsMain(imageDTO);
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public void deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
    }

}
