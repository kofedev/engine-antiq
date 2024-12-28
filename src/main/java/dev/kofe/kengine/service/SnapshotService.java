package dev.kofe.kengine.service;

import org.springframework.core.io.Resource;

public interface SnapshotService {
     Resource productsSnapshot ();
     Resource categoriesSnapshot ();
}
