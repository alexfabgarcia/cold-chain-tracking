package br.ufscar.ppgcc.data;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

@Entity
@Table(name = "freight")
@NamedEntityGraph(name = "Freight.details", attributeNodes = {
        @NamedAttributeNode("product"), @NamedAttributeNode("device"), @NamedAttributeNode("carrier")
})
@NamedEntityGraph(
        name = "Freight.productMeasurementsAndDetails",
        attributeNodes = {
                @NamedAttributeNode(value = "product", subgraph = "Product.measurements"),
                @NamedAttributeNode("device"), @NamedAttributeNode("carrier")
        },
        subgraphs = @NamedSubgraph(
                name = "Product.measurements",
                attributeNodes = @NamedAttributeNode(value = "measurementTypes")
        )
)
public class Freight {

    public enum Status {
        CREATED, STARTED, FINISHED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name="carrier_id")
    private Carrier carrier;

    @Type(JsonType.class)
    private GeolocationPoint origin;

    @Type(JsonType.class)
    private GeolocationPoint destination;

    private String description;

    private Boolean violated = Boolean.FALSE;

    @Column(name = "started_at", nullable = true, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime startedAt;

    @Column(name = "finished_at", nullable = true, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime finishedAt;

    @Version
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public GeolocationPoint getOrigin() {
        return origin;
    }

    public void setOrigin(GeolocationPoint origin) {
        this.origin = origin;
    }

    public GeolocationPoint getDestination() {
        return destination;
    }

    public void setDestination(GeolocationPoint destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public Optional<ZonedDateTime> getStartedAt() {
        return Optional.ofNullable(startedAt);
    }

    public Optional<ZonedDateTime> getFinishedAt() {
        return Optional.ofNullable(finishedAt);
    }

    public void start() {
        this.startedAt = ZonedDateTime.now();
    }

    public void setViolated() {
        violated = Boolean.TRUE;
    }

    public boolean isCreated() {
        return isNull(startedAt);
    }

    public boolean isStarted() {
        return isNull(finishedAt) && nonNull(startedAt);
    }

    public Status getStatus() {
        if (isCreated()) {
            return Status.CREATED;
        }
        if (isStarted()) {
            return Status.STARTED;
        }
        return Status.FINISHED;
    }

    public String getOriginAddress() {
        return Optional.ofNullable(origin).map(GeolocationPoint::address).orElse(null);
    }

    public String getDestinationAddress() {
        return Optional.ofNullable(destination).map(GeolocationPoint::address).orElse(null);
    }

    public String getProductName() {
        return Optional.ofNullable(product).map(Product::getName).orElse(null);
    }

    public String getDeviceName() {
        return Optional.ofNullable(device).map(Device::getName).orElse(null);
    }

    public String getCarrierName() {
        return Optional.ofNullable(carrier).map(Carrier::getFullName).orElse(null);
    }

    public List<String> getViolatedConditions(Map<String, Double> measurements) {
        var productMeasurements = getProductMeasurements();
        return measurements.entrySet().stream()
                .filter(entry -> {
                    var productMeasurement = productMeasurements.get(entry.getKey());
                    return productMeasurement != null && (entry.getValue() < productMeasurement.getMinimum() ||
                                    entry.getValue() > productMeasurement.getMaximum());
                })
                .map(Map.Entry::getKey)
                .toList();
    }

    private Map<String, ProductMeasurementType> getProductMeasurements() {
        return Optional.ofNullable(product)
                .map(Product::getMeasurementTypes).orElse(Collections.emptySet())
                .stream()
                .collect(toMap(item -> item.getMeasurementType().getName(), Function.identity()));
    }
}
