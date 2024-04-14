package br.ufscar.ppgcc.data;

public record GeolocationPoint(double latitude, double longitude, String address) {
    public GeolocationPoint(double latitude, double longitude) {
        this(latitude, longitude, null);
    }
}
