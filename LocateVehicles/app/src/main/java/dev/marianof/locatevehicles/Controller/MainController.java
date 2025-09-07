package dev.marianof.locatevehicles.Controller;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dev.marianof.locatevehicles.Model.Coordenadas;
import dev.marianof.locatevehicles.View.MainActivity;

public class MainController {
    private static MainController myController;
    private static MainActivity myActivity;
    private String apiKey;
    private ArrayList<Coordenadas> coordenadas;
    
    private MainController() {
    }


    public static MainController getSingleton() {
        if (myController == null)
            myController = new MainController();
        return myController;
    }
    
    public void setMyActivity(MainActivity mainActivity) {
        myActivity = mainActivity;
    }

    public void makePetition() {
        Petition petition = new Petition();
        petition.getPositions();
    }

    public ArrayList<Coordenadas> getCoordenadas() {
        return coordenadas;
    }

    public void saveApiKey(String res) {
        apiKey = res;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void makePetitionLogIn(String name, String pass) {
        Petition petition = new Petition();
        petition.login(name, pass);
    }

    public void readFile(ActionCallback a, File filesDir) {
        new Thread(() ->{
            File file = new File(filesDir, "login.txt");
            String pete;
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                pete = bufferedReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            makePetitionLogIn(pete.split(":")[0], pete.split(":")[1]);

            new Handler(Looper.getMainLooper()).post(a::onCompleted);
        }).start();
    }

    public void parsePositions(String res) {
        Response response = new Response();
        coordenadas = new ArrayList<>();
        coordenadas.addAll(response.parsePosistions(res));
        myActivity.setPos();
    }
}
