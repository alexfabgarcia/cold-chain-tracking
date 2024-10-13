package br.ufscar.ppgcc.domain.test;

import br.ufscar.ppgcc.data.*;
import br.ufscar.ppgcc.domain.carrier.CarrierRepository;
import br.ufscar.ppgcc.domain.device.DeviceRepository;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import br.ufscar.ppgcc.domain.device.kpn.KpnGetDevicesResponse;
import br.ufscar.ppgcc.domain.device.ttn.TtnGetDevicesResponse;
import br.ufscar.ppgcc.domain.freight.FreightRepository;
import br.ufscar.ppgcc.domain.product.ProductRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.util.UUID;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/tests")
public class TestScenarioController {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    CarrierRepository carrierRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    FreightRepository freightRepository;

    @GetMapping("/kpn")
    public ResponseEntity<Void> generateCsvKpn(@Param("devices") Integer devices, HttpServletResponse response) {
        return generateCsv(devices, response, NetworkServer.KPN);
    }

    @GetMapping("/ttn")
    public ResponseEntity<Void> generateCsvTtn(@Param("devices") Integer devices, HttpServletResponse response) {
        return generateCsv(devices, response, NetworkServer.TTN);
    }

    private ResponseEntity<Void> generateCsv(Integer devices, HttpServletResponse response, NetworkServer networkServer) {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s-devices-data.csv\"", networkServer));

        try (PrintWriter writer = response.getWriter()) {

            // Header row
            writer.write(String.format("DEVICE_ID%n"));

            IntStream.range(0, devices)
                    .mapToObj(num -> createStartedFreight(num, networkServer))
                    .forEach(freight -> writer.write(String.format("%s%n", freight.getDevice().getExternalId())));

            writer.flush();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Freight createStartedFreight(int num, NetworkServer networkServer) {
        String description = String.format("LoadTest Freight %s %s", networkServer, num);
        return freightRepository.findByDescription(description).orElseGet(() -> {
            var carrier = createCarrier(num);
            var device = createDevice(num, networkServer);
            var freight = new Freight();
            freight.setCarrier(carrier);
            freight.setDevice(device);
            freight.setProduct(getProduct());
            freight.setOrigin(new GeolocationPoint(52.2704079, 5.161766699999999, "Burgemeester de Bordesstraat 80, 1404 GZ Bussum, Netherlands"));
            freight.setDestination(new GeolocationPoint(52.1624896, 5.308352699999999, "Soesterbergsestraat 76, 3768 EK Soest, Netherlands"));
            freight.setDescription(description);
            freight.start();
            return freightRepository.save(freight);
        });
    }

    private Device createDevice(int num, NetworkServer networkServer) {
        var name = String.format("LoadTest %s %s", networkServer, num);
        return deviceRepository.findByName(name).orElseGet(() -> {
            var device = new Device();
            device.setFrom(
                    networkServer == NetworkServer.KPN ?
                            new KpnGetDevicesResponse.KpnDevice(UUID.randomUUID().toString(), name, RandomStringUtils.randomAlphanumeric(16)) :
                            new TtnGetDevicesResponse.EndDevice(new TtnGetDevicesResponse.EndDevice.Ids(UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(16)), name)
                    );
            device.setPayloadDecoder("temperatureHumidityDecoder");
            return deviceRepository.save(device);
        });
    }

    private Carrier createCarrier(int num) {
        var firstName = String.format("LoadTest Carrier %s", num);
        var lastName = "Test";
        return carrierRepository.findByFirstNameAndSurname(firstName, lastName).orElseGet(() -> {
            var carrier = new Carrier();
            carrier.setFirstName(firstName);
            carrier.setSurname(lastName);
            carrier.setPhone("+5511999999999");
            return carrierRepository.save(carrier);
        });
    }

    private Product getProduct() {
        return productRepository.findByName("Novavax COVID-19")
                .or(() -> productRepository.findByName("CoronaVac COVID-19"))
                .orElseThrow(() -> new IllegalStateException("There is no Novavax/CoronaVac COVID-19 product."));
    }

}
