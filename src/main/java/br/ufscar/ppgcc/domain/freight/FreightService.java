package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.data.FreightViolation;
import br.ufscar.ppgcc.domain.carrier.CarrierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class FreightService {

    public static final Logger LOGGER = LoggerFactory.getLogger(FreightService.class);

    private final FreightRepository freightRepository;
    private final CarrierRepository carrierRepository;

    FreightService(FreightRepository freightRepository, CarrierRepository carrierRepository) {
        this.freightRepository = freightRepository;
        this.carrierRepository = carrierRepository;
    }

    public void start(Freight freight, String userId) {
        carrierRepository.findByUserId(userId).ifPresent(freight::setCarrier);
        freight.start();
        freightRepository.save(freight);
    }

    public void finish(Freight freight) {
        freight.finish();
        freightRepository.save(freight);
    }

    public Optional<Freight> findStarted(Device device) {
        return freightRepository.findFirstByDeviceAndStartedAtIsNotNullAndFinishedAtIsNullOrderByCreatedAtDesc(device);
    }

    public void violated(Freight freight, List<FreightViolation> violatedConditions) {
        violatedConditions.forEach(condition -> LOGGER.warn("{} violated for freight {}.", condition.name(), freight.getId()));
        freight.setViolated();
        freightRepository.save(freight);
    }
}
