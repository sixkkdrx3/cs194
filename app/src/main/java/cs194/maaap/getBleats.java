package cs194.maaap;

import android.os.AsyncTask;
import java.util.List;

/**
 * Created by kaidi on 1/31/16.
 */
public class GetBleats extends AsyncTask<Void, Void, List<Bleat> > {

    private BleatAction bleatAction;

    public GetBleats(BleatAction bleatAction) {
        this.bleatAction = bleatAction;
    }

    protected List<Bleat> doInBackground(Void... Params) {
        return bleatAction.getBleats();
    }
}
