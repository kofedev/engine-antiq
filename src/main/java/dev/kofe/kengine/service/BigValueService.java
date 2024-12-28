package dev.kofe.kengine.service;

import dev.kofe.kengine.model.BigValue;

public interface BigValueService {
    BigValue createAndSaveEmptyBigValue();
    BigValue saveStateBigValue(BigValue bigValue);
}
