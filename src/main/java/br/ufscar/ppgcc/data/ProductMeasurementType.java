package br.ufscar.ppgcc.data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Entity
@Table(name = "product_measurement_type")
@NamedEntityGraph(name = "ProductMeasurementType.eager", attributeNodes = {
        @NamedAttributeNode("product"),
        @NamedAttributeNode("measurementType")
})
public class ProductMeasurementType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="measurement_type_id", nullable=false)
    private MeasurementType measurementType;

    private Double minimum;

    private Double maximum;

    @Version
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    protected ProductMeasurementType() {
    }

    public ProductMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public Double getMinimum() {
        return minimum;
    }

    public void setMinimumSafely(String minimum) {
        try {
            this.minimum = Double.valueOf(minimum);
        } catch (NumberFormatException e) {
            this.minimum = null;
        }
    }

    public Double getMaximum() {
        return maximum;
    }

    public void setMaximumSafely(String maximum) {
        try {
            this.maximum = Double.valueOf(maximum);
        } catch (NumberFormatException e) {
            this.maximum = null;
        }
    }

    public String getMeasurementTypeWithUnit() {
        return Optional.of(measurementType)
                .map(type -> String.format("%s (%s)", type.getName(), type.getMeasurementUnit())).orElse("");
    }

    public boolean isValid() {
        return nonNull(product) && nonNull(measurementType) && nonNull(minimum) && nonNull(maximum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductMeasurementType that = (ProductMeasurementType) o;
        return Objects.equals(product, that.product) && Objects.equals(measurementType, that.measurementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, measurementType);
    }

}
