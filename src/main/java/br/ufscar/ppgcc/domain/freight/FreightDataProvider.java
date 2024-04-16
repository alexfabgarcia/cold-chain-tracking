package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.*;
import br.ufscar.ppgcc.domain.device.DeviceMeasurementService;
import br.ufscar.ppgcc.domain.product.ProductMeasurementTypeRepository;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
class FreightDataProvider extends CrudDataProvider<Freight> {

    private final DeviceMeasurementService deviceMeasurementService;
    private final ProductMeasurementTypeRepository productMeasurementTypeRepository;

    public FreightDataProvider(FreightRepository repository, DeviceMeasurementService deviceMeasurementService,
                               ProductMeasurementTypeRepository productMeasurementTypeRepository) {
        super(repository);
        this.deviceMeasurementService = deviceMeasurementService;
        this.productMeasurementTypeRepository = productMeasurementTypeRepository;
    }

    public Map<MeasurementType, List<DeviceMeasurement>> getSensorMeasurements(Freight freight) {
        return deviceMeasurementService.getSensorMeasurements(freight.getDevice(), ZonedDateTime.now().minusYears(1), ZonedDateTime.now());
    }

    public Map<ZonedDateTime, GeolocationPoint> getDeviceLocations(Freight freight) {
        return deviceMeasurementService.getLocations(freight.getDevice(), ZonedDateTime.now().minusYears(1), ZonedDateTime.now());
    }

    public Map<MeasurementType, ProductMeasurementType> getProductMeasurements(Freight freight) {
        return productMeasurementTypeRepository.findByProduct(freight.getProduct())
                .stream().collect(toMap(ProductMeasurementType::getMeasurementType, Function.identity()));
    }

}
