package dev.kofe.kengine.service;

import dev.kofe.kengine.model.Descriptor;

public interface DescriptorService {
    Descriptor createAndSaveEmptyDescriptor();
    Descriptor createAndSaveDescriptorWithBigValue(String value);
    //Descriptor saveDescriptorState(Descriptor descriptor);

}
