package cs194.maaap;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.location.Criteria;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private HashMap<String, Bleat> bleatMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        bleatMap = new HashMap<String, Bleat>();

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.bleat_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                InputDialogFragment f = new InputDialogFragment();
                f.show(ft, "postBleat");
            }
        });

    }

    public boolean onMarkerClick(Marker marker)
    {
        Log.d("map", "marker clicked " + marker.getSnippet());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String bid = marker.getSnippet();
        DisplayDialogFragment ddf = new DisplayDialogFragment(bleatMap.get(bid));
        ddf.show(ft, "showBleat");
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "onMapReady called");
        mMap = googleMap;
        double ret[] = getGPS();
        LatLng currentLocation = new LatLng(ret[0], ret[1]);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, (float) 15.0));
        drawBleats();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraChangeListener(this);

        /* end testing filterBleats */

        //37.427490, -122.170265 (main quad)
    }

    public void drawBleats()
    {
        mMap.clear();

        /* begin testing filterBleats */
        FilterBleats filterBleats = new FilterBleats(this);
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        //List<Bleat> result = filterBleats.filter(0, bounds.southwest.latitude, bounds.northeast.latitude, bounds.southwest.longitude, bounds.northeast.longitude);
        List<Bleat> result = filterBleats.filter(0, -180, 180, -180, 180);
        //Log.d("map", Integer.toString(result.size()));

        //test coordinates
        double latMin = 37.420512;
        double latMax = 37.431350;
        double lngMin = -122.182196;
        double lngMax = -122.159193;

        if (result != null) {
            Random r = new Random();
            r.setSeed(System.currentTimeMillis());
            for(Bleat bleat : result) {
                bleat.setLatitude(latMin + (latMax-latMin)*r.nextDouble());
                bleat.setLongitude(lngMin + (lngMax - lngMin) * r.nextDouble());
            }

            bleatMap.clear();
            for (Bleat bleat : result) {
                Log.d("map", bleat.getMessage());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(bleat.getLatitude(), bleat.getLongitude())).snippet(bleat.getBID()));
                bleatMap.put(bleat.getBID(), bleat);
            }
            Log.d("map", "done");
        } else {
            Log.d("map", "ggwp");
        }
    }

    public void onCameraChange(CameraPosition change)
    {
        Log.d("map", "cameraChanged");
        //drawBleats();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("map", "onConnected called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("map", "connection suspended");
    }


    protected double[] getGPS() {
        double[] ret = new double[2];
        try {
            mMap.setMyLocationEnabled(true);
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                ret[0] = location.getLatitude();
                ret[1] = location.getLongitude();
            } else { //default to stanford coordinates
                ret[0] = 37.43;
                ret[1] = -122.17;
            }
        } catch (SecurityException se) {
            ret[0] = 37.43;
            ret[1] = -122.17;
        }
        return ret;
    }
}
