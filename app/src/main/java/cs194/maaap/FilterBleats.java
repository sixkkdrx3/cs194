package cs194.maaap;

import android.support.annotation.NonNull;
import android.util.Log;
import android.content.ContextWrapper;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaidi on 1/31/16.
 */
public class FilterBleats extends ContextWrapper {
    private BleatAction bleatAction;
    private List<Bleat> result = null;

    public FilterBleats(Context base) {
        super(base);
        BleatAction bleatAction = new BleatAction((MapsActivity)base);
        getBleats getBleats = new getBleats(bleatAction);
        List<Bleat> result;
        try {
            result = getBleats.execute().get();
            this.result = result;
        } catch (Exception e) {
            Log.d("map", "ggwp");
        }
    }

    public List<Bleat> filter(long minTime, double minLat, double maxLat,
                              double minLong, double maxLong) {
        if (result == null) return result;

        List<Bleat> filterResult = new ArrayList<Bleat>();
        for (Bleat bleat : result) {
            Log.d("map", bleat.getTime() + " " + bleat.getLatitude() + " " + bleat.getLongitude() + " " + bleat.getMessage());
            if (bleat.getTime() > minTime && bleat.getLatitude() > minLat &&
                bleat.getLatitude() < maxLat && bleat.getLongitude() > minLong &&
                bleat.getLongitude() < maxLong) {
                filterResult.add(bleat);
            }
        }
        return filterResult;
    }
}
