package br.ufscar.ppgcc.domain.device.ttn;

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

import java.util.Optional;

@Component
public class TtnMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TtnMessageHandler.class);

    private final ObjectMapper objectMapper;
    private final DeviceMeasurementService deviceMeasurementService;
    private final EventService eventService;

    public TtnMessageHandler(ObjectMapper objectMapper, DeviceMeasurementService deviceMeasurementService,
                             EventService eventService) {
        this.objectMapper = objectMapper;
        this.deviceMeasurementService = deviceMeasurementService;
        this.eventService = eventService;
    }

    @Override
    @ServiceActivator(inputChannel = "ttnMqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        LOGGER.debug("Handling message: {}", message);
        eventService.saveRawContentAsJson(message);

        var topicName = topicName(message);
        if (topicName.endsWith("/up")) {
            handleUplinkMessage(message);
        }
    }

    private String topicName(Message<?> message) {
        return Optional.ofNullable(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).map(Object::toString)
                .orElseThrow(() -> new MessageHandlingException(message, "Invalid topic name."));
    }

    private void handleUplinkMessage(Message<?> message) {
        try {
            var uplinkMessage = objectMapper.readValue(message.getPayload().toString(), TtnUplinkMessage.class);
            LOGGER.info("Handling uplink message of {}.", uplinkMessage.deviceId());
            uplinkMessage.decodedPayload().ifPresent(decodedPayload -> deviceMeasurementService.savePayload(
                    uplinkMessage.deviceId(), NetworkServer.TTN, uplinkMessage.deviceEui(), decodedPayload, uplinkMessage.receivedAt()));
        } catch (JsonProcessingException e) {
            throw new MessageHandlingException(message, e);
        }
    }

}
