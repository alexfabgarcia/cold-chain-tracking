package br.ufscar.ppgcc.data;

import br.ufscar.ppgcc.domain.device.NetworkEndDevice;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;
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

    private String eui;

    private String name;

    @Column(name = "payload_decoder")
    private String payloadDecoder;

    @Version
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    public Device() {
    }

    public Device(String externalId, NetworkServer networkServer) {
        this.externalId = externalId;
        this.networkServer = networkServer;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getEui() {
        return eui;
    }

    public void setEui(String eui) {
        this.eui = eui;
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

    public String getPayloadDecoder() {
        return payloadDecoder;
    }

    public void setPayloadDecoder(String payloadPattern) {
        this.payloadDecoder = payloadPattern;
    }

    public void setFrom(NetworkEndDevice networkEndDevice) {
        if (nonNull(networkEndDevice)) {
            externalId = networkEndDevice.id();
            name = networkEndDevice.name();
            networkServer = networkEndDevice.networkServer();
            eui = networkEndDevice.eui();
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

    @Override
    public String toString() {
        return new StringJoiner(", ", Device.class.getSimpleName() + "[", "]")
                .add("externalId='" + externalId + "'")
                .add("networkServer=" + networkServer)
                .add("name='" + name + "'")
                .toString();
    }
}
