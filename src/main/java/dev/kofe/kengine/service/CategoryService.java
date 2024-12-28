package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.CategoryDTO;
import dev.kofe.kengine.dto.CategoryParentDTO;
import dev.kofe.kengine.dto.CategoryUpdateDTO;
import dev.kofe.kengine.model.Category;

import java.util.List;

public interface CategoryService {
    Category initialCreateRootCategory();
    CategoryDTO getRootCategory();
    List<CategoryDTO> getAllCategories();
    List<CategoryDTO> getAllActiveCategories();
    CategoryDTO getCategoryById(Long categoryId);
    CategoryDTO createEmptyCategoryByParentId(CategoryParentDTO parentCategoryDTO);
    CategoryDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    void deleteCategory(Long categoryId);
    boolean isPossibleRelocateCategoryToNewParent (Long categoryToRelocateId, Long newParentId);
}



