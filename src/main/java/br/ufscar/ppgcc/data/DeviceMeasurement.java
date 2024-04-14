package br.ufscar.ppgcc.data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_measurement")
public class DeviceMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "device_id")
    private String deviceId;

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

    public DeviceMeasurement(String deviceId, MeasurementType measurementType, ZonedDateTime measuredAt, String value) {
        this.deviceId = deviceId;
        this.measurementType = measurementType;
        this.measuredAt = measuredAt;
        this.value = value;
    }

}
