package cs194.maaap;

import android.util.Log;
import android.content.ContextWrapper;
import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaidi on 1/31/16.
 */
public class FilterBleats extends ContextWrapper {
    private BleatAction bleatAction;
    private List<Bleat> result = null;

    public FilterBleats(Context base) {
        super(base);
        BleatAction bleatAction = new BleatAction((MapsActivity)base, "MapsActivity");

        //Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        //double a = bounds.northeast.latitude;
        //eav.put(":latMin", new AttributeValue().withN());
        //eav.put(":latMax", new AttributeValue().withN(bounds.southwest.latitude));

        //DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
//                .withFilterExpression();
        GetBleats getBleats = new GetBleats(bleatAction);

        try {
            result = getBleats.execute().get();
        } catch (Exception e) {
            Log.d("map", "ggwp");
        }
    }

    public List<Bleat> filter(long minTime, LatLngBounds bounds) {
        if (result == null) return result;

        List<Bleat> filterResult = new ArrayList<Bleat>();
        for (Bleat bleat : result) {
            Log.d("map", bleat.getTime() + " " + bleat.getLatitude() + " " + bleat.getLongitude() + " " + bleat.getMessage());
            LatLng pos = new LatLng(bleat.getLatitude(), bleat.getLongitude());
            if (bleat.getTime() > minTime && bounds.contains(pos)) {
                filterResult.add(bleat);
            }
        }
        return filterResult;
    }
}
