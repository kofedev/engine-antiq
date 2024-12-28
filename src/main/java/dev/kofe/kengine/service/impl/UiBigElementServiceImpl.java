package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.DescriptorDTO;
import dev.kofe.kengine.dto.UiElementDTO;
import dev.kofe.kengine.dto.UiElementDTO2;
import dev.kofe.kengine.dto.UiElementOneLanguageDTO;
import dev.kofe.kengine.mapper.LanguageMapper;
import dev.kofe.kengine.model.*;
import dev.kofe.kengine.repository.*;
import dev.kofe.kengine.service.UiBigElementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UiBigElementServiceImpl implements UiBigElementService {

    private final UiBigElementRepository uiBigElementRepository;
    private final DescriptorSetRepository descriptorSetRepository;
    private final LanguageRepository languageRepository;
    private final DescriptorRepository descriptorRepository;
    private final LanguageMapper languageMapper;
    private final BigValueRepository bigValueRepository;

    public UiBigElementServiceImpl (UiBigElementRepository uiBigElementRepository, DescriptorSetRepository descriptorSetRepository,
                                      LanguageRepository languageRepository, DescriptorRepository descriptorRepository,
                                      LanguageMapper languageMapper, BigValueRepository bigValueRepository) {
        this.uiBigElementRepository = uiBigElementRepository;
        this.descriptorSetRepository = descriptorSetRepository;
        this.languageRepository = languageRepository;
        this.descriptorRepository = descriptorRepository;
        this.languageMapper = languageMapper;
        this.bigValueRepository = bigValueRepository;
    }


    @Override
    public void createNewUiBigElementAndExpandForEachLanguage(UiElementOneLanguageDTO uiElementToRegister) {
        int currentMaxIndex;
        UiBigElement uiBigElement = new UiBigElement();

        UiBigElement uiBigElementWithMaxKey = uiBigElementRepository.findUiBigElementWithMaxKey();

        // **** GENERATION NEW KEY ****

        if (uiBigElementWithMaxKey != null) {
            currentMaxIndex = uiBigElementWithMaxKey.getKey();
        } else {
            currentMaxIndex = -1;
        }
        //
        uiBigElement.setKey(currentMaxIndex + 1);
        uiBigElementRepository.save(uiBigElement);
        //
        DescriptorSet descriptorSet = new DescriptorSet();
        descriptorSetRepository.save(descriptorSet);
        uiBigElement.setValueSet(descriptorSet);
        uiBigElementRepository.save(uiBigElement); //// ???? //@ToDo

        List<Language> languageList = languageRepository.findAll();
        for (Language language : languageList) {
            Descriptor descriptor = new Descriptor();
            descriptorRepository.save(descriptor);
            descriptor.setLanguage(language);
            language.addDescriptor(descriptor);
            languageRepository.save(language); //// ???
            // BIG VALUE
            descriptor.setIsBig(true);
            descriptor.setValue(uiElementToRegister.getValue());
            BigValue bigValue = new BigValue();
            bigValueRepository.save(bigValue);
            bigValue.setDescriptor(descriptor);
            bigValue.setValue(uiElementToRegister.getValue());
            bigValueRepository.save(bigValue); //// ???
            descriptor.setBigValue(bigValue);
            descriptorRepository.save(descriptor);
            descriptorSet.addDescriptor(descriptor);
        }

        descriptorSetRepository.save(descriptorSet);
    }

    @Override
    public UiElementDTO getUiBigElementByKey(int key) {
        UiBigElement uiBigElement = uiBigElementRepository.findByKey(key);
        if (uiBigElement == null) return null;
        List<DescriptorDTO> descriptorDTOList = new ArrayList<>();
        for (Descriptor descriptor : uiBigElement.getValueSet().getDescriptors()) {
            DescriptorDTO descriptorDTO = new DescriptorDTO();
            descriptorDTO.setDescriptorId(descriptor.getDescriptorId());
            // BIG VALUE
            descriptorDTO.setIsBig(true);
            if (descriptor.getBigValue() != null) {
                 descriptorDTO.setValue(descriptor.getBigValue().getValue());
            } else {
                 descriptorDTO.setValue("");
            }

            descriptorDTO.setLanguage(languageMapper.fromLanguage(descriptor.getLanguage()));
            descriptorDTOList.add(descriptorDTO);
        }
        UiElementDTO uiElementDTO = new UiElementDTO();
        uiElementDTO.setKey(uiBigElement.getKey());
        uiElementDTO.setDescriptors(descriptorDTOList);
        uiElementDTO.setSetId(uiBigElement.getValueSet().getDescriptorSetId());
        uiElementDTO.setUiElementId(uiBigElement.getUiBigElementId());

        if ((uiElementDTO.getKey() + 1) == uiBigElementRepository.count()) {
            uiElementDTO.setIsLast(true);
        }

        return uiElementDTO;
    }

    @Override
    public void deleteUiBigElement(Long elementId) {
        UiBigElement uiBigElementToDelete = uiBigElementRepository.findById(elementId).orElse(null);
        if (uiBigElementToDelete != null) {

            if ((uiBigElementToDelete.getKey() + 1) == uiBigElementRepository.count()) {
                if (uiBigElementToDelete.getValueSet() != null) {
                    DescriptorSet descriptorSet = descriptorSetRepository.findById(uiBigElementToDelete.getValueSet().getDescriptorSetId()).orElse(null);
                    uiBigElementToDelete.setValueSet(null); // unchain
                    if (descriptorSet != null) {
                        descriptorSetRepository.delete(descriptorSet);
                    }
                }
                uiBigElementRepository.delete(uiBigElementToDelete);
            }
        }
    }

    @Override
    public List<UiElementOneLanguageDTO> getAllUiBigElementsForInitialLanguage() {
        // get an initial language
        Language initialLanguage = languageRepository.findByIsInitialIsTrue();
        // prepare list
        List<UiElementOneLanguageDTO> uiElementInitialLanguageDTOList = new ArrayList<>();
        // get all
        List<UiBigElement> uiBigElementList = uiBigElementRepository.findAll();
        // scan all
        Descriptor descriptorForInitialLanguage = new Descriptor();
        for (UiBigElement uiBigElement : uiBigElementList) {
            for (Descriptor descriptor : uiBigElement.getValueSet().getDescriptors()) {
                if (descriptor.getLanguage().getLanguageId().equals(initialLanguage.getLanguageId())) {
                    descriptorForInitialLanguage = descriptor;
                }
            }
            UiElementOneLanguageDTO uiElementDTO = new UiElementOneLanguageDTO();
            uiElementDTO.setIsBig(true);
            uiElementDTO.setKey(uiBigElement.getKey());
            uiElementDTO.setValue(descriptorForInitialLanguage.getValue());
            uiElementInitialLanguageDTOList.add(uiElementDTO);
        }

        return uiElementInitialLanguageDTOList;
    }

    @Override
    public UiElementDTO2 getTwoUiBigElementByKey(int keyOne, int keyTwo) {
        return new UiElementDTO2(getUiBigElementByKey(keyOne), getUiBigElementByKey(keyTwo));
    }

}
