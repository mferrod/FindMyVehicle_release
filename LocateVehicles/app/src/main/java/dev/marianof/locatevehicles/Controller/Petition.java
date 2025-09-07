package dev.marianof.locatevehicles.Controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Petition {

    private final String IP = "http://IP:PORT/api";

    public void login(String user, String pass) {
        OkHttpClient client = new OkHttpClient();
        String json = "{\n\"username\": \"" + user + "\",\n" +
                "\"password\": \"" + pass + "\"\n}";
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                //url(TEST_URL + "/auth/login")
                .url(IP + "/auth/login")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("Petition - Login", "NO SE HA PODIDO HACER LA PETICIÓN, NO HAY CONEXIÓN O EL SERVIDOR HA CAIDO");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    Log.d("Petition - Login", "PETICION DE LOGIN CORRECTA, API KEY RECIBIDA.");
                    MainController.getSingleton().saveApiKey(res);
                    Log.d("MainActivity", "REPORTE DE APIKEY: " + MainController.getSingleton().getApiKey());
                });
            }
        });
    }

    public void getPositions() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + MainController.getSingleton().getApiKey())
                //url(TEST_URL + "/auth/login")
                .url(IP + "/user/getAll")
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("Petition - Login", "NO SE HA PODIDO HACER LA PETICIÓN, NO HAY CONEXIÓN O EL SERVIDOR HA CAIDO");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    Log.d("Petition - Posiciones", "POSICIONES RECOGIDAS.");
                    MainController.getSingleton().parsePositions(res);
                });
            }
        });
    }
}
