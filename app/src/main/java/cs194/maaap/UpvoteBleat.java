package cs194.maaap;

import android.os.AsyncTask;

/**
 * Created by kaidi on 2/7/16.
 */
public class UpvoteBleat extends AsyncTask<Bleat, Void, Void> {

    private BleatAction bleatAction;

    public UpvoteBleat(BleatAction bleatAction) {
            this.bleatAction = bleatAction;
        }

    protected Void doInBackground(Bleat... bleat) {
        bleatAction.upvoteBleat(bleat[0]);
        return null;
    }
}

