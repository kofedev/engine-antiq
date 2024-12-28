package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.ImageDTO;
import dev.kofe.kengine.mapper.ImageMapper;
import dev.kofe.kengine.model.Image;
import dev.kofe.kengine.model.Product;
import dev.kofe.kengine.repository.ImageRepository;
import dev.kofe.kengine.repository.ProductRepository;
import dev.kofe.kengine.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ImageMapper imageMapper;

    @Value("${engine.base.upload.directory}") String baseUploadDirectory;

    @Autowired
    public ImageServiceImpl (ImageRepository imageRepository,
                             ProductRepository productRepository,
                             ImageMapper imageMapper) {
        this.imageRepository = imageRepository;
        this.productRepository = productRepository;
        this.imageMapper = imageMapper;
    }

    @Override
    public ImageDTO setImageAsMain(ImageDTO imageDTO) {
        Image image = imageRepository.findById(imageDTO.getImageId()).orElse(null);
        if (image == null) return null;
        Product product = productRepository.findById(image.getProduct().getProductId()).orElse(null);
        if (product == null) return null;
        List<Image> imageList = imageRepository.findAllByProduct(product);
        // clear
        for (Image imageToClearMainStatus : imageList) {
            imageToClearMainStatus.setMainImage(false);
            imageRepository.save(imageToClearMainStatus);
        }
        // set main
        image.setMainImage(true);
        imageRepository.save(image);

        return imageMapper.fromImage(image);
    }

    @Override
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) return;
        Product product = productRepository.findById(image.getProduct().getProductId()).orElse(null);
        if (product == null) return;

        String imageUrl = image.getImageUrl();

        product.removeImage(image);
        productRepository.save(product);
        imageRepository.delete(image);


        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1); // extract filename
//        String absolutePath = UPLOAD_DIRECTORY + "/" + fileName;
        String absolutePath = baseUploadDirectory + "/" + fileName;

        //System.out.println("absolutePath: " + absolutePath);

        try {
            Path filePath = Paths.get(absolutePath);
            Files.delete(filePath);
            System.out.println("File deleted successfully"); //@ToDo LOGGING!
        } catch (NoSuchFileException e) {
            System.out.println("No such file/directory exists");
        } catch (DirectoryNotEmptyException e) {
            System.out.println("Directory is not empty");
        } catch (IOException e) {
            System.out.println("Invalid permissions or other IOException");
        }

    }

}
