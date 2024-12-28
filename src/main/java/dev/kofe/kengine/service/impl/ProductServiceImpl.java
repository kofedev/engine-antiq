package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.*;
import dev.kofe.kengine.mapper.CategoryMapper;
import dev.kofe.kengine.mapper.OrderMapper;
import dev.kofe.kengine.mapper.ProductMapper;
import dev.kofe.kengine.model.*;
import dev.kofe.kengine.repository.*;
import dev.kofe.kengine.service.DescriptorSetService;
import dev.kofe.kengine.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final DescriptorSetService descriptorSetService;
    private final DescriptorSetRepository descriptorSetRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final ImageRepository imageRepository;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    @Value("${engine.base.server}") String baseServer;
    @Value("${engine.base.upload.directory}") String uploadDirectory;

    public ProductServiceImpl(ProductRepository productRepository,
                              DescriptorSetService descriptorSetService,
                              DescriptorSetRepository descriptorSetRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              ImageRepository imageRepository,
                              CategoryMapper categoryMapper,
                              OrderMapper orderMapper,
                              OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.descriptorSetService =descriptorSetService;
        this.descriptorSetRepository = descriptorSetRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.imageRepository = imageRepository;
        this.categoryMapper = categoryMapper;
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    public ProductDTO createNewEmptyProductByParentCategoryAndExpandDescriptors(CategoryParentDTO categoryParentDTO) {
        Category parentCategory = categoryRepository.findById(categoryParentDTO.getCategoryId()).orElse(null);
        if (parentCategory == null) return null;

        Product product = new Product();
        product.setCurrentQuantity(0);
        product.setKeyWords("");
        product.setNote("");
        product.setOfferPrice(new BigDecimal(0));
        product.setPartNumber("");
        product.setPublishedOn(new java.sql.Timestamp(System.currentTimeMillis()));
        productRepository.save(product);
        // title, brief, full sets
        product.setTitleSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage());
        product.setBriefSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage());
        product.setFullSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage_BigDescriptors());
        // parent category;
        product.setCategory(parentCategory);
        parentCategory.addProduct(product);
        categoryRepository.save(parentCategory);
        productRepository.save(product);

        return productMapper.fromProduct(product, true);
    }

    @Override
    public List<ProductDTO> getAllProductsByCategoryId(Long categoryId, boolean isFull) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) return null;
        return productMapper.fromProductList(productRepository.findAllByCategory(category), isFull);
    }

    @Override
    public ProductDTO getProductById(Long productId, boolean isFull) {
        return productMapper.fromProduct( productRepository.findById(productId).orElse(null), isFull );
    }

    // exclude IMAGES //@ToDO
    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product productToUpdate = productRepository.findById(productDTO.getProductId()).orElse(null);
        if (productToUpdate == null) return null;
        // **** PLANE FIELDS
        productToUpdate.setActive          (productDTO.getActive());
        productToUpdate.setPartNumber      (productDTO.getPartNumber());
        productToUpdate.setOfferPrice      (productDTO.getOfferPrice());
        productToUpdate.setCurrentQuantity (productDTO.getCurrentQuantity());
        if (productDTO.getPublishedOn() != null) productToUpdate.setPublishedOn (productDTO.getPublishedOn());
        productToUpdate.setKeyWords        (productDTO.getKeyWords());
        productToUpdate.setNote            (productDTO.getNote());
        productRepository.save(productToUpdate);
        // *** PARENT CATEGORY
        if (!productToUpdate.getCategory().getCategoryId().equals(productDTO.getCategory().getCategoryId())) {
            // relocation case
            Category oldCategory = categoryRepository.findById(productToUpdate.getCategory().getCategoryId()).orElse(null);
            Category newCategory = categoryRepository.findById(productDTO.getCategory().getCategoryId()).orElse(null);
            if (oldCategory == null || newCategory == null) return null;
            newCategory.addProduct(productToUpdate);
            oldCategory.removeProduct(productToUpdate);
            productToUpdate.setCategory(newCategory);
            categoryRepository.save(newCategory);
            categoryRepository.save(oldCategory);
            productRepository.save(productToUpdate);
        }
        // *** DESCRIPTORS
        descriptorSetService.updateValuesOnlyOfDescriptorsOnly(wrapDescriptorsToDescriptorSet(productDTO.getTitleDescriptors()));
        descriptorSetService.updateValuesOnlyOfDescriptorsOnly(wrapDescriptorsToDescriptorSet(productDTO.getBriefDescriptors()));
        descriptorSetService.updateValuesOnlyOfDescriptorsOnly(wrapDescriptorsToDescriptorSet(productDTO.getFullDescriptors()));

        productRepository.save(productToUpdate);
        return productMapper.fromProduct(productToUpdate, true);
    }

    private DescriptorSetDTO wrapDescriptorsToDescriptorSet (List<DescriptorDTO> descriptorDTOList) {
        DescriptorSetDTO descriptorSetDTO = new DescriptorSetDTO();
        descriptorSetDTO.setDescriptors(descriptorDTOList);
        return descriptorSetDTO;
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            // **** clear descriptor sets
            DescriptorSet titleDescriptorSet = descriptorSetRepository.findById(product.getTitleSet().getDescriptorSetId()).orElse(null);
            DescriptorSet briefDescriptorSet = descriptorSetRepository.findById(product.getBriefSet().getDescriptorSetId()).orElse(null);
            DescriptorSet fullDescriptorSet  = descriptorSetRepository.findById(product.getFullSet().getDescriptorSetId()).orElse(null);
            if (titleDescriptorSet == null || briefDescriptorSet == null || fullDescriptorSet == null) {
                System.out.println("Delete product with ID " + productId + ". Problems with descriptorSETs.");
                return;
            }

            product.setTitleSet(null);
            product.setBriefSet(null);
            product.setFullSet(null);
            descriptorSetRepository.delete(titleDescriptorSet);
            descriptorSetRepository.delete(briefDescriptorSet);
            descriptorSetRepository.delete(fullDescriptorSet);
            // **** delete process
            productRepository.delete(product);
        }
    }

    // ** clone product
    @Override
    public ProductDTO cloneProductByPatternProductId(Long productId) {
        Product patternProduct = productRepository.findById(productId).orElse(null);
        if (patternProduct == null) { return null; } //@ToDo log!

        Product product = new Product();

        product.setActive(patternProduct.getActive());
        product.setPartNumber(patternProduct.getPartNumber() + " copy");
        product.setOfferPrice(patternProduct.getOfferPrice());
        product.setCurrentQuantity(patternProduct.getCurrentQuantity());
        product.setPublishedOn(new java.sql.Timestamp(System.currentTimeMillis()));
        product.setKeyWords(patternProduct.getKeyWords());
        product.setNote(patternProduct.getNote());

        productRepository.save(product);

        // title, brief, full sets // ****************************************************************************
        product.setTitleSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage(patternProduct.getTitleSet()));
        product.setBriefSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage(patternProduct.getBriefSet()));
        product.setFullSet(descriptorSetService.createDescriptorSetAndExpandDescriptorsEachForLanguage_BigDescriptors(patternProduct.getFullSet()));

        Category parentCategory = categoryRepository.findById(patternProduct.getCategory().getCategoryId()).orElse(null);
        if (parentCategory == null) { return null; } //@ToDo log!

        // parent category;
        product.setCategory(parentCategory);
        parentCategory.addProduct(product);
        categoryRepository.save(parentCategory);
        productRepository.save(product);

        // images clone
        for (Image patternImage : patternProduct.getImages()) {
            Image newImage = new Image();
            String newImageUrlPath = cloneImageFile(patternImage);
            if (newImageUrlPath != null) {
                newImage.setImageUrl(newImageUrlPath);
                newImage.setMainImage(patternImage.isMainImage());
                newImage.setActive(patternImage.isActive());
                imageRepository.save(newImage);
                product.addImage(newImage);
                newImage.setProduct(product);
                imageRepository.save(newImage);
                productRepository.save(product);
            } else {
                //@ToDo LOGGER
            }
        }


        return productMapper.fromProduct(product, false);
    }


    // returns the url name of new imageFile which is ready to set to new Image
    private String cloneImageFile (Image patternImage) {

        String imageUrl = patternImage.getImageUrl();
        String patternFileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1); // extract filename
