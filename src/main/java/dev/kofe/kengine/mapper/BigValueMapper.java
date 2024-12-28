package dev.kofe.kengine.mapper;

import dev.kofe.kengine.dto.BigValueDTO;
import dev.kofe.kengine.model.BigValue;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BigValueMapper {

    public BigValueDTO fromBigValue (BigValue bigValue) {
        if (bigValue == null) return null;
        BigValueDTO bigValueDTO = new BigValueDTO();
        BeanUtils.copyProperties(bigValue, bigValueDTO);
        return bigValueDTO;
    }

}
