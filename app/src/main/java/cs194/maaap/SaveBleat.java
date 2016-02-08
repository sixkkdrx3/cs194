package cs194.maaap;

import android.os.AsyncTask;

/**
 * Created by kaidi on 1/24/16.
 */
public class SaveBleat extends AsyncTask<String, Void, Void> {

    private BleatAction bleatAction;

    public SaveBleat(BleatAction bleatAction) {
        this.bleatAction = bleatAction;
    }

    protected Void doInBackground(String... message) {
        bleatAction.saveBleat(message[0]);
        return null;
    }
}
