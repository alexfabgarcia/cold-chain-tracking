package br.ufscar.ppgcc.data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "product")
@NamedEntityGraph(
        name = "Product.measurementTypes",
        attributeNodes = @NamedAttributeNode(value = "measurementTypes", subgraph = "ProductMeasurementType.measurementType"),
        subgraphs = @NamedSubgraph(
                name = "ProductMeasurementType.measurementType",
                attributeNodes = @NamedAttributeNode(value = "measurementType")
        )
)
public class Product {

    public enum Category {
        VACCINE,
        ANTIVENOM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Version
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductMeasurementType> measurementTypes = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<ProductMeasurementType> getMeasurementTypes() {
        return measurementTypes;
    }

    public void setMeasurementTypes(Set<ProductMeasurementType> newMeasurementTypes) {
        var existingMeasurementMap = this.measurementTypes.stream()
                .collect(Collectors.toMap(Function.identity(), Function.identity()));
        this.measurementTypes = newMeasurementTypes.stream()
                .peek(a -> a.setProduct(this))
                .filter(ProductMeasurementType::isValid)
                .map(measurementType -> existingMeasurementMap.merge(measurementType, measurementType, (oldValue, value) -> {
                    oldValue.setMinimumSafely(value.getMinimum().toString());
                    oldValue.setMaximumSafely(value.getMaximum().toString());
                    return oldValue;
                }))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name) && category == product.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category);
    }

}
