package dev.marianof.getlocated.View;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.ProgressDialog.show;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dev.marianof.getlocated.Controller.MainController;
import dev.marianof.getlocated.Controller.MyLocationListener;
import dev.marianof.getlocated.R;
import dev.marianof.getlocated.Services.BackgroundService;

public class MainActivity extends AppCompatActivity {
    private MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mainActivity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();
        File file = new File(getFilesDir(), "key.txt");
        if (!file.exists())
            createAlertDialog();
        MainController.getSingleton().setActivity(this);
        this.startService(new Intent(this.getApplicationContext(), BackgroundService.class));
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

        adbuild.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                createFile(correo.getText().toString(), passwd.getText().toString());
            }
        });

        AlertDialog alertDialog = adbuild.create();
        alertDialog.show();
    }

    private void createFile(String correo, String passwd) {
        new Thread(() -> {
            File dir = new File(getFilesDir(), "key.txt");
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
}