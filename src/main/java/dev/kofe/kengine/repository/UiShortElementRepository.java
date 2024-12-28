package dev.kofe.kengine.repository;

import dev.kofe.kengine.dto.UiElementOneLanguageDTO;
import dev.kofe.kengine.model.UiShortElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UiShortElementRepository extends JpaRepository<UiShortElement, Long> {

    @Query("SELECT u FROM UiShortElement u WHERE u.key = (SELECT MAX(u2.key) FROM UiShortElement u2)")
    UiShortElement findUiShortElementWithMaxKey();

    UiShortElement findByKey(int key);

    @Query("SELECT ue.key, d.value FROM UiShortElement ue " +
        "JOIN ue.valueSet.descriptors d " +
        "JOIN d.language l " +
        "WHERE l.languageId = :languageId")
    List<Object[]> findUiElementShortValuesForLanguage(@Param("languageId") Long languageId);

}
