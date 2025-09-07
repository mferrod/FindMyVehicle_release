package dev.marianof.getlocated.Model;

public class User {
    private final Integer id;
    private final String name;
    private double altitude;
    private double latitude;
    private final String apiKey;

    public User(Integer id, String name, double altitude, double latitude, String apiKey) {
        this.id = id;
        this.name = name;
        this.altitude = altitude;
        this.latitude = latitude;
        this.apiKey = apiKey;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getApiKey() {
        return apiKey;
    }
}
