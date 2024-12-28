package dev.kofe.kengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "languages")
@Data
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @Basic
    @Column(name = "language_code")
    private String languageCode;

    @Basic
    @Column(name = "language_name")
    private String languageName;

    @Basic
    @Column(name = "is_initial")
    private Boolean isInitial = false;

    @Basic
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Basic
    @Column(name = "by_default")
    private Boolean byDefault = false;

    @OneToMany (mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Descriptor> descriptors = new ArrayList<>();

    public void addDescriptor(Descriptor descriptor) {
        this.descriptors.add(descriptor);
        descriptor.setLanguage(this);
    }

    public void removeDescriptor(Descriptor descriptor) {
        this.descriptors.remove(descriptor);
        descriptor.setLanguage(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return languageId.equals(language.languageId)
                && Objects.equals(languageCode, language.languageCode)
                && Objects.equals(languageName, language.languageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageId, languageCode, languageName);
    }

    @Override
    public String toString() {
        return "User{" +
                "languageId=" + languageId   +
                ", code='"    + languageCode + '\'' +
                ", name='"    + languageName + '\'' +
                '}';
    }

}
