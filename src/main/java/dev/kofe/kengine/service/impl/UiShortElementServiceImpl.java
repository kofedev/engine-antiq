package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.DescriptorDTO;
import dev.kofe.kengine.dto.UiElementDTO;
import dev.kofe.kengine.dto.UiElementOneLanguageDTO;
import dev.kofe.kengine.mapper.LanguageMapper;
import dev.kofe.kengine.model.*;
import dev.kofe.kengine.repository.DescriptorRepository;
import dev.kofe.kengine.repository.DescriptorSetRepository;
import dev.kofe.kengine.repository.LanguageRepository;
import dev.kofe.kengine.repository.UiShortElementRepository;
import dev.kofe.kengine.service.UiShortElementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UiShortElementServiceImpl implements UiShortElementService {

    private final UiShortElementRepository uiShortElementRepository;
    private final DescriptorSetRepository descriptorSetRepository;
    private final LanguageRepository languageRepository;
    private final DescriptorRepository descriptorRepository;
    private final LanguageMapper languageMapper;

    public UiShortElementServiceImpl (UiShortElementRepository uiShortElementRepository, DescriptorSetRepository descriptorSetRepository,
                                      LanguageRepository languageRepository, DescriptorRepository descriptorRepository,
                                      LanguageMapper languageMapper) {
        this.uiShortElementRepository = uiShortElementRepository;
        this.descriptorSetRepository = descriptorSetRepository;
        this.languageRepository = languageRepository;
        this.descriptorRepository = descriptorRepository;
        this.languageMapper = languageMapper;
    }

    @Override
    public void createNewUiShortElementAndExpandForEachLanguage(UiElementOneLanguageDTO uiElementOneLanguageDTO) {
        int currentMaxIndex;
        UiShortElement uiShortElementToRegister = new UiShortElement();
        UiShortElement uiShortElementWithMaxKey = null;

        // **** GENERATION NEW KEY ****
        uiShortElementWithMaxKey = uiShortElementRepository.findUiShortElementWithMaxKey();

        if (uiShortElementWithMaxKey != null) {
            currentMaxIndex = uiShortElementWithMaxKey.getKey();
        } else {
            currentMaxIndex = -1;
        }

        uiShortElementToRegister.setKey(currentMaxIndex + 1);
        uiShortElementRepository.save(uiShortElementToRegister);

        // **** go expand

        DescriptorSet descriptorSet = new DescriptorSet();
        descriptorSetRepository.save(descriptorSet);

        uiShortElementToRegister.setValueSet(descriptorSet);
        uiShortElementRepository.save(uiShortElementToRegister); //@ToDo --- attention

        List<Language> languageList = languageRepository.findAll();
        for (Language language : languageList) {
            Descriptor descriptor = new Descriptor();
            descriptorRepository.save(descriptor);
            descriptor.setLanguage(language);
            language.addDescriptor(descriptor);
            languageRepository.save(language); //// ???
            descriptor.setValue(uiElementOneLanguageDTO.getValue());
            descriptor.setIsBig(false);
            descriptorRepository.save(descriptor);
            descriptorSet.addDescriptor(descriptor);
        }

        descriptorSetRepository.save(descriptorSet);
    }

    @Override
    public UiElementDTO getUiElementByKey(int key) {
        UiShortElement uiShortElement = uiShortElementRepository.findByKey(key);
        if (uiShortElement == null) return null;

        List<DescriptorDTO> descriptorDTOList = new ArrayList<>();
        for (Descriptor descriptor : uiShortElement.getValueSet().getDescriptors()) {
            DescriptorDTO descriptorDTO = new DescriptorDTO();
            descriptorDTO.setDescriptorId(descriptor.getDescriptorId());
            descriptorDTO.setIsBig(false);
            descriptorDTO.setDescriptorId(descriptor.getDescriptorId());
            descriptorDTO.setIsSearchable(descriptor.getIsSearchable());
            descriptorDTO.setValue(descriptor.getValue());

            descriptorDTO.setLanguage(languageMapper.fromLanguage(descriptor.getLanguage()));
            descriptorDTOList.add(descriptorDTO);
        }

        UiElementDTO uiElementDTO = new UiElementDTO();
        uiElementDTO.setIsBig(false);
        uiElementDTO.setKey(uiShortElement.getKey());
        uiElementDTO.setDescriptors(descriptorDTOList);
        uiElementDTO.setSetId(uiShortElement.getValueSet().getDescriptorSetId());
        uiElementDTO.setUiElementId(uiShortElement.getUiShortElementId());

        if ((uiElementDTO.getKey() + 1) == uiShortElementRepository.count()) {
            uiElementDTO.setIsLast(true);
        }

        return uiElementDTO;
    }

    @Override
    public void deleteUiElement(Long elementId) {
        UiShortElement uiShortElementToDelete = uiShortElementRepository.findById(elementId).orElse(null);
        if (uiShortElementToDelete != null) {
            if ((uiShortElementToDelete.getKey() + 1) == uiShortElementRepository.count()) {
                // ok: it is a last element
                if (uiShortElementToDelete.getValueSet() != null) {
                    DescriptorSet descriptorSet = descriptorSetRepository.findById(uiShortElementToDelete.getValueSet().getDescriptorSetId()).orElse(null);
                    uiShortElementToDelete.setValueSet(null); // unchain
                    if (descriptorSet != null) {
                        descriptorSetRepository.delete(descriptorSet);
                    }
                }
                uiShortElementRepository.delete(uiShortElementToDelete);
            }
        }
    }


    private List<UiElementOneLanguageDTO> getAllUiShortElementsByLanguage(Long languageId) {
        List<Object[]> result = uiShortElementRepository.findUiElementShortValuesForLanguage(languageId);
        return result.stream()
                .map(row -> {
                    UiElementOneLanguageDTO uiElementOneLanguageDTO = new UiElementOneLanguageDTO();
                    uiElementOneLanguageDTO.setKey((Integer) row[0]);
                    uiElementOneLanguageDTO.setValue((String) row[1]);
                    return uiElementOneLanguageDTO;
                })
                .collect(Collectors.toList());
    }


    @Override
    public String[] getUiShortsElementsByLanguage(Long languageId) {

        List<UiElementOneLanguageDTO> uiElementDTOList = getAllUiShortElementsByLanguage (languageId);

        String[] uiShortElementsArray = new String[uiElementDTOList.size()];
        Arrays.fill(uiShortElementsArray, "");  //@ToDo need to laboratory experiment -- to delete
        for (UiElementOneLanguageDTO uiElementDTO : uiElementDTOList) {
            int index = uiElementDTO.getKey();
            if (index >= 0 && index < uiElementDTOList.size()) {
                uiShortElementsArray[index] = uiElementDTO.getValue();
            }
        }

        return uiShortElementsArray;
    }

    @Override
    public List<UiElementOneLanguageDTO> getAllUiShortElementsForInitialLanguage() {
        // get an initial language
        Language initialLanguage = languageRepository.findByIsInitialIsTrue();
        // prepare list
        List<UiElementOneLanguageDTO> uiElementInitialLanguageDTOList = new ArrayList<>();
        // get all shorts
        List<UiShortElement> uiShortElementList = uiShortElementRepository.findAll();
        // scan all
        Descriptor descriptorForInitialLanguage = new Descriptor();
        for (UiShortElement uiShortElement : uiShortElementList) {
            for (Descriptor descriptor : uiShortElement.getValueSet().getDescriptors()) {
                if (descriptor.getLanguage().getLanguageId().equals(initialLanguage.getLanguageId())) {
                    descriptorForInitialLanguage = descriptor;
                }
            }
            UiElementOneLanguageDTO uiElementDTO = new UiElementOneLanguageDTO();
            uiElementDTO.setIsBig(false);
            uiElementDTO.setKey(uiShortElement.getKey());
            uiElementDTO.setValue(descriptorForInitialLanguage.getValue());
            uiElementInitialLanguageDTOList.add(uiElementDTO);
        }

        return uiElementInitialLanguageDTOList;
    }

}
