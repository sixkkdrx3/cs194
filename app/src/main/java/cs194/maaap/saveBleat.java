package cs194.maaap;

import android.os.AsyncTask;

/**
 * Created by kaidi on 1/24/16.
 */
public class saveBleat extends AsyncTask<String, Void, Void> {

    private BleatAction bleatAction;

    public saveBleat(BleatAction bleatAction) {
        this.bleatAction = bleatAction;
    }

    protected Void doInBackground(String... message) {
        bleatAction.saveBleat(message[0]);
        return null;
    }
}
