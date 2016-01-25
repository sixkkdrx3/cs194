package cs194.maaap;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.bleat_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                InputDialogFragment f = new InputDialogFragment();
                f.show(ft, "dialog");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "onMapReady called");
        mMap = googleMap;
        double lat, lng;
        try {
            mMap.setMyLocationEnabled(true);
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            } else { //default to stanford coordinates
                lat = 37.43;
                lng = -122.17;
            }
        }
        catch(SecurityException se)
        {
            lat = 37.43;
            lng = -122.17;
        }
        LatLng currentLocation = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, (float) 15.0));
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
}
