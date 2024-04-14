package br.ufscar.ppgcc.data;

public enum MeasurementUnit {

    DEGREE_CELSIUS("°C"),
    PERCENTAGE("%"),
    LATITUDE_LONGITUDE("Latitude, Longitude"),
    ;

    private final String description;

    MeasurementUnit(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
