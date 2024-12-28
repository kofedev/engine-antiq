package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.DescriptorDTO;
import dev.kofe.kengine.model.Descriptor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DescriptorMapper {

//    private final BigValueMapper bigValueMapper;
    private final LanguageMapper languageMapper;

    public DescriptorMapper (LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }

    public List<DescriptorDTO> mapperFromDescriptorListToListDTO (List<Descriptor> descriptors) {
        if (descriptors == null) return null;
        List<DescriptorDTO> descriptorDTOList = new ArrayList<>();
        for (Descriptor descriptor : descriptors) {
            DescriptorDTO descriptorDTO = new DescriptorDTO();
            descriptorDTO.setDescriptorId(descriptor.getDescriptorId());
            descriptorDTO.setIsBig(descriptor.getIsBig());
            descriptorDTO.setIsSearchable(descriptor.getIsSearchable());
            descriptorDTO.setLanguage(languageMapper.fromLanguage(descriptor.getLanguage()));
            if (descriptor.getIsBig()) {
                descriptorDTO.setValue(descriptor.getBigValue().getValue());
            } else {
                descriptorDTO.setValue(descriptor.getValue());
            }
            descriptorDTOList.add(descriptorDTO);
        }

        return descriptorDTOList;
    }

}


