package cs194.maaap;

import android.os.AsyncTask;

import java.io.File;

/**
 * Created by kaidi on 3/6/16.
 */

public class UploadPhoto extends AsyncTask<File, Void, Void> {

    private BleatAction bleatAction;

    public UploadPhoto(BleatAction bleatAction) { this.bleatAction = bleatAction; }
    protected Void doInBackground(File... file) {
        bleatAction.uploadPhoto(file[0]);
        return null;
    }
}