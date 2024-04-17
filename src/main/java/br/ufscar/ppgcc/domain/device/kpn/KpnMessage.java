package br.ufscar.ppgcc.domain.device.kpn;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KpnMessage {

    private static final Pattern URN_PATTERN = Pattern.compile("urn:dev:DEVEUI:(\\w+):");

    private final Map<String, KpnSenML> sensorMeasurementMap;

    public KpnMessage(List<KpnSenML> senMLList) {
        this.sensorMeasurementMap = senMLList.stream().collect(Collectors.toMap(KpnSenML::n, Function.identity()));
    }

    public record KpnPayload(String value, ZonedDateTime time) {}

    public record KpnLocation(Double latitude, Double longitude, String method, ZonedDateTime time) {}

    public String getDeviceEUI() {
        return sensorMeasurementMap.values().stream()
                .map(KpnSenML::bn)
                .filter(Objects::nonNull)
                .map(URN_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .findAny()
                .orElse(null);
    }

    public Optional<KpnPayload> getPayload() {
        return findMeasurement("payload")
                .map(senML -> new KpnPayload(senML.getStringFromHexValue(), senML.getBaseTime()));
    }

    public Optional<KpnLocation> getLocation() {
        return findMeasurement("locOrigin")
                .map(senML -> new KpnLocation(getDouble("latitude"), getDouble("longitude"), getLocationMethod(), senML.getBaseTime()));
    }

    private Optional<KpnSenML> findMeasurement(String name) {
        return Optional.ofNullable(sensorMeasurementMap.get(name));
    }

    private Double getDouble(String name) {
        return findMeasurement(name)
                .map(KpnSenML::v)
                .map(Number::doubleValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid measurement name: " + name));
    }

    private String getLocationMethod() {
        return findMeasurement("locMethod")
                .map(KpnSenML::vs)
                .orElseThrow(() -> new IllegalArgumentException("Invalid measurement name: locMethod"));
    }

}
