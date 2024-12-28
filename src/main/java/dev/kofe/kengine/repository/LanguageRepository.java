package dev.kofe.kengine.repository;

import dev.kofe.kengine.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    Language findByIsInitialIsTrue();
    List<Language> findAllByIsActiveIsTrue();
    Language findByByDefaultIsTrue();
}
