package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.model.BigValue;
import dev.kofe.kengine.model.Descriptor;
import dev.kofe.kengine.repository.BigValueRepository;
import dev.kofe.kengine.repository.DescriptorRepository;
import dev.kofe.kengine.service.DescriptorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DescriptorServiceImpl implements DescriptorService {

    private final DescriptorRepository descriptorRepository;
    private final BigValueRepository bigValueRepository;

    public DescriptorServiceImpl (DescriptorRepository descriptorRepository,
                                  BigValueRepository bigValueRepository) {
        this.descriptorRepository = descriptorRepository;
        this.bigValueRepository = bigValueRepository;
    }

    @Override
    public Descriptor createAndSaveEmptyDescriptor() {
        return descriptorRepository.save(new Descriptor());
    }

    @Override
    public Descriptor createAndSaveDescriptorWithBigValue(String value) {
        Descriptor descriptor = new Descriptor();
        descriptor.setIsBig(true);
        descriptorRepository.save(descriptor);
        BigValue bigValue = bigValueRepository.save(new BigValue());
        bigValue.setValue(value);
        descriptor.setBigValue(bigValue);
        bigValue.setDescriptor(descriptor);
        descriptorRepository.save(descriptor);
        bigValueRepository.save(bigValue);
        return descriptor;
    }

}
