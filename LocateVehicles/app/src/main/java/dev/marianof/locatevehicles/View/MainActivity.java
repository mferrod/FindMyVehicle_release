package dev.marianof.locatevehicles.View;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import dev.marianof.locatevehicles.Controller.ActionCallback;
import dev.marianof.locatevehicles.Controller.MainController;
import dev.marianof.locatevehicles.Controller.ScheduledTask.PositionTask;
import dev.marianof.locatevehicles.Model.Coordenadas;
import dev.marianof.locatevehicles.R;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private MainActivity mainActivity;
    private List<Marker> activeMarkers = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mainActivity = this;
        if (checkNetworkConnection(mainActivity).equals("Sin Conexión"))
        {
                Toast.makeText(mainActivity, "Conéctate a una red WiFi o activa los datos móviles.", Toast.LENGTH_LONG).show();
                finishAndRemoveTask();
        }
        if (ActivityCompat.checkSelfPermission(mainActivity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mainActivity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mainActivity, "ACTIVA LA LOCALIZACIÓN", Toast.LENGTH_SHORT).show();
                mainActivity.finish();
        }
        MainController.getSingleton().setMyActivity(mainActivity);
        File file = new File(getFilesDir(), "login.txt");
        if (!file.exists())
            createAlertDialog();
        MainController.getSingleton().readFile(this::activateTask, getFilesDir());
        if (MainController.getSingleton().getApiKey() != null)
            finishAndRemoveTask();
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);

        GeoPoint start = new GeoPoint(37.9153439,-3.0047756);
        mapController.setCenter(start);
    }

    public static String checkNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "Wi-Fi";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "Datos Móviles";
            }
        }
        return "Sin Conexión";
    }

    private void activateTask() {
        Timer tiempo = new Timer();
        tiempo.schedule(new PositionTask(), 1000, 5000);
    }

    public void setPos() {
        for (Marker marker: activeMarkers) {
            mapView.getOverlays().remove(marker);
            mapView.invalidate();
        }
        activeMarkers.clear();
        ArrayList<Coordenadas> cor = MainController.getSingleton().getCoordenadas();
        for (int i = 0; i < cor.size() - 1; i++) {
            addExpiringMarker(cor.get(i).getLatitude(), cor.get(i).getLongitude(), cor.get(i).getName());
        }
    }

    private void createAlertDialog() {
        AlertDialog.Builder adbuild = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText correo = new EditText(this);
        correo.setHint("Correo");
        layout.addView(correo);

        final EditText passwd = new EditText(this);
        passwd.setHint("Contraseña");
        layout.addView(passwd);

        adbuild.setView(layout);

        adbuild.setCancelable(false).setPositiveButton("OK", (dialog, id) -> createFile(correo.getText().toString(), passwd.getText().toString()));

        AlertDialog alertDialog = adbuild.create();
        alertDialog.show();
    }

    private void createFile(String correo, String passwd) {
        new Thread(() -> {
            File dir = new File(getFilesDir(), "login.txt");
            try {
                FileWriter fileWriter = new FileWriter(dir);
                fileWriter.append(String.valueOf(correo)).append(":").append(String.valueOf(passwd));
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ).start();
    }

    private void addExpiringMarker(double lat, double lon, String title) {
        GeoPoint point = new GeoPoint(lat, lon);
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        Drawable d;
        // Personalizar el icono del marcador
        //if (title.equals("x@gmail.com"))
        //    d = getResources().getDrawable(R.drawable.x, null);
        //else
        //    d = getResources().getDrawable(R.drawable.x, null);

        //Esto está hecho para cambiar entre distintos dispositivos, pero en próximas actualizaciones cambiará.

        // Convertir drawable en bitmap
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        // Escalar el bitmap (ejemplo: 64x64 px)
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 32, 32, false);

        // Asignar icono reducido
        marker.setIcon(new BitmapDrawable(getResources(), smallMarker));

        mapView.getOverlays().add(marker);
        activeMarkers.add(marker);
        mapView.invalidate();
    }
}