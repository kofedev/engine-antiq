package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.ImageDTO;
import dev.kofe.kengine.model.Image;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageMapper {

    public ImageDTO fromImage(Image image) {
        if (image == null) return null;
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageId(image.getImageId());
        imageDTO.setImageUrl(image.getImageUrl());
        imageDTO.setActive(image.isActive());
        imageDTO.setMainImage(image.isMainImage());

        return imageDTO;
    }

    public List<ImageDTO> fromImageListToImageDTOList(List<Image> imageList) {
        if (imageList == null) return null;
        List<ImageDTO> imageDTOList = new ArrayList<>();
        for (Image image : imageList) {
            imageDTOList.add(fromImage(image));
        }

        return imageDTOList;
    }

}
