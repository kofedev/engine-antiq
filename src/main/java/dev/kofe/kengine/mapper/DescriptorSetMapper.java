package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.DescriptorSetDTO;
import dev.kofe.kengine.dto.UserDTO;
import dev.kofe.kengine.model.Descriptor;
import dev.kofe.kengine.model.DescriptorSet;
import dev.kofe.kengine.model.Role;
import dev.kofe.kengine.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class DescriptorSetMapper {

    private final DescriptorMapper descriptorMapper;

    public DescriptorSetMapper (DescriptorMapper descriptorMapper) {
        this.descriptorMapper = descriptorMapper;
    }

}
