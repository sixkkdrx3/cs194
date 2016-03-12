package cs194.maaap;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by kaidi on 3/11/16.
 */
public class DrawBleats extends AsyncTask<Void, Void, List<Bleat> > {
    private BleatAction bleatAction;
    private MapFragment mapFragment;

    public DrawBleats(BleatAction bleatAction, MapFragment mapFragment) {
        this.bleatAction = bleatAction;
        this.mapFragment = mapFragment;
    }

    protected List<Bleat> doInBackground(Void... Params) {
        return bleatAction.getBleats();
    }

    protected void onPostExecute(List<Bleat> bleats) {
        mapFragment.drawBleats(bleats);
    }
}
