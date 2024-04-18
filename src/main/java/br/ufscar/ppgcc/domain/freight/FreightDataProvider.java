package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.*;
import br.ufscar.ppgcc.domain.device.DeviceMeasurementService;
import br.ufscar.ppgcc.domain.product.ProductMeasurementTypeRepository;
import br.ufscar.ppgcc.domain.security.SecurityService;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Component
class FreightDataProvider extends CrudDataProvider<Freight> {

    private final transient FreightRepository repository;
    private final transient DeviceMeasurementService deviceMeasurementService;
    private final transient ProductMeasurementTypeRepository productMeasurementTypeRepository;
    private final transient SecurityService securityService;

    public FreightDataProvider(FreightRepository repository, DeviceMeasurementService deviceMeasurementService,
                               ProductMeasurementTypeRepository productMeasurementTypeRepository, SecurityService securityService) {
        super(repository);
        this.repository = repository;
        this.deviceMeasurementService = deviceMeasurementService;
        this.productMeasurementTypeRepository = productMeasurementTypeRepository;
        this.securityService = securityService;
    }

    @Override
    protected Stream<Freight> fetchFromBackEnd(Query<Freight, CrudFilter> query) {
        if (securityService.isCarrier()) {
            return repository.findByCarrierUserId(getUsername(), getPageable(query)).stream();
        }
        return super.fetchFromBackEnd(query);
    }

    private String getUsername() {
        return securityService.getAuthenticatedUser().getUsername();
    }

    @Override
    protected int sizeInBackEnd(Query<Freight, CrudFilter> query) {
        if (securityService.isCarrier()) {
            return repository.findByCarrierUserId(getUsername(), Pageable.unpaged()).getSize();
        }
        return super.sizeInBackEnd(query);
    }

    public Map<MeasurementType, List<DeviceMeasurement>> getSensorMeasurements(Freight freight) {
        return deviceMeasurementService.getSensorMeasurements(freight.getDevice(),
                freight.getStartedAt().orElse(ZonedDateTime.now()),
                freight.getFinishedAt().orElse(ZonedDateTime.now()));
    }

    public Map<ZonedDateTime, GeolocationPoint> getDeviceLocations(Freight freight) {
        return deviceMeasurementService.getLocations(freight.getDevice(),
                freight.getStartedAt().orElse(ZonedDateTime.now()),
                freight.getFinishedAt().orElse(ZonedDateTime.now()));
    }

    public Map<MeasurementType, ProductMeasurementType> getProductMeasurements(Freight freight) {
        return productMeasurementTypeRepository.findByProduct(freight.getProduct())
                .stream().collect(toMap(ProductMeasurementType::getMeasurementType, Function.identity()));
    }

}
