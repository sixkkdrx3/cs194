package cs194.maaap;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private HashMap<String, Bleat> bleatMap;
    private long lastUpdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        bleatMap = new HashMap<String, Bleat>();
        lastUpdated = 0;

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

    public boolean onMarkerClick(Marker marker) {
        Log.d("map", "marker clicked " + marker.getSnippet());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String bid = marker.getSnippet();
        DisplayDialogFragment ddf = new DisplayDialogFragment(bleatMap.get(bid));

        ddf.show(ft, "showBleat");
        marker.showInfoWindow();

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "onMapReady called");
        mMap = googleMap;
        double ret[] = getGPS();
        LatLng currentLocation = new LatLng(ret[0], ret[1]);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, (float) 15.0));
        drawBleats(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setInfoWindowAdapter(new FakeWindowAdapter(this));

        /* end testing filterBleats */

        //37.427490, -122.170265 (main quad)
    }

    private int getTextStyle(int size) {
        switch(size) {
            case 10:
                return R.style.textSize10;
            case 11:
                return R.style.textSize11;
            case 12:
                return R.style.textSize12;
            case 13:
                return R.style.textSize13;
            case 14:
                return R.style.textSize14;
            case 15:
                return R.style.textSize15;
            case 16:
                return R.style.textSize16;
            case 17:
                return R.style.textSize17;
            case 18:
                return R.style.textSize18;
            case 19:
                return R.style.textSize19;
            case 20:
                return R.style.textSize20;
        }
        return R.style.textSize10;
    }

    public String wordWrap(String message, int fontSize)
    {
        int threshold = 350/fontSize;
        for(int i = threshold; i < message.length()-1; i+=threshold)
        {
            if(message.charAt(i) != ' ' && message.charAt(i-1) != ' ')
            {
                message = message.substring(0, i) + "-\n" + message.substring(i, message.length());
                i+=2;
            }
            else
            {
                message = message.substring(0, i) + "\n" + message.substring(i, message.length());
                i++;
            }
        }
        return message;
    }

    public void drawBleats(boolean ... forced) {
        long curTime = Calendar.getInstance().getTimeInMillis();
        if (curTime - lastUpdated > Constants.WAIT_TIME || (forced.length > 0 && forced[0])) {  // 2 minutes
            //  mMap.clear(); // not needed?
            /* begin testing filterBleats */
            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            FilterBleats filterBleats = new FilterBleats(this);
            List<Bleat> result = filterBleats.filter(curTime - Constants.EXPIRE_DURATION, bounds);

            IconGenerator iconFactory = new IconGenerator(this);

            if (result != null) {
                for (Bleat bleat : result) {
                    Log.d("mappp", bleat.getMessage());
                    if (!bleatMap.containsKey(bleat.getBID())) {

                        double lat =  bleat.getLatitude();
                        double lng = bleat.getLongitude();
                        if(bleat.getTime()<1455059660758L) {
                            lat += (((bleat.getBID() + " lat").hashCode() % 1024) - 512) / 1024.0 * 0.005;
                            lng += (((bleat.getBID() + " lng").hashCode() % 1024) - 512) / 1024.0 * 0.005;
                        }

                        int upvotes = bleat.computeNetUpvotes();
                        int fontSize = Math.min(20, (int) (Math.log(Math.max(upvotes + 1, 1)) / Math.log(1.5) + 10.00001));
                        String message = wordWrap(bleat.getMessage(), fontSize);
                        iconFactory.setTextAppearance(getTextStyle(fontSize));
                        MarkerOptions markerOptions = new MarkerOptions().
                                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(message))).
                                position(new LatLng(lat, lng)).
                                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                        markerOptions.snippet(bleat.getBID());
                        if(bleat.getBID() == "e5c915c3-060e-4c26-a225-d809a7922988" || bleat.getBID() == "6742a95c-ee06-4628-9aae-0e3cc69b30b0")
                            Log.d("map", "putting snippet " + bleat.getBID());
                        markerOptions.title(message);
                        Marker m = mMap.addMarker(markerOptions);
                    }
                    bleatMap.put(bleat.getBID(), bleat);
                    if(bleat.getBID() == "e5c915c3-060e-4c26-a225-d809a7922988"|| bleat.getBID() == "6742a95c-ee06-4628-9aae-0e3cc69b30b0")
                        Log.d("map", "putting bleat " + bleat.getBID());
                }
                Log.d("map", "done");
            } else {
                Log.d("map", "ggwp");
            }
        }
        lastUpdated = curTime;
    }

    public class FakeWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private Context context = null;

        public FakeWindowAdapter(Context context) {
            this.context = context;
        }

        public View getInfoWindow(Marker marker) {
            View v = ((Activity) context).getLayoutInflater().inflate(R.layout.no_infowindow, null);
            return v;
        }

        public View getInfoContents(Marker marker) {
            return null;
        }
    }



    public void onCameraChange(CameraPosition change) {
        Log.d("map", "cameraChanged");
        drawBleats(true);
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
