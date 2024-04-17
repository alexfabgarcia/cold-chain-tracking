package br.ufscar.ppgcc.domain.device.kpn;

import br.ufscar.ppgcc.data.GeolocationPoint;
import br.ufscar.ppgcc.domain.device.DeviceMeasurementService;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import br.ufscar.ppgcc.domain.event.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class KpnMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpnMessageHandler.class);

    private final ObjectMapper objectMapper;
    private final DeviceMeasurementService deviceMeasurementService;
    private final EventService eventService;

    public KpnMessageHandler(ObjectMapper objectMapper, DeviceMeasurementService deviceMeasurementService,
                             EventService eventService) {
        this.objectMapper = objectMapper;
        this.deviceMeasurementService = deviceMeasurementService;
        this.eventService = eventService;
    }

    @Override
    @ServiceActivator(inputChannel = "kpnMqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        LOGGER.debug("Handling message: {}", message);
        eventService.saveRawContentAsJson(message);

        var senMLList = readSensorMeasurementList(message);
        var kpnMessage = new KpnMessage(senMLList);
        var deviceId = getDeviceId(message);
        kpnMessage.getPayload().ifPresent(
                payload -> deviceMeasurementService.savePayload(deviceId, NetworkServer.KPN, kpnMessage.getDeviceEUI(), payload.value(), payload.time()));
        kpnMessage.getLocation().ifPresent(location -> {
            var geolocation = new GeolocationPoint(location.latitude(), location.longitude());
            deviceMeasurementService.saveLocation(deviceId, NetworkServer.KPN, kpnMessage.getDeviceEUI(), geolocation, location.time());
        });
        LOGGER.info("Message was successfully processed for device {}", deviceId);
    }

    private List<KpnSenML> readSensorMeasurementList(Message<?> message) {
        try {
            return Arrays.asList(objectMapper.readValue(message.getPayload().toString(), KpnSenML[].class));
        } catch (JsonProcessingException e) {
            throw new MessageHandlingException(message, e);
        }
    }

    private String getDeviceId(Message<?> message) {
        var topicName = Optional.ofNullable(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).map(Object::toString)
                .orElseThrow(() -> new MessageHandlingException(message, "Invalid topic name."));
        var topicComponents = topicName.split("/");
        return topicComponents[topicComponents.length - 1];
    }

}
