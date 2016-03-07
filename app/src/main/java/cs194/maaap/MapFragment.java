package cs194.maaap;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Map;

//Old imports
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private HashMap<String, Bleat> bleatMap;
    private HashMap<String, MarkerInfo> markerInfoMap;
    private long lastUpdated;
    private MainActivity parentActivity;
    private int thumbnailSize = 128;

    public class MarkerInfo {
        public Bitmap bitmap;
        public Bleat[] bleats;
        public Marker marker;
        public MarkerOptions markerOptions;
        public float anchorU;
        public float anchorV;
        public boolean isConsolidated;
        public boolean forceLoc;

        public MarkerInfo(Bitmap bitmap, Bleat[] bleats, MarkerOptions markerOptions, float anchorU, float anchorV, boolean isConsolidated, boolean forceLoc) {
            this.bitmap = bitmap;
            this.bleats = bleats;
            this.marker = null;
            this.markerOptions = markerOptions;
            this.anchorU = anchorU;
            this.anchorV = anchorV;
            this.isConsolidated = isConsolidated;
            this.forceLoc = forceLoc;
        }

        public Bleat maxBleat()
        {
            Bleat maxb = bleats[0];
            for(int i = 1; i < bleats.length; i++)
            {
                if(bleats[i].gt(maxb))
                    maxb = bleats[i];
            }
            return maxb;
        }

        public boolean gt(MarkerInfo other) {
            return maxBleat().gt(other.maxBleat());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_map, container, false);


        bleatMap = new HashMap<String, Bleat>();
        markerInfoMap = new HashMap<String, MarkerInfo>();
        lastUpdated = 0;
        parentActivity = (MainActivity)getActivity();

        mGoogleApiClient = new GoogleApiClient.Builder(parentActivity).addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.bleat_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(parentActivity, BleatCreateActivity.class);
                intent.putExtra("coords", getGPS());
                parentActivity.startActivity(intent);
            }
        });

        try {
            BleatAction bleatAction = new BleatAction(parentActivity, "Main");
            GetBleats getBleats = new GetBleats(bleatAction);
            DataStore.getInstance().updateBleats(getBleats.execute().get());
        } catch (Exception e) { }
        return v;
    }

    public boolean onMarkerClick(Marker marker) {
        Log.d("map", "marker clicked " + marker.getSnippet());
        FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
        String bid = marker.getSnippet();
        Bleat[] bleats = markerInfoMap.get(bid).bleats;
        Intent displayIntent;
        if(bleats.length == 1) {
            displayIntent = new Intent(parentActivity, BleatDisplay.class);
            displayIntent.putExtra("myBID", bleats[0].getBID());
        }
        else
        {
            displayIntent = new Intent(parentActivity, MultiBleatDisplay.class);
            //Temporary hack
            List<Bleat> blah = new ArrayList<Bleat>();
            for (Bleat bleat : bleats) blah.add(bleat);
            displayIntent.putExtra("myBIDs", (Utils.extractBIDs(blah)).toArray(new String[blah.size()]));
            //End temporary hack
        }

        parentActivity.startActivity(displayIntent);
        //MapsActivity.this.finish();
//        DisplayDialogFragment ddf = new DisplayDialogFragment(bleatMap.get(bid));
//
//        ddf.show(ft, "showBleat");
//        marker.showInfoWindow();
        //Log.d("valll", "end");
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "onMapReady called");
        mMap = googleMap;
        assert (mMap != null);
        double ret[] = getGPS();
        Log.d("map", ret[0] + " " + ret[1]);
        LatLng currentLocation = new LatLng(ret[0], ret[1]);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, (float) 15.0));
        drawBleats(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setInfoWindowAdapter(new FakeWindowAdapter(parentActivity));

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

    public static Pair<Integer, Integer> scalePreserveRatio(int originalWidth, int originalHeight, int scaledWidth, int scaledHeight)
    {
        if(originalWidth*scaledHeight < originalHeight*scaledWidth)
        {
            return new Pair<Integer, Integer>(originalWidth*scaledHeight/originalHeight, scaledHeight);
        }
        else
        {
            return new Pair<Integer, Integer>(scaledWidth, originalHeight*scaledWidth/originalWidth);
        }
    }

    private MarkerInfo addMarker(Bleat bleat, int fontSize, LatLng location) {
        Bitmap markerBitmap;
        double lat = bleat.getLatitude(), lng = bleat.getLongitude();
        float anchorU, anchorV;
        if(bleat.getMessage().length() < 200) {
            IconGenerator iconFactory = new IconGenerator(parentActivity);
            // Remove the random function below later
            if (location == null) {
                if (bleat.getTime() < 1455059660758L) {
                    lat += (((bleat.getBID() + " lat").hashCode() % 1024) - 512) / 1024.0 * 0.005;
                    lng += (((bleat.getBID() + " lng").hashCode() % 1024) - 512) / 1024.0 * 0.005;
                }
            } else {
                lat = location.latitude;
                lng = location.longitude;
            }

            String message = wordWrap(bleat.getMessage(), fontSize);
            iconFactory.setTextAppearance(getTextStyle(fontSize));
            markerBitmap = iconFactory.makeIcon(message);
            anchorU = iconFactory.getAnchorU();
            anchorV = iconFactory.getAnchorV();
        }
        else
        {
            byte[] imageBytes = Base64.decode(bleat.getMessage().getBytes(), Base64.DEFAULT);
            Bitmap fullBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Pair<Integer, Integer> size = scalePreserveRatio(fullBitmap.getWidth(), fullBitmap.getHeight(), thumbnailSize, thumbnailSize);
            markerBitmap = fullBitmap.createScaledBitmap(fullBitmap, size.first, size.second, true);
            anchorU = 0.5f;
            anchorV = 1.0f;
        }
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)).
                position(new LatLng(lat, lng)).
                anchor(anchorU, anchorV);
        markerOptions.snippet(bleat.getBID());
        //markerOptions.title(message);
        //Marker m = mMap.addMarker(markerOptions);
        MarkerInfo markerInfo = new MarkerInfo(markerBitmap, new Bleat[]{bleat}, markerOptions, anchorU, anchorV, false, location != null);
        markerInfoMap.put(bleat.getBID(), markerInfo);
        return markerInfo;
    }

    private MarkerInfo addConsolidatedMarker(Bleat[] bleats, LatLng location)
    {
        double lat = 0.0, lng = 0.0;
        if(location == null) {
            for (int i = 0; i < bleats.length; i++) {
                lat += bleats[i].getLatitude();
                lng += bleats[i].getLongitude();
            }
            lat /= bleats.length;
            lng /= bleats.length;
        }
        else{
            lat = location.latitude;
            lng = location.longitude;
        }



        // Remove the random function below later
        if(bleats[0].getTime()<1455059660758L) {
            lat += (((bleats[0].getBID() + " lat").hashCode() % 1024) - 512) / 1024.0 * 0.005;
            lng += (((bleats[0].getBID() + " lng").hashCode() % 1024) - 512) / 1024.0 * 0.005;
        }

        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_message_black);
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)).
                position(new LatLng(lat, lng)).
                anchor(0.5f, 1.0f);
        markerOptions.snippet(bleats[0].getBID());
        //markerOptions.title(message);
        //Marker m = mMap.addMarker(markerOptions);
        MarkerInfo markerInfo = new MarkerInfo(markerBitmap, bleats, markerOptions, 0.5f, 1.0f, true, location != null);
        markerInfoMap.put(bleats[0].getBID(), markerInfo);
        return markerInfo;
    }

    public void drawBleats(boolean ... forced) {
        long curTime = Calendar.getInstance().getTimeInMillis();
        if (curTime - lastUpdated > Constants.WAIT_TIME || (forced.length > 0 && forced[0])) {  // 2 minutes
            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            assert (mMap != null);
            assert(((MapFragment)(parentActivity.adapter.getItem(0))).mMap != null);
            FilterBleats filterBleats = new FilterBleats(parentActivity);
            List<Bleat> result = filterBleats.filter(curTime - Constants.EXPIRE_DURATION, bounds);
            bleatMap.clear();
            for(MarkerInfo markerInfo : markerInfoMap.values()) {
                markerInfo.marker.remove();
            }
            markerInfoMap.clear();

            if (result != null) {
                for (Bleat bleat : result) {
                    Log.d("mappp", bleat.getMessage());
                    if (!bleatMap.containsKey(bleat.getBID())) {
                        addMarker(bleat, Constants.BLEAT_DEFAULT_SIZE, null);
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

        consolidateBleats();

        for(MarkerInfo markerInfo : markerInfoMap.values())
        {
            markerInfo.marker = mMap.addMarker(markerInfo.markerOptions);
        }

        lastUpdated = curTime;
    }

    double overlapFactor = 0;


    public Pair<Point, Point> boundingRectangle(MarkerInfo markerInfo)
    {
        MarkerOptions options = markerInfo.markerOptions;
        Projection projection = mMap.getProjection();
        LatLng latLng = options.getPosition();
        Point screenPos = projection.toScreenLocation(latLng);
        Bitmap bitmap = markerInfo.bitmap;
        int tlx = (int) (screenPos.x - markerInfo.anchorU*bitmap.getWidth() + overlapFactor * bitmap.getWidth());
        int tly = (int) (screenPos.y - markerInfo.anchorV*bitmap.getHeight()+ overlapFactor * bitmap.getHeight());
        Point topLeft = new Point(tlx, tly);
        Point bottomRight = new Point((int) (tlx + bitmap.getWidth()*(1-2*overlapFactor)), (int) (tly + bitmap.getHeight()*(1-2*overlapFactor)));
        return new Pair<Point, Point>(topLeft, bottomRight);
    }


    public LatLng separate(MarkerInfo mi, MarkerInfo mj)
    {
        int width = (int) (mj.bitmap.getWidth()*(1-2*overlapFactor));
        int height = (int) (mj.bitmap.getHeight()*(1-2*overlapFactor));
        Pair<Point, Point> boxi = boundingRectangle(mi);
        Pair<Point, Point> boxj = boundingRectangle(mj);


        Point current = new Point((int) (boxj.first.x + mj.anchorU*width), (int) (boxj.first.y+mj.anchorV*height));

        int leftShift = boxj.first.x-boxi.first.x+width+2;
        int rightShift = boxi.second.x-boxj.first.x+2;
        int topShift = boxj.first.y-boxi.first.y+height+2;
        int bottomShift = boxi.second.y-boxj.first.y+2;


        Pair<Integer, Point>[] candidates = new Pair[] {
                new Pair<Integer, Point>(leftShift, new Point(current.x-leftShift, current.y)),
                new Pair<Integer, Point>(rightShift, new Point(current.x+rightShift, current.y)),
                new Pair<Integer, Point>(topShift, new Point(current.x, current.y-topShift)),
                new Pair<Integer, Point>(bottomShift, new Point(current.x, current.y+bottomShift))};

        Point newScreenPos = Collections.min(Arrays.asList(candidates), new Comparator<Pair<Integer, Point>>() {
            @Override
            public int compare(Pair<Integer, Point> pair1, Pair<Integer, Point> pair2) {
                return pair1.first-pair2.first;
            }
        }).second;

        Projection projection = mMap.getProjection();
        return projection.fromScreenLocation(newScreenPos);
    }

    public boolean isOverlapping(MarkerInfo a, MarkerInfo b)
    {
        Pair<Point, Point> aBox = boundingRectangle(a);
        Pair<Point, Point> bBox = boundingRectangle(b);
        //Log.d("consolidate", aBox.toString());
        //Log.d("consolidate", bBox.toString());
        return !(aBox.first.x >= bBox.second.x || aBox.second.x <= bBox.first.x ||
                aBox.first.y >= bBox.second.y || aBox.second.y <= bBox.first.y);
    }

    public Comparator<Map.Entry<String, MarkerInfo>> getEntryComparator()
    {
        return Collections.reverseOrder(new Comparator<Map.Entry<String, MarkerInfo>>() {
            @Override
            public int compare(Map.Entry<String, MarkerInfo> entry1, Map.Entry<String, MarkerInfo> entry2) {
                if(entry1.getValue().gt(entry2.getValue()))
                    return 1;
                else if(entry2.getValue().gt(entry1.getValue()))
                    return -1;
                else
                    return 0;
            }
        });
    }

    public Map.Entry<String, MarkerInfo>[] getMarkerInfoEntries()
    {
        Map.Entry<String, MarkerInfo>[] entries = (Map.Entry<String, MarkerInfo>[]) markerInfoMap.entrySet().toArray(new Map.Entry[markerInfoMap.size()]);

        Arrays.sort(entries, getEntryComparator());
        return entries;
    }

    public void consolidateBleats()
    {
        boolean hasChangedOuter;
        do {
            hasChangedOuter = false;
            boolean hasChanged;
            do {
                hasChanged = false;

                Map.Entry<String, MarkerInfo>[] entries = getMarkerInfoEntries();

                for (int i = 0; i < entries.length; i++) {
                    if (entries[i] == null)
                        continue;
                    MarkerInfo mi = entries[i].getValue();

                    for (int j = i + 1; j < entries.length; j++) {
                        if (entries[j] == null)
                            continue;
                        MarkerInfo mj = entries[j].getValue();
                        if (isOverlapping(mi, mj)) {
                            //if (!mi.isConsolidated && !mj.isConsolidated) {
                            if (false) {
                                hasChanged = true;
                                if (mi.gt(mj)) //turn mj into a consolidated bleat;
                                {
                                    //mj.marker.remove();
                                    markerInfoMap.remove(mj.bleats[0].getBID());
                                    mj = addConsolidatedMarker(new Bleat[]{mj.bleats[0]}, null);
                                    entries[j].setValue(mj);
                                } else {
                                    //mi.marker.remove();
                                    markerInfoMap.remove(mi.bleats[0].getBID());
                                    mi = addConsolidatedMarker(new Bleat[]{mi.bleats[0]}, null);
                                    entries[i].setValue(mi);
                                }
                            } else if (mi.isConsolidated) //merge them
                            {
                                Log.d("consolidate", "merging " + mi.maxBleat().getMessage());
                                Log.d("consolidate", "merging " + mi.maxBleat().getMessage());
                                hasChanged = true;
                                //mi.marker.remove();
                                //mj.marker.remove();
                                markerInfoMap.remove(mi.bleats[0].getBID());
                                markerInfoMap.remove(mj.bleats[0].getBID());
                                Bleat[] bothBleats = new Bleat[mi.bleats.length + mj.bleats.length];
                                System.arraycopy(mi.bleats, 0, bothBleats, 0, mi.bleats.length);
                                System.arraycopy(mj.bleats, 0, bothBleats, mi.bleats.length, mj.bleats.length);
                                mi = addConsolidatedMarker(bothBleats, null);
                                entries[i].setValue(mi);
                                entries[j] = null;
                            }
                        }
                    }
                }
                if(hasChanged)
                    hasChangedOuter = true;
            } while (hasChanged);

            Map.Entry<String, MarkerInfo>[] entries = getMarkerInfoEntries();
            Log.d("consolidate", "entries size: " + entries.length);
            for (int i = 0; i < entries.length; i++) {
                MarkerInfo mi = entries[i].getValue();
                //if (mi.isConsolidated) {
                if (true) {
                    ArrayList<Integer> overlapping = new ArrayList<Integer>();
                    for (int j = 0; j < i; j++) {
                        if (entries[j] == null)
                            continue;
                        MarkerInfo mj = entries[j].getValue();
                        if (isOverlapping(mi, mj)) {
                            Log.d("consolidate", i + " " + j);
                            hasChangedOuter = true;
                            overlapping.add(j);
                        }
                    }
                    if (overlapping.size() > 1 || (mi.forceLoc && overlapping.size() > 0)) {
                        //Log.d("consolidate", "before consolidating " + i + ", size " + markerInfoMap.size());
                        Log.d("consolidate", "before consolidating " + mi.maxBleat().getMessage());
                        ArrayList<Bleat> bleatList = new ArrayList<Bleat>();
                        //Log.d("consolidate", "osize "+overlapping.size());
                        int kstart = mi.forceLoc ? 0 : 1;
                        //Log.d("consolidate", "kstart "+kstart);
                        for (int k = kstart; k < overlapping.size(); k++) {
                            int j = overlapping.get(k);
                            MarkerInfo mj = entries[j].getValue();
                            Log.d("consolidate", "during consolidating " + mj.maxBleat().getMessage());
                            bleatList.addAll(Arrays.asList(mj.bleats));
                            if(k > kstart)
                                entries[j] = null;
                            markerInfoMap.remove(mj.bleats[0].getBID());
                            //mj.marker.remove();
                        }
                        bleatList.addAll(Arrays.asList(mi.bleats));
                        markerInfoMap.remove(mi.bleats[0].getBID());
                        //mi.marker.remove();
                        entries[i] = null;
                        entries[overlapping.get(kstart)].setValue(addConsolidatedMarker(bleatList.toArray(new Bleat[bleatList.size()]), null));
                        //Log.d("consolidate", "after consolidate, size " + markerInfoMap.size());
                    } else if (overlapping.size() == 1) {
                        int j = overlapping.get(0);
                        MarkerInfo mj = entries[j].getValue();
                        LatLng newLoc = separate(mj, mi);


                        Log.d("consolidate", "moving \"" + mi.maxBleat().getMessage() + "\" to side of \"" + mj.maxBleat().getMessage() + "\"");

                        //mi.marker.remove();
                        markerInfoMap.remove(mi.bleats[0].getBID());
                        if(mi.isConsolidated)
                            mi = addConsolidatedMarker(mi.bleats, newLoc);
                        else
                            mi = addMarker(mi.bleats[0],  Constants.BLEAT_DEFAULT_SIZE, newLoc);
                        entries[i].setValue(mi);

                        for (int k = 0; k < j; k++) {
                            if (entries[k] == null)
                                continue;
                            MarkerInfo mk = entries[k].getValue();
                            if (isOverlapping(mi, mk)) {
                                //mi.marker.remove();
                                //mj.marker.remove();
                                markerInfoMap.remove(mi.bleats[0].getBID());
                                markerInfoMap.remove(mj.bleats[0].getBID());
                                Bleat[] bothBleats = new Bleat[mi.bleats.length + mj.bleats.length];
                                System.arraycopy(mi.bleats, 0, bothBleats, 0, mi.bleats.length);
                                System.arraycopy(mj.bleats, 0, bothBleats, mi.bleats.length, mj.bleats.length);
                                entries[j].setValue(addConsolidatedMarker(bothBleats, null));
                                entries[i] = null;
                                break;
                            }
                        }
                    }
                }
            }
        } while(hasChangedOuter);
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
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
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
        } catch (Exception e) {
            Log.d("error", "gg");
            ret[0] = 37.43;
            ret[1] = -122.17;
        }
        return ret;
    }
}