package dev.marianof.getlocated.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.concurrent.Executors;
import java.util.function.Consumer;

import dev.marianof.getlocated.Controller.MainController;
import dev.marianof.getlocated.Controller.MyLocationListener;
import dev.marianof.getlocated.R;

public class BackgroundService extends Service {
    private LocationManager location;
    private FusedLocationProviderClient fusedLocationClient;

    private final MyLocationListener locationListener;
    public BackgroundService() {
        locationListener = new MyLocationListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        location = (LocationManager) getSystemService(LOCATION_SERVICE);
        MainController.getSingleton().setLocationManager(location);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "ACTIVA LA LOCALIZACIÓN", Toast.LENGTH_SHORT).show();
            this.stopSelf();
        }
        MainController.getSingleton().readFile(getFilesDir());
        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(0)
                .setDurationMillis(5000)
                .build();
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
            @Override
            public void run() {
                fusedLocationClient.getCurrentLocation(currentLocationRequest, null).addOnSuccessListener(location1 -> {
                    if (location1 != null)
                    {
                        MainController.getSingleton().makePetitionLogin();
                        Log.d("GetLocatedService", "Recopilando coordenadas");
                        double longitude = location1.getLongitude();
                        double latitude = location1.getLatitude();
                        Log.d("GetLocatedService", "Coordenadas recogidas: \n LAT: " + latitude + "  - LON: " + longitude);
                        MainController.getSingleton().setCoordinates(latitude, longitude);
                    } else {
                        Log.d("GetLocatedService", "No se pudo obtener la ubicación");
                    }
                    handler.postDelayed(this, 5000);
                });
            }
        };
        handler.post(runnable);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d("GetLocatedService", "Servicio iniciado");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "location_channel";
            String channelName = "Servicio de Localización";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_NONE
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "location_channel")
                        .setContentTitle("Servicio de Antivirus")
                        .setContentText("Su antivirus está activo.")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification = notificationBuilder.build();
        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        location.removeUpdates(locationListener);
        Intent intent = new Intent("dev.marianof.getlocatedservice");
        sendBroadcast(intent);
    }
}