package dev.kofe.kengine.service;

import dev.kofe.kengine.dto.DescriptorSetDTO;
import dev.kofe.kengine.model.DescriptorSet;

import java.util.List;

public interface DescriptorSetService {
    DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage();
    DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage(DescriptorSet descriptorSet);
    DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage_BigDescriptors();
    DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage_BigDescriptors(DescriptorSet descriptorSet);
    List<DescriptorSet> getAllDescriptorSets();
    DescriptorSetDTO getDescriptorSetById(Long id);
    DescriptorSet saveStateDescriptorSet (DescriptorSet descriptorSet);
    void updateValuesOnlyOfDescriptorsOnly(DescriptorSetDTO descriptorSetDTO);
}
