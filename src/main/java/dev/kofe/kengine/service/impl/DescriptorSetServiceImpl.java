package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.DescriptorDTO;
import dev.kofe.kengine.dto.DescriptorSetDTO;
import dev.kofe.kengine.mapper.BigValueMapper;
import dev.kofe.kengine.mapper.LanguageMapper;
import dev.kofe.kengine.model.BigValue;
import dev.kofe.kengine.model.Descriptor;
import dev.kofe.kengine.model.DescriptorSet;
import dev.kofe.kengine.model.Language;
import dev.kofe.kengine.repository.BigValueRepository;
import dev.kofe.kengine.repository.DescriptorRepository;
import dev.kofe.kengine.repository.DescriptorSetRepository;
import dev.kofe.kengine.repository.LanguageRepository;
import dev.kofe.kengine.service.DescriptorSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static dev.kofe.kengine.constant.KEConstant.LENGTH_FIRST_SYMBOLS_FROM_BIG_VALUE_TO_USE_IN_SHORT_VALUE;

@Service
@Transactional
public class DescriptorSetServiceImpl implements DescriptorSetService {

    private final DescriptorSetRepository descriptorSetRepository;
    private final LanguageRepository languageRepository;
    private final DescriptorRepository descriptorRepository;
    private final BigValueRepository bigValueRepository;
    private final LanguageMapper languageMapper;
    private final BigValueMapper bigValueMapper;

    public DescriptorSetServiceImpl (DescriptorSetRepository descriptorSetRepository,
                                     DescriptorRepository descriptorRepository,
                                     LanguageRepository languageRepository,
                                     BigValueRepository bigValueRepository,
                                     LanguageMapper languageMapper,
                                     BigValueMapper bigValueMapper) {
        this.descriptorSetRepository = descriptorSetRepository;
        this.descriptorRepository = descriptorRepository;
        this.languageRepository = languageRepository;
        this.bigValueRepository = bigValueRepository;
        this.languageMapper = languageMapper;
        this.bigValueMapper = bigValueMapper;
    }

    @Override
    public DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage() {

        DescriptorSet newDescriptorSet = descriptorSetRepository.save(new DescriptorSet());

        List<Language> allLanguages = languageRepository.findAll();
        for (Language language : allLanguages) {
            Descriptor newDescriptor = descriptorRepository.save(new Descriptor());
            newDescriptor.setValue("");
            language.addDescriptor(newDescriptor);
            languageRepository.save(language);
            newDescriptorSet.addDescriptor(newDescriptor);
        }
        descriptorSetRepository.save(newDescriptorSet);

        return newDescriptorSet;
    }

    // create and clone info from patter descriptorSet
    @Override
    public DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage(DescriptorSet descriptorSet) {

        DescriptorSet newDescriptorSet = descriptorSetRepository.save(new DescriptorSet());

        List<Language> allLanguages = languageRepository.findAll();
        for (Language language : allLanguages) {
            Descriptor newDescriptor = descriptorRepository.save(new Descriptor());

            for (Descriptor descriptor : descriptorSet.getDescriptors()) {
                if (descriptor.getLanguage().getLanguageId().equals(language.getLanguageId())) {
                    newDescriptor.setValue(descriptor.getValue() + " copy");
                    break;
                }
            }

            language.addDescriptor(newDescriptor);
            languageRepository.save(language);
            newDescriptorSet.addDescriptor(newDescriptor);
        }
        descriptorSetRepository.save(newDescriptorSet);

        return newDescriptorSet;
    }


    @Override
    public DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage_BigDescriptors() {

        DescriptorSet newDescriptorSet = descriptorSetRepository.save(new DescriptorSet());

        List<Language> allLanguages = languageRepository.findAll();
        for (Language language : allLanguages) {
            Descriptor newDescriptor = descriptorRepository.save(new Descriptor());
            newDescriptor.setIsBig(true);
            language.addDescriptor(newDescriptor);
            languageRepository.save(language);
            BigValue bigValue = new BigValue();
            bigValue.setValue("");
            bigValueRepository.save(bigValue);
            newDescriptor.setBigValue(bigValue);
            bigValue.setDescriptor(newDescriptor);
            descriptorRepository.save(newDescriptor);
            bigValueRepository.save(bigValue);

            newDescriptorSet.addDescriptor(newDescriptor);
        }
        descriptorSetRepository.save(newDescriptorSet);

        return newDescriptorSet;
    }

