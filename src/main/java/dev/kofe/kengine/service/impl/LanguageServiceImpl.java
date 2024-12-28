package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.LanguageDTO;
import dev.kofe.kengine.mapper.LanguageMapper;
import dev.kofe.kengine.model.*;
import dev.kofe.kengine.repository.*;
import dev.kofe.kengine.service.LanguageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static dev.kofe.kengine.constant.KEConstant.*;

@Service
@Transactional
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final DescriptorSetRepository descriptorSetRepository;
    private final BigValueRepository bigValueRepository;
    private final DescriptorRepository descriptorRepository;
    private final LanguageMapper languageMapper;

    public LanguageServiceImpl (LanguageRepository languageRepository,
                                DescriptorSetRepository descriptorSetRepository,
                                BigValueRepository bigValueRepository,
                                DescriptorRepository descriptorRepository,
                                LanguageMapper languageMapper
                                ) {
        this.languageRepository = languageRepository;
        this.descriptorSetRepository = descriptorSetRepository;
        this.bigValueRepository = bigValueRepository;
        this.descriptorRepository = descriptorRepository;
        this.languageMapper = languageMapper;
    }


    @Override
    public void createInitialLanguage() {
        Language initialLanguage = new Language();
        initialLanguage.setLanguageCode(INITIAL_LANGUAGE_CODE);
        initialLanguage.setLanguageName(INITIAL_LANGUAGE_NAME);
        initialLanguage.setIsInitial(true);
        initialLanguage.setByDefault(true);
        languageRepository.save(initialLanguage);
    }

    @Override
    public List<LanguageDTO> getAllLanguages() {
        return languageMapper.fromLanguageList(languageRepository.findAll());
    }

    @Override
    public List<LanguageDTO> getAllActiveLanguages() {
        return languageMapper.fromLanguageList(languageRepository.findAllByIsActiveIsTrue());
    }

    @Override
    public LanguageDTO getLanguageById(Long languageId) {
        return languageMapper.fromLanguage(languageRepository.findById(languageId).orElse(null));
    }

    //@ToDO set values from initial language
    @Override
    public LanguageDTO addNewLanguageAndExpandDescriptors(LanguageDTO languageDTO) {
        // First: create and save new language
        Language newLanguage = new Language();
        newLanguage.setLanguageCode(languageDTO.getLanguageCode()); // **** CODE **** //
        newLanguage.setLanguageName(languageDTO.getLanguageName()); // **** NAME **** //
        newLanguage.setIsActive(languageDTO.getIsActive());         // **** ACTIVE STATUS **** //
        newLanguage.setByDefault(false);
        languageRepository.save(newLanguage);

        // Second: expand descriptors for each descriptor set (per one new descriptor for each set)
        List<DescriptorSet> descriptorSets = descriptorSetRepository.findAll();

        for (DescriptorSet descriptorSet : descriptorSets) {

            // analyse the descriptor set
            Descriptor descriptorPattern = new Descriptor(); // create a pattern
            List<Descriptor> descriptors = descriptorSet.getDescriptors();
            if (!descriptors.isEmpty()) {
                // get the pattern: from initial language
                for (Descriptor descriptor : descriptors) {
                    if (descriptor.getLanguage().getIsInitial()) {
                        descriptorPattern = descriptor;
                    }
                }
            } else {
                // an exception case! So, create pattern by default
            }

            // So, create new descriptor (for new language)
            Descriptor newDescriptor = descriptorRepository.save(new Descriptor());
            newDescriptor.setIsSearchable(descriptorPattern.getIsSearchable());

            // Big Value matter:
            if (descriptorPattern.getBigValue() != null) {
                // create BigValue
                BigValue bigValue = new BigValue();
                bigValue.setValue("");
                bigValue.setValue(descriptorPattern.getBigValue().getValue());
                bigValueRepository.save(bigValue);
                newDescriptor.setIsBig(true);
                //// first symbols to short value (to usability)
                newDescriptor.setValue(bigValue.getValue().
                        substring(0, Math.min(LENGTH_FIRST_SYMBOLS_FROM_BIG_VALUE_TO_USE_IN_SHORT_VALUE,
                                bigValue.getValue().length())));
                bigValue.setDescriptor(newDescriptor);
                bigValueRepository.save(bigValue);
                newDescriptor.setBigValue(bigValue);
                descriptorRepository.save(newDescriptor);
            } else {
                newDescriptor.setValue(descriptorPattern.getValue());
            }

            descriptorRepository.save(newDescriptor);
            newLanguage.addDescriptor(newDescriptor);
            descriptorSet.addDescriptor(newDescriptor);
            descriptorSetRepository.save(descriptorSet);

        } // for (DescriptorSet descriptorSet : descriptorSets

        languageRepository.save(newLanguage);
        return languageMapper.fromLanguage(newLanguage);
    }

    @Override
    public LanguageDTO updateLanguage(LanguageDTO languageDTO, Long languageId) {
        Language language = languageRepository.findById(languageId).orElse(null);
        if (language != null) {
            language.setLanguageCode(languageDTO.getLanguageCode());
            language.setLanguageName(languageDTO.getLanguageName());
            language.setIsActive(languageDTO.getIsActive());
            languageRepository.save(language);
            return languageMapper.fromLanguage(language);
        } else {
            return null;
        }
    }

    @Override
    public Boolean deleteLanguage(Long languageId) {
        boolean result = false;
        Language language = languageRepository.findById(languageId).orElse(null);
        if (language != null) {
            if (!language.getIsInitial()) {
                languageRepository.delete(language);
                result = true;
            }
        }

        return result;
    }

    @Override
    public LanguageDTO getInitialLanguage() {
        return languageMapper.fromLanguage(languageRepository.findByIsInitialIsTrue());
    }

    @Override
    public LanguageDTO setDefaultLanguage(Long languageId) {
        Language languageToSetDefaultStatus = languageRepository.findById(languageId).orElse(null);
        if (languageToSetDefaultStatus == null) return null;
        Language currentDefaultLanguage = languageRepository.findByByDefaultIsTrue();
        if (currentDefaultLanguage != null) {
            currentDefaultLanguage.setByDefault(false);
            languageToSetDefaultStatus.setByDefault(true);
        }

        return languageMapper.fromLanguage(languageToSetDefaultStatus);
    }

}
