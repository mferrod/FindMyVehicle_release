package dev.marianof.getlocated.Controller;

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
    private final String URL = "http://IP:PORT/api";
    private final String TEST_URL = "http://192.168.1.123:8080/api";
    private static Petition myPetition;

    public void uploadCoordinates(double latitude, double longitude) {
        OkHttpClient client = new OkHttpClient();
        String json = "{\n\"longitude\": " + longitude + ",\n" +
                "\"latitude\": " + latitude + "\n}";
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + MainController.getSingleton().getAPI_KEY())
                //.url(TEST_URL + "/user/updateCoordinates")
                .url(URL + "/user/updateCoordinates")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("Petition", "NO SE HA PODIDO HACER LA PETICIÓN, NO HAY CONEXIÓN O EL SERVIDOR HA CAIDO");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Petition", "PETICIÓN REALIZADA CORRECTAMENTE.");
            }
        });
    }

    public void login(String user, String pass) {
        OkHttpClient client = new OkHttpClient();
        String json = "{\n\"username\": \"" + user + "\",\n" +
                "\"password\": \"" + pass + "\"\n}";
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                //url(TEST_URL + "/auth/login")
                .url(URL + "/auth/login")
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainController.getSingleton().setAPI_KEY(res);
                    }
                });
            }
        });
    }

}
