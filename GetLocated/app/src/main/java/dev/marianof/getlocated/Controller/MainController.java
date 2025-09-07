package dev.marianof.getlocated.Controller;

import android.location.LocationManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dev.marianof.getlocated.View.MainActivity;

public class MainController {
    private static MainActivity myActivity;
    private static MainController mainController;
    private String API_KEY;
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private Double latitud;
    private Double longitud;
    private String user;
    private String pass;
    private MainController() {
        locationListener = new MyLocationListener();
        latitud = 0.0;
        longitud = 0.0;
    }

    public static MainController getSingleton() {
        if (mainController == null)
            mainController = new MainController();
        return mainController;
    }

    public void setActivity(MainActivity activity)
    {
        myActivity = activity;
    }

    private void makePetition(double latitude, double longitude) {
        Petition petition = new Petition();
        petition.uploadCoordinates(latitude, longitude);
    }

    public void setLocationManager(LocationManager location) {
        locationManager = location;
    }

    public void setCoordinates(double latitude, double longitude) {
        latitud = latitude;
        longitud = longitude;
        Log.d("MainController", "Coordenadas recibidas, checkeo: \n LAT: " + latitude + "  - LON: " + longitude);
        Log.d("MainController", "Coordenadas correctas, enivando...");
        makePetition(latitud, longitud);
    }

    public void readFile(File filesDir) {
        File file = new File(filesDir, "key.txt");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String pete = bufferedReader.readLine();
            MainController.getSingleton().setUser(pete.split(":")[0]);
            MainController.getSingleton().setPass(pete.split(":")[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        makePetitionLogin();
    }

    public void makePetitionLogin() {
        Petition petition = new Petition();
        petition.login(this.user, this.pass);
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }
    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
