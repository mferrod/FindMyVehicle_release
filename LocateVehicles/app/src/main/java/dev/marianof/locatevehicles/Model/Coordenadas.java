package dev.marianof.locatevehicles.Model;

public class Coordenadas {
    private final String name;
    private final Double latitude;
    private final Double longitude;
    private byte[] picture;

    public Coordenadas(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordenadas(String name, Double latitude, Double longitude, byte[] picture) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public byte[] getPicture() {
        return picture;
    }
}
