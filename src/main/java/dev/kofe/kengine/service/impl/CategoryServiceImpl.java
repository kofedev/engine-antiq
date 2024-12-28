package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.CategoryDTO;
import dev.kofe.kengine.dto.CategoryParentDTO;
import dev.kofe.kengine.dto.CategoryUpdateDTO;
import dev.kofe.kengine.dto.DescriptorDTO;
import dev.kofe.kengine.mapper.CategoryMapper;
import dev.kofe.kengine.model.Category;
import dev.kofe.kengine.model.Descriptor;
import dev.kofe.kengine.model.DescriptorSet;
import dev.kofe.kengine.repository.CategoryRepository;
import dev.kofe.kengine.repository.DescriptorRepository;
import dev.kofe.kengine.repository.DescriptorSetRepository;
import dev.kofe.kengine.service.CategoryService;
import dev.kofe.kengine.service.DescriptorSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final DescriptorRepository descriptorRepository;
    private final DescriptorSetService descriptorSetService;
    private final DescriptorSetRepository descriptorSetRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               DescriptorRepository descriptorRepository,
                               DescriptorSetService descriptorSetService,
                               CategoryMapper categoryMapper,
                               DescriptorSetRepository descriptorSetRepository) {
        this.categoryRepository = categoryRepository;
        this.descriptorRepository = descriptorRepository;
        this.descriptorSetService = descriptorSetService;
        this.categoryMapper = categoryMapper;
        this.descriptorSetRepository = descriptorSetRepository;
    }

    @Override
    public Category initialCreateRootCategory() {
        Category rootCategory = new Category();
        rootCategory.setRoot(true);
        categoryRepository.save(rootCategory);
        rootCategory.setTitleSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage());
//        rootCategory.setBriefSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage());
        categoryRepository.save(rootCategory);
        return rootCategory;
    }

    @Override
    public CategoryDTO getRootCategory() {
        return categoryMapper.fromCategory(categoryRepository.findByRootIsTrue(), false);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.fromCategoryList(categoryRepository.findAll());
    }

    @Override
    public List<CategoryDTO> getAllActiveCategories() {
        return null;
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        return categoryMapper.fromCategory(categoryRepository.findById(categoryId).orElse(null), false);
    }

    @Override
    public CategoryDTO createEmptyCategoryByParentId(CategoryParentDTO parentCategoryDTO) {
        Category parent = categoryRepository.findById(parentCategoryDTO.getCategoryId()).orElse(null);
        if (parent != null) {
            Category category = new Category();
            category.setActive(true);
            category.setRoot(false);
            categoryRepository.save(category);
            category.setTitleSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage());
            category.setParent(parent);
            parent.addSubcategory(category);
            categoryRepository.save(category);
            categoryRepository.save(parent);
            return categoryMapper.fromCategory(category, false);
        }

        return null;
    }

    @Override
    public CategoryDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO) {
        Category categoryToUpdate = categoryRepository.findById(categoryUpdateDTO.getCategoryId()).orElse(null);
        if (categoryToUpdate == null) return null;
        // **** update basic fields
        categoryToUpdate.setActive(categoryUpdateDTO.getActive());
        categoryRepository.save(categoryToUpdate);
        // **** update parent if needs
        if (categoryToUpdate.getParent() != null) {
            if (!categoryToUpdate.getParent().getCategoryId().equals(categoryUpdateDTO.getParent().getCategoryId())) {
                // yes, update parent
                Category categoryPreParent = categoryRepository.findById(categoryToUpdate.getParent().getCategoryId()).orElse(null);
                Category categoryNewParent = categoryRepository.findById(categoryUpdateDTO.getParent().getCategoryId()).orElse(null);
                if (categoryPreParent != null && categoryNewParent != null) {
                    categoryPreParent.removeSubcategory(categoryToUpdate);
                    categoryRepository.save(categoryPreParent);
                    categoryNewParent.addSubcategory(categoryToUpdate);
                    categoryRepository.save(categoryNewParent);
                    categoryToUpdate.setParent(categoryNewParent);
                    categoryRepository.save(categoryToUpdate);
                }

            }
        }
        // **** update descriptors **** SHORT VALUES ONLY
        for (DescriptorDTO descriptorDTO : categoryUpdateDTO.getTitleDescriptors()) {
            Descriptor descriptorToUpdate = descriptorRepository.findById(descriptorDTO.getDescriptorId()).orElse(null);
            if (descriptorToUpdate != null) {
                descriptorToUpdate.setValue(descriptorDTO.getValue());
                descriptorRepository.save(descriptorToUpdate);
            }
        }

        return categoryMapper.fromCategory(categoryToUpdate, false);
    }

    @Override
    public boolean isPossibleRelocateCategoryToNewParent (Long categoryToRelocateId, Long newParentId) {
        Category categoryToRelocate = categoryRepository.findById(categoryToRelocateId).orElse(null);
        Category categoryNewParent = categoryRepository.findById(newParentId).orElse(null);
        if (categoryToRelocate != null && categoryNewParent != null) {
            if (categoryToRelocate.getRoot()) return false;
            if (categoryNewParent.getRoot()) return true;
            Category parent = categoryNewParent.getParent();
            while (!parent.getRoot()) {
                if (parent.getCategoryId().equals(categoryToRelocate.getCategoryId())) return false;
                parent = parent.getParent();
            }
        }
        return true;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) return;
        if (category.getTitleSet() != null) {
            DescriptorSet descriptorSet = descriptorSetRepository.findById(category.getTitleSet().getDescriptorSetId()).orElse(null);
            category.setTitleSet(null); // unchain
            if (descriptorSet != null) {
                descriptorSetRepository.delete(descriptorSet);
            }
        }
        categoryRepository.delete(category);
    }

}
