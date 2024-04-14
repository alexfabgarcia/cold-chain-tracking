package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.data.GeolocationPoint;
import br.ufscar.ppgcc.domain.device.DeviceMeasurementService;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;

@Component
class FreightDataProvider extends CrudDataProvider<Freight> {

    private final DeviceMeasurementService deviceMeasurementService;

    public FreightDataProvider(FreightRepository repository, DeviceMeasurementService deviceMeasurementService) {
        super(repository);
        this.deviceMeasurementService = deviceMeasurementService;
    }

    public Map<ZonedDateTime, GeolocationPoint> getDeviceLocations(Freight freight) {
        return deviceMeasurementService.getDeviceLocations(freight.getDevice(), ZonedDateTime.now().minusYears(1), ZonedDateTime.now());
    }

}
