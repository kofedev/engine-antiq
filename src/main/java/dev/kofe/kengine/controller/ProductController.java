package dev.kofe.kengine.controller;

import dev.kofe.kengine.dto.CategoryParentDTO;
import dev.kofe.kengine.dto.ProductDTO;
import dev.kofe.kengine.dto.ProductFullDTO;
import dev.kofe.kengine.dto.ProductPageDTO;
import dev.kofe.kengine.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ProductDTO createNewEmptyProductByParentCategory(@RequestBody CategoryParentDTO categoryParentDTO) {
        return productService.createNewEmptyProductByParentCategoryAndExpandDescriptors(categoryParentDTO);
    }

    @GetMapping("/short/category/{categoryId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public List<ProductDTO> getAllProductsByCategoryId_short(@PathVariable Long categoryId) {
        return productService.getAllProductsByCategoryId(categoryId, false);
    }

    @GetMapping("/full/category/{categoryId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public List<ProductDTO> getAllProductsByCategoryId_full(@PathVariable Long categoryId) {
        return productService.getAllProductsByCategoryId(categoryId, true);
    }

    @GetMapping("/common/short/{productId}")
    public ProductDTO getProductById_short(@PathVariable Long productId) {
        return productService.getProductById(productId, false);
    }

    @GetMapping("/common/full/{productId}")
    public ProductDTO getProductById_full(@PathVariable Long productId) {
        return productService.getProductById(productId, true);
    }

    // get product full with List of orders
    @GetMapping("/full/orders/{productId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ProductFullDTO getProductByIdFullOrdersAndCategory(@PathVariable Long productId) {
        return productService.getProductByIdFullWithOrdersAndCategory(productId);
    }

    @PutMapping("")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ProductDTO updateProduct(@RequestBody ProductDTO productDTO) {
        return productService.updateProduct(productDTO);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ** clone product
    @GetMapping("/clone/{productId}")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public ProductDTO cloneProduct(@PathVariable Long productId) {
        return productService.cloneProductByPatternProductId(productId);
    }

    //*** search machine
    //*** search in the category and subcategories which belong to the category
    //*** DOC:
    //*** categoryId=id - is a category ID
    //*** min=minimal_price
    //*** max=maximal_price
    //*** sortbyprice=asc of desc
    //*** pagenum=page_number_from_0
    //*** pagesize=page_size
    //*** search=string   - search in all searchable descriptors and languages, excluding BigValues
    //***
    @GetMapping("/common")
    public ProductPageDTO
    productEngine (@RequestParam(required = true) String categoryId,
                   @RequestParam(required = false) String search,
                   @RequestParam(required = false) String min,
                   @RequestParam(required = false) String max,
                   @RequestParam(required = false) String sortbyprice,
                   @RequestParam(required = false) String pagenum,
                   @RequestParam(required = false) String pagesize) {

        return productService.getActiveProductsWithPositiveQuantitiesByParameters(categoryId, search, min, max, sortbyprice, pagenum, pagesize);
    }

}





