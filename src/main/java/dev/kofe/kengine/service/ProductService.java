package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.CategoryParentDTO;
import dev.kofe.kengine.dto.ProductDTO;
import dev.kofe.kengine.dto.ProductFullDTO;
import dev.kofe.kengine.dto.ProductPageDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ProductService {

    ProductDTO createNewEmptyProductByParentCategoryAndExpandDescriptors(CategoryParentDTO categoryParentDTO);
    List<ProductDTO> getAllProductsByCategoryId(Long categoryId, boolean isFull);
    ProductDTO getProductById(Long productId, boolean isFull);
    ProductDTO updateProduct(ProductDTO productDTO);
    void deleteProduct(Long productId);
    ProductPageDTO getActiveProductsWithPositiveQuantitiesByParameters(String categoryId, String search, String min, String max, String sortbyprice, String pagenum, String pagesize);
    ProductDTO cloneProductByPatternProductId(Long productId);
    ProductFullDTO getProductByIdFullWithOrdersAndCategory(Long productId);

}
