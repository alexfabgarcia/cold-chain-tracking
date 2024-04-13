package br.ufscar.ppgcc.data;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Entity
@Table(name = "product_sensor_type")
public class ProductSensorType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="sensor_type_id", nullable=false)
    private SensorType sensorType;

    private Double minimum;

    private Double maximum;

    protected ProductSensorType() {
    }

    public ProductSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public SensorType getSensorType() {
        return sensorType;
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

    public String getSensorTypeWithUnit() {
        return Optional.of(sensorType)
                .map(type -> String.format("%s (%s)", type.getName(), type.getMeasurementUnit())).orElse("");
    }

    public boolean isValid() {
        return nonNull(product) && nonNull(sensorType) && nonNull(minimum) && nonNull(maximum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSensorType that = (ProductSensorType) o;
        return Objects.equals(product, that.product) && Objects.equals(sensorType, that.sensorType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, sensorType);
    }

}
