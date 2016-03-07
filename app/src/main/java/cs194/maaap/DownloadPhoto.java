package cs194.maaap;

import android.os.AsyncTask;

import java.io.File;
import java.util.List;

/**
 * Created by kaidi on 3/6/16.
 */
public class DownloadPhoto extends AsyncTask<String, Void, File> {
    private BleatAction bleatAction;

    public DownloadPhoto(BleatAction bleatAction) { this.bleatAction = bleatAction; }

    protected File doInBackground(String... params) {
        return bleatAction.downloadPhoto(params[0]);
    }
}