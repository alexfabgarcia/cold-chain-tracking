package br.ufscar.ppgcc.domain.device;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class TemperatureHumidityDecoder implements DevicePayloadDecoder {

    @Override
    public String id() {
        return "temperatureHumidityDecoder";
    }

    @Override
    public String name() {
        return "Temperature and Humidity Payload Decoder";
    }

    @Override
    public Map<String, Double> decode(String payloadHex) {
        var bytes = Hex.decode(payloadHex);
        Map<String, Double> decoded = new HashMap<>();

        // temperature
        int rawTemp = bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8;
        decoded.put("Temperature", sflt162f(rawTemp) * 100);

        // humidity
        int rawHumid = bytes[2] & 0xFF | (bytes[3] & 0xFF) << 8;
        decoded.put("Humidity", sflt162f(rawHumid) * 100);

        return decoded;
    }

    private double sflt162f(int rawSflt16) {

        // throw away high bits for repeatability.
        rawSflt16 &= 0xFFFF;

        // special case minus zero:
        if (rawSflt16 == 0x8000)
            return -0.0;

        // extract the sign.
        int sSign = ((rawSflt16 & 0x8000) != 0) ? -1 : 1;

        // extract the exponent
        int exp1 = (rawSflt16 >> 11) & 0xF;

        // extract the "mantissa" (the fractional part)
        double mant1 = (rawSflt16 & 0x7FF) / 2048.0;

        // convert back to a floating point number.
        return sSign * mant1 * Math.pow(2, exp1 - 15);
    }
}
