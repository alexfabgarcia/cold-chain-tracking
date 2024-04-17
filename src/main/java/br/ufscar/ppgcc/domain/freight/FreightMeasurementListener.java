package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.data.ConditionViolatedEvent;
import br.ufscar.ppgcc.data.DeviceMeasurementEvent;
import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.data.ProductMeasurementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class FreightMeasurementListener implements ApplicationListener<DeviceMeasurementEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreightMeasurementListener.class);

    private final FreightRepository freightRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public FreightMeasurementListener(FreightRepository freightRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.freightRepository = freightRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(DeviceMeasurementEvent event) {
        var violatedConditions = violatedConditions(event);
        if (!violatedConditions.isEmpty()) {
            applicationEventPublisher.publishEvent(new ConditionViolatedEvent(violatedConditions, event.getDevice()));
        }
    }

    private List<String> violatedConditions(DeviceMeasurementEvent event) {
        var productMeasurements = getProductMeasureTypes(event);
        return event.getMeasurements().entrySet().stream()
                .filter(entry -> Optional.ofNullable(productMeasurements.get(entry.getKey()))
                        .filter(productMeasurement -> entry.getValue() < productMeasurement.getMinimum() || entry.getValue() > productMeasurement.getMaximum())
                        .isPresent())
                .peek(entry -> LOGGER.warn("{} measured as {} violates the limits. Device {}.", entry.getKey(), entry.getValue(), event.getDevice()))
                .map(Map.Entry::getKey)
                .toList();
    }
    private Map<String, ProductMeasurementType> getProductMeasureTypes(DeviceMeasurementEvent event) {
        var freight = freightRepository.findFirstByDeviceAndStatus(event.getDevice(), Freight.Status.CREATED); //TODO change to started
        return freight.getProduct().getMeasurementTypes().stream()
                .collect(toMap(item -> item.getMeasurementType().getName(), Function.identity()));
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
