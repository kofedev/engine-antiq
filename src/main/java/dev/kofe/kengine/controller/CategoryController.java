package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.CategoryDTO;
import dev.kofe.kengine.dto.CategoryParentDTO;
import dev.kofe.kengine.dto.CategoryUpdateDTO;
import dev.kofe.kengine.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public CategoryDTO createEmptyCategoryByParentCategory(@RequestBody CategoryParentDTO parentCategoryDTO) {
        return categoryService.createEmptyCategoryByParentId(parentCategoryDTO);
    }

    @PutMapping("")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public CategoryDTO updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        return categoryService.updateCategory(categoryUpdateDTO);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/possible_to_relocate")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public boolean isPossibleToRelocate(@RequestParam Long categoryId, @RequestParam Long destinationId) {
        return categoryService.isPossibleRelocateCategoryToNewParent(categoryId, destinationId);
    }

    @GetMapping("/common/root")
    public CategoryDTO getRootCategory() {
        return categoryService.getRootCategory();
    }

    @GetMapping("/common/active")
    public List<CategoryDTO> getAllActiveCategories() {
        return categoryService.getAllActiveCategories();
    }

    @GetMapping("/common/{categoryId}")
    public CategoryDTO getAllActiveCategories(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }





}
