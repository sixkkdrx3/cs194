package cs194.maaap;

import android.os.AsyncTask;
import java.util.List;

/**
 * Created by kaidi on 1/31/16.
 */
public class GetBleats extends AsyncTask<Void, Void, List<Bleat> > {

    private BleatAction bleatAction;
    private GetBleats.Runnable runnable;

    public GetBleats(BleatAction bleatAction) {
        this.bleatAction = bleatAction;
        runnable = null;
    }

    public GetBleats(BleatAction bleatAction, GetBleats.Runnable runnable) {
        this.bleatAction = bleatAction;
        this.runnable = runnable;
    }

    protected List<Bleat> doInBackground(Void... Params) {
        return bleatAction.getBleats();
    }

    protected void onPostExecute(List<Bleat> bleats) {
        DataStore store = DataStore.getInstance();
        synchronized(store) {
            store.updateBleats(bleats);
            store.bleatsDownloaded = true;
            store.notifyAll();
        }
        if(runnable != null) {
            runnable.run(bleats);
        }
    }

    public abstract static class Runnable {
        public abstract void run(List<Bleat> bleats);
    }
}
