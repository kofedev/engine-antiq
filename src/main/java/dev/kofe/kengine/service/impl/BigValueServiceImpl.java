package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.model.BigValue;
import dev.kofe.kengine.repository.BigValueRepository;
import dev.kofe.kengine.service.BigValueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BigValueServiceImpl implements BigValueService {

    private final BigValueRepository bigValueRepository;

    public BigValueServiceImpl (BigValueRepository bigValueRepository) {
        this.bigValueRepository = bigValueRepository;
    }

    @Override
    public BigValue createAndSaveEmptyBigValue() {
        return bigValueRepository.save(new BigValue());
    }

    @Override
    public BigValue saveStateBigValue(BigValue bigValue) {
        return bigValueRepository.save(bigValue);
    }
}
