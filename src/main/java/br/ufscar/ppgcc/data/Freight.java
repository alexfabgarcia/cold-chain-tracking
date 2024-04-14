package br.ufscar.ppgcc.data;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "freight")
@NamedEntityGraph(name = "Freight.details", attributeNodes = {
        @NamedAttributeNode("product"), @NamedAttributeNode("device"), @NamedAttributeNode("carrier")
})
public class Freight {

    public enum Status {
        CREATED, STARTED, FINISHED, CANCELLED
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

    @Type(JsonType.class)
    private GeolocationPoint origin;

    @Type(JsonType.class)
    private GeolocationPoint destination;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    @ManyToOne
    @JoinColumn(name="carrier_id")
    private Carrier carrier;

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

    public Status getStatus() {
        return status;
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
        return Optional.ofNullable(carrier).map(Carrier::getFirstName).orElse(null);
    }

}
