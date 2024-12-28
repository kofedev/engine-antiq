package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.ProductDTO;
import dev.kofe.kengine.model.DescriptorSet;
import dev.kofe.kengine.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductMapper {

    private final LanguageMapper languageMapper;
    private final DescriptorMapper descriptorMapper;
    private final CategoryParentMapper categoryParentMapper;
    private final ImageMapper imageMapper;
    public ProductMapper (LanguageMapper languageMapper,
                          DescriptorMapper descriptorMapper,
                          CategoryParentMapper categoryParentMapper,
                          ImageMapper imageMapper) {
        this.languageMapper = languageMapper;
        this.descriptorMapper = descriptorMapper;
        this.categoryParentMapper = categoryParentMapper;
        this.imageMapper = imageMapper;
    }

    public ProductDTO fromProduct(Product product, boolean isFull) {
        if (product == null) return null;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setActive(product.getActive());
        productDTO.setPartNumber(product.getPartNumber());
        productDTO.setOfferPrice(product.getOfferPrice());
        productDTO.setCurrentQuantity(product.getCurrentQuantity());
        productDTO.setPublishedOn(product.getPublishedOn());
        productDTO.setKeyWords(product.getKeyWords());
        productDTO.setNote(product.getNote());
        // category
        productDTO.setCategory(categoryParentMapper.fromCategory(product.getCategory()));
        // images matter
        productDTO.setImages(imageMapper.fromImageListToImageDTOList(product.getImages()));

        // title and brief descriptors
        productDTO.setTitleDescriptors(descriptorMapper.mapperFromDescriptorListToListDTO(product.getTitleSet().getDescriptors()));
        productDTO.setBriefDescriptors(descriptorMapper.mapperFromDescriptorListToListDTO(product.getBriefSet().getDescriptors()));

        DescriptorSet descriptorSet = product.getFullSet();

        // full descriptors matter
        if (isFull) {
            productDTO.setFullDescriptors(descriptorMapper.mapperFromDescriptorListToListDTO(product.getFullSet().getDescriptors()));
        } else {
            productDTO.setFullDescriptorSetId(product.getFullSet().getDescriptorSetId());
        }

        return productDTO;
    }

    public List<ProductDTO> fromProductList (List<Product> productList, boolean isFull) {
        if (productList == null) return null;
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product : productList) {
            productDTOList.add(fromProduct(product, isFull));
        }

        return productDTOList;
    }

}
