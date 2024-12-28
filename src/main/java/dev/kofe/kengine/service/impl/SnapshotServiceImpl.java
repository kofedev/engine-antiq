package dev.kofe.kengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kofe.kengine.dto.CategoryDTO;
import dev.kofe.kengine.dto.ProductDTO;
import dev.kofe.kengine.mapper.CategoryMapper;
import dev.kofe.kengine.mapper.ProductMapper;
import dev.kofe.kengine.model.Category;
import dev.kofe.kengine.model.Product;
import dev.kofe.kengine.repository.CategoryRepository;
import dev.kofe.kengine.repository.ProductRepository;
import dev.kofe.kengine.service.SnapshotService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SnapshotServiceImpl implements SnapshotService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public SnapshotServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               ProductMapper productMapper,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Resource productsSnapshot () {

        List<Product> productList = productRepository.findAll();
        List<ProductDTO> productDTOList = productMapper.fromProductList(productList, true);

        // Convert the list of products to JSON as a string
        String jsonString = convertListToJson(productDTOList);

        // Convert the string to an InputStream
        ByteArrayInputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

        // Create a Resource from the InputStream and return this Resource
        return new InputStreamResource(stream);
    }

    @Override
    public Resource categoriesSnapshot () {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOList = categoryMapper.fromCategoryList(categoryList);

        // Convert the list of products to JSON as a string
        String jsonString = convertListToJson(categoryDTOList);

        // Convert the string to an InputStream
        ByteArrayInputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

        // Create a Resource from the InputStream and return this Resource
        return new InputStreamResource(stream);
    }


    private String convertListToJson(java.util.List objectList) {
        try {

            // Create ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the list to JSON
            String jsonString = objectMapper.writeValueAsString(objectList);

            return jsonString;
        } catch (JsonProcessingException e) {
            // Handle the exception appropriately (log, throw, etc.)
            e.printStackTrace();
            return "[]"; // Return an empty array as a fallback
        }
    }


}
