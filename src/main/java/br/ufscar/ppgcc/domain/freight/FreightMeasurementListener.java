package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.data.ConditionViolatedEvent;
import br.ufscar.ppgcc.data.DeviceMeasurementEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class FreightMeasurementListener implements ApplicationListener<DeviceMeasurementEvent> {

    private final FreightService freightService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public FreightMeasurementListener(FreightService freightService, ApplicationEventPublisher applicationEventPublisher) {
        this.freightService = freightService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(DeviceMeasurementEvent event) {
        freightService.findStarted(event.getDevice()).ifPresent(freight -> {
            var violatedConditions = freight.getViolatedConditions(event.getMeasurements());
            if (!violatedConditions.isEmpty()) {
                freightService.violated(freight, violatedConditions);
                applicationEventPublisher.publishEvent(new ConditionViolatedEvent(violatedConditions, freight));
            }
        });
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