    @Override
    public DescriptorSet createDescriptorSetAndExpandDescriptorsEachForLanguage_BigDescriptors(DescriptorSet descriptorSet) {

        DescriptorSet newDescriptorSet = descriptorSetRepository.save(new DescriptorSet());

        List<Language> allLanguages = languageRepository.findAll();
        for (Language language : allLanguages) {
            Descriptor newDescriptor = descriptorRepository.save(new Descriptor());
            newDescriptor.setIsBig(true);

            for (Descriptor descriptor : descriptorSet.getDescriptors()) {
                if (descriptor.getLanguage().getLanguageId().equals(language.getLanguageId())) {
                    newDescriptor.setValue(descriptor.getValue() + " copy");
                    break;
                }
            }

            language.addDescriptor(newDescriptor);
            languageRepository.save(language);

            BigValue bigValue = new BigValue();

            for (Descriptor descriptor : descriptorSet.getDescriptors()) {
                if (descriptor.getLanguage().getLanguageId().equals(language.getLanguageId())) {
                    if (descriptor.getBigValue() != null) {
                        bigValue.setValue(descriptor.getBigValue().getValue() + " copy");
                    }
                    break;
                }
            }

            bigValueRepository.save(bigValue);
            newDescriptor.setBigValue(bigValue);
            bigValue.setDescriptor(newDescriptor);
            descriptorRepository.save(newDescriptor);
            bigValueRepository.save(bigValue);

            newDescriptorSet.addDescriptor(newDescriptor);
        }
        descriptorSetRepository.save(newDescriptorSet);

        return newDescriptorSet;
    }


    @Override
    public List<DescriptorSet> getAllDescriptorSets() {
        return descriptorSetRepository.findAll();
    }

    @Override
    public DescriptorSet saveStateDescriptorSet(DescriptorSet descriptorSet) {
        return descriptorSetRepository.save(descriptorSet);
    }

    @Override
    public DescriptorSetDTO getDescriptorSetById(Long id) {
        DescriptorSetDTO descriptorSetDTO = null;
        DescriptorSet descriptorSet = descriptorSetRepository.findById(id).orElse(null);
        if (descriptorSet != null) {
            descriptorSetDTO = new DescriptorSetDTO();
            List<DescriptorDTO> descriptorDTOS = new ArrayList<>();
            for (Descriptor descriptor : descriptorSet.getDescriptors()) {
                DescriptorDTO descriptorDTO = new DescriptorDTO();
                descriptorDTO.setDescriptorId(descriptor.getDescriptorId());
                descriptorDTO.setIsSearchable(descriptor.getIsSearchable());
                descriptorDTO.setIsBig(descriptor.getIsBig());
                descriptorDTO.setLanguage(languageMapper.fromLanguage(descriptor.getLanguage()));
                if (descriptor.getIsBig() && descriptor.getBigValue() != null) {
                    // big case
                    if (descriptor.getBigValue() != null) {
                        descriptorDTO.setValue(descriptor.getBigValue().getValue());
                    } else {
                        descriptorDTO.setValue("");
                    }

                } else {
                    // short case
                    descriptorDTO.setValue(descriptor.getValue());
                }
                descriptorDTOS.add(descriptorDTO);
            }
            descriptorSetDTO.setDescriptorSetId(descriptorSet.getDescriptorSetId());
            descriptorSetDTO.setDescriptors(descriptorDTOS);
        }

        return descriptorSetDTO;
    }

    // update descriptors which "packed" to the set for transferring
    // touch BIG-VALUES too
    @Override
    public void updateValuesOnlyOfDescriptorsOnly(DescriptorSetDTO descriptorSetDTO) {
        for (DescriptorDTO descriptorDTOWithNewValue : descriptorSetDTO.getDescriptors()) {
            Descriptor descriptorToUpdate = descriptorRepository.findById(descriptorDTOWithNewValue.getDescriptorId()).orElse(null);
            if (descriptorToUpdate != null) {
                if (descriptorToUpdate.getIsBig()) {
                    // BIG CASE
                    BigValue bigValueToUpdate = bigValueRepository.findById(descriptorToUpdate.getBigValue().getBigValueId()).orElse(null);
                    if (bigValueToUpdate != null) {
                        bigValueToUpdate.setValue(descriptorDTOWithNewValue.getValue());
                        bigValueRepository.save(bigValueToUpdate);
                        // first symbols to short value (to usability)
                        descriptorToUpdate.setValue(descriptorDTOWithNewValue.getValue().
                                substring(0, Math.min(LENGTH_FIRST_SYMBOLS_FROM_BIG_VALUE_TO_USE_IN_SHORT_VALUE,
                                        descriptorDTOWithNewValue.getValue().length())));
                        descriptorRepository.save(descriptorToUpdate);
                    }
                } else {
                    // SHORT CASE
                    descriptorToUpdate.setValue(descriptorDTOWithNewValue.getValue());
                    descriptorRepository.save(descriptorToUpdate);
                }
            }
        }
    }


}
