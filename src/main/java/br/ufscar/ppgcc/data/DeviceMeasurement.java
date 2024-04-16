package br.ufscar.ppgcc.data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_measurement")
@NamedEntityGraph(name = "DeviceMeasurement.eager", attributeNodes = {
        @NamedAttributeNode("device"), @NamedAttributeNode("measurementType")
})
public class DeviceMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "measurement_type_id", nullable = false)
    private MeasurementType measurementType;

    @Column(name = "measured_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime measuredAt;

    private String value;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    protected DeviceMeasurement() {
    }

    public DeviceMeasurement(Device device, MeasurementType measurementType, ZonedDateTime measuredAt, String value) {
        this.device = device;
        this.measurementType = measurementType;
        this.measuredAt = measuredAt;
        this.value = value;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public String getValue() {
        return value;
    }

    public ZonedDateTime getMeasuredAt() {
        return measuredAt;
    }
}
