package br.ufscar.ppgcc.domain.device.kpn;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HexFormat;

/**
 * <a href="https://datatracker.ietf.org/doc/html/rfc8428">Sensor Measurement Lists (SenML) RFC</a>
 +---------------+------+---------+
 |         SenML | JSON | Type    |
 +---------------+------+---------+
 |     Base Name | bn   | String  |
 |     Base Time | bt   | Number  |
 |     Base Unit | bu   | String  |
 |    Base Value | bv   | Number  |
 |       Version | bver | Number  |
 |          Name | n    | String  |
 |          Unit | u    | String  |
 |         Value | v    | Number  |
 |  String Value | vs   | String  |
 | Boolean Value | vb   | Boolean |
 |    Data Value | vd   | String  |
 |     Value Sum | s    | Number  |
 |          Time | t    | Number  |
 |   Update Time | ut   | Number  |
 +---------------+------+---------+
 */
public record KpnSenML(String bn, Number bt, String bu, Number bv, Number bver, String n, String u, Number v, String vs,
                       Boolean vb, String vd, Number s, Number t, Number ut) {

    public String getStringFromHexValue() {
        return new String(HexFormat.of().parseHex(vs()));
    }

    public ZonedDateTime getBaseTime() {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(bt.longValue()), ZoneId.systemDefault());
    }

}
