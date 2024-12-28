package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.*;
import dev.kofe.kengine.model.Category;
import dev.kofe.kengine.model.Descriptor;
import dev.kofe.kengine.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryMapper {

    private final LanguageMapper languageMapper;
    private final DescriptorMapper descriptorMapper;
    public CategoryMapper (LanguageMapper languageMapper,
                           DescriptorMapper descriptorMapper) {
        this.languageMapper = languageMapper;
        this.descriptorMapper = descriptorMapper;
    }

    public CategoryDTO fromCategory(Category category, boolean withActiveSubCategoriesOnly) {
        if (category == null) return null;
        CategoryDTO categoryDTO = new CategoryDTO();
        // *** mapping
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setActive(category.getActive());
        categoryDTO.setRoot(category.getRoot());
        // *** mapping categorySet - titles
        categoryDTO.setTitleDescriptors(descriptorMapper.mapperFromDescriptorListToListDTO(category.getTitleSet().getDescriptors()));
        // *** parent
        if (!category.getRoot() && category.getParent() != null) {
            CategoryParentDTO categoryParentDTO = new CategoryParentDTO();
            categoryParentDTO.setCategoryId(category.getParent().getCategoryId());
            categoryParentDTO.setActive(category.getParent().getActive());
            categoryParentDTO.setRoot(category.getParent().getRoot());
            // *** mapping categorySet - titles - for parent
            categoryParentDTO.setTitleDescriptors(descriptorMapper.mapperFromDescriptorListToListDTO(category.getParent().getTitleSet().getDescriptors()));
            categoryDTO.setParent(categoryParentDTO);
        } else {
            categoryDTO.setParent(null);
        }

        // *** subcategories
        List<CategorySubsDTO> categorySubsDTOList = new ArrayList<>();
        for (Category subCategory : category.getSubcategories()) {
            if (withActiveSubCategoriesOnly && !subCategory.getActive()) continue;
            CategorySubsDTO categorySubsDTO = new CategorySubsDTO();
            categorySubsDTO.setCategoryId(subCategory.getCategoryId());
            categorySubsDTO.setActive(subCategory.getActive());
            categorySubsDTO.setTitleDescriptors(descriptorMapper.mapperFromDescriptorListToListDTO(subCategory.getTitleSet().getDescriptors()));
            categorySubsDTOList.add(categorySubsDTO);
        }
        categoryDTO.setSubcategories(categorySubsDTOList);

        // *** products
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product : category.getProducts()) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(product.getProductId());
            productDTOList.add(productDTO);
        }
        categoryDTO.setProducts(productDTOList);

        return categoryDTO;
    }

    public List<CategoryDTO> fromCategoryList (List<Category> categoryList) {
        if (categoryList == null) return null;
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryDTOList.add(fromCategory(category, false));
        }

        return categoryDTOList;
    }

}
