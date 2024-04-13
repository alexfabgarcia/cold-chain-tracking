package br.ufscar.ppgcc.data;

import br.ufscar.ppgcc.domain.device.NetworkEndDevice;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "external_id")
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "network_server")
    private NetworkServer networkServer;

    private String name;

    @Column(name = "payload_pattern")
    private String payloadPattern;

    @Version
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public NetworkServer getNetworkServer() {
        return networkServer;
    }

    public void setNetworkServer(NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    public String getPayloadPattern() {
        return payloadPattern;
    }

    public void setPayloadPattern(String payloadPattern) {
        this.payloadPattern = payloadPattern;
    }

    public void setFrom(NetworkEndDevice networkEndDevice) {
        if (nonNull(networkEndDevice)) {
            externalId = networkEndDevice.id();
            name = networkEndDevice.name();
            networkServer = networkEndDevice.networkServer();
        } else {
            externalId = null;
            name = null;
            networkServer = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(externalId, device.externalId) && networkServer == device.networkServer && Objects.equals(name, device.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, networkServer, name);
    }
}
