package android.teste.matheusaguilar.maps;

import androidx.appcompat.widget.ActivityChooserView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.locks.ReadWriteLock;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    private GoogleMap mMap;

    private String local;

    private final LatLng NATAL = new LatLng(-16.374775, -40.537228);
    private final LatLng VICOSA = new LatLng(-20.753199, -42.878001);
    private final LatLng DPI = new LatLng(-20.764993, -42.868467);

    private LatLng ATUAL;
    private Marker markerAtual = null;

    private LocationManager lm;
    private Criteria criteria;
    private String provider;
    private int REQ_TIME_LATLONG = 5000;
    private int DIST_MIN = 0;

    public final int LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        local = getIntent().getStringExtra("local");

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        PackageManager pm = getPackageManager();
        boolean hasGPS = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if(hasGPS){
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        }
        else{
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart(){
        super.onStart();

        provider = lm.getBestProvider(criteria, true);

        if(provider == null){
            Toast.makeText(this, "Provedor não encontrado", Toast.LENGTH_LONG);
        }
        else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                lm.requestLocationUpdates(provider, REQ_TIME_LATLONG, DIST_MIN, this);
        }
    }

    @Override
    protected void onDestroy(){
        lm.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location){
        if(location != null){
            ATUAL = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        atualizaMapa();
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void atualizaMapa(){
        switch(local){
            case "cidadeNatal":
                mMap.addMarker(new MarkerOptions().position(NATAL).title("Rubim-MG"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(NATAL, 16));
                break;
            case "casaVicosa":
                mMap.addMarker(new MarkerOptions().position(VICOSA).title("Viçosa-MG"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(VICOSA, 16));
                break;
            case "departamento":
                mMap.addMarker(new MarkerOptions().position(DPI).title("DPI"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DPI, 16));
                break;
            case "atual":
                if(markerAtual != null)
                    markerAtual.remove();
                markerAtual = mMap.addMarker(new MarkerOptions().position(ATUAL).title("Localização Atual")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ATUAL, 16));

                float results[] = new float[3];
                Location.distanceBetween(VICOSA.latitude, VICOSA.longitude, ATUAL.latitude, ATUAL.longitude, results);

                Toast.makeText(this, "Você está a " + Float.toString(results[0]) + " metros de sua casa em Viçosa",
                        Toast.LENGTH_LONG).show();

                break;
        }
    }

    public void trocaFoco(View view){
        local = view.getTag().toString();
        atualizaMapa();
    }

    public void localizacaoAtual(View view){
        requestLocationPermission();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ATUAL != null) {
            local = "atual";
            atualizaMapa();
        }
    }

    public void requestLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,"Permita o uso da localização para acessar seu local", Toast.LENGTH_LONG).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String permission[], int[] grantResults){
        switch(code){
            case LOCATION_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permissão Concedida", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "Permissão Negada: não é possível buscar a localização", Toast.LENGTH_LONG);
                }
            break;
        }
    }
}