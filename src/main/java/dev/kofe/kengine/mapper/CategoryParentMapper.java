package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.CategoryParentDTO;
import dev.kofe.kengine.model.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryParentMapper {

    private final DescriptorMapper descriptorMapper;
    public CategoryParentMapper(DescriptorMapper descriptorMapper) {
        this.descriptorMapper = descriptorMapper;
    }

    CategoryParentDTO fromCategory (Category category) {
        if (category == null) return null;
        CategoryParentDTO categoryParentDTO = new CategoryParentDTO();
        categoryParentDTO.setCategoryId(category.getCategoryId());
        categoryParentDTO.setActive(category.getActive());
        categoryParentDTO.setRoot(category.getRoot());
        categoryParentDTO.setTitleDescriptors(descriptorMapper.mapperFromDescriptorListToListDTO(category.getTitleSet().getDescriptors()));
        return categoryParentDTO;
    }
}