//        String patternImageAbsolutePath = UPLOAD_DIRECTORY + "/" + patternFileName;
        String patternImageAbsolutePath = uploadDirectory + "/" + patternFileName;

        String uniqPartFileName = "_copy_" + UUID.randomUUID().toString().substring(0, 4);

        String baseName = StringUtils.stripFilenameExtension(patternFileName);
        String extension = StringUtils.getFilenameExtension(patternFileName);
        String newFileNameWithRandomTail = String.format("%s%s.%s", baseName, uniqPartFileName, extension);

        Path sourcePath = Paths.get(patternImageAbsolutePath);
        Path destinationPath = sourcePath.resolveSibling(newFileNameWithRandomTail);

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
//            return SERVER_NAME + "/img/" + newFileNameWithRandomTail;
            return baseServer + "/img/" + newFileNameWithRandomTail;
        } catch (Exception e) {
            //@ToDo Log. Separate method
        }

        return null;
    }

    //************************************************************************************************************
    //*** PRODUCT ENGINE VER.2 ***********************************************************************************
    //*** mode to collect products: this engine collects products from the category and from all sub-categories
    //*** active categories and products only
    //*** if category is inactive = all products from this category are considered as inactive
    //*** ---------------
    //*** documentation:
    //*** ---------------
    //*** categoryId=id     - is a category ID
    //*** min=minimal_price
    //*** max=maximal_price
    //*** sortbyprice=asc or desc
    //*** pagenum=page_number_from_0
    //*** pagesize=page_size
    //***
    //*** search=string   - search in all searchable descriptors and languages, excluding BigValues
    //******************************************************************************************************
    //*** ATTENTION: OPERATE WITH ACTIVE PRODUCTS / CATEGORIES ONLY

    @Override
    public ProductPageDTO
    getActiveProductsWithPositiveQuantitiesByParameters
            (String categoryId, String search, String min, String max, String sortbyprice, String pagenum, String pagesize) {

        Long categoryIdAsLong;
        int pagenumAsInt = 0;
        int pagesizeAsInt = Integer.MAX_VALUE;
        BigDecimal minAsBigDecimal = (min != null) ? new BigDecimal(min) : null;
        BigDecimal maxAsBigDecimal = (max != null) ? new BigDecimal(max) : null;
        Pageable pageable;
        Page<Product> productPage;
        Sort sort =
                (sortbyprice == null)
                ? null
                : ( (sortbyprice.toLowerCase().equals("asc")) ? Sort.by(Sort.Order.asc("offerPrice")) : Sort.by(Sort.Order.desc("offerPrice")) );

        // **** attempt to retrieve the category ID ****
        try {
            categoryIdAsLong = Long.parseLong(categoryId);
        } catch (Exception e) {
            System.out.println("Something was going wrong: convert category ID to Long"); //@ToDo LOGGER!
            return null;
        }

        // **** create a Pageable instance for pagination
        // ********* attempt to retrieve page number and size
        if (pagenum != null && pagesize != null) {
            try {
                pagenumAsInt = Integer.parseInt(pagenum);
                pagesizeAsInt = Integer.parseInt(pagesize);
            } catch (Exception e) {
                System.out.println("Something was going wrong: convert page and/or size to Int"); //@ToDo LOGGER!
                return null;
            }
        }

        // ********* create instance with sorting
        if (sort != null) {
            pageable = PageRequest.of(pagenumAsInt, pagesizeAsInt, sort);
        } else {
            pageable = PageRequest.of(pagenumAsInt, pagesizeAsInt);
        }

        // get IDs of categories and subcategories which are included to the category
        List<Long> subcategoryIds = categoryRepository.findSubcategoryIdsRecursive(categoryIdAsLong);

        if (search != null) {
            // SEARCHING CASE
            productPage =
                    productRepository.findAllByCategoryIdsAndSearchAndPriceRange(subcategoryIds, search, minAsBigDecimal, maxAsBigDecimal, pageable);
        } else {
            // NON-SEARCH CASE
            productPage =
                    productRepository.findAllByCategoryIdsAndPriceRange(subcategoryIds, minAsBigDecimal, maxAsBigDecimal, pageable);
        }

        // category issue
        Category category = categoryRepository.findById(categoryIdAsLong).orElse(null);
        CategoryDTO categoryDTO = null;
        if (category != null) {
            categoryDTO = categoryMapper.fromCategory(category, true);
        }

        return new ProductPageDTO(
                productPage.getNumber(),
                productPage.getTotalPages(),
                productMapper.fromProductList(productPage.getContent(), false),
                categoryDTO);
    }

    @Override
    public ProductFullDTO getProductByIdFullWithOrdersAndCategory(Long productId) {

        return new ProductFullDTO(
                getProductById(productId, true),
                orderMapper.fromOrderList(orderRepository.findByProductId(productId)),
                categoryMapper.fromCategory(productRepository.findCategoryByProductId(productId).orElse(null), false));
    }

}
