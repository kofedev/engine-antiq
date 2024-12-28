package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.ImageDTO;
import org.springframework.stereotype.Service;

public interface ImageService {
    ImageDTO setImageAsMain(ImageDTO imageDTO);
    void deleteImage(Long imageId);
}
