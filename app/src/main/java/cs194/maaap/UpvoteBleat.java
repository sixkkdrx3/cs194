package cs194.maaap;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by kaidi on 2/7/16.
 */
public class UpvoteBleat extends AsyncTask<Bleat, Void, Bleat> {

    private BleatAction bleatAction;
    private final WeakReference<TextView> textViewReference;

    public UpvoteBleat(BleatAction bleatAction, TextView textView) {
        textViewReference = new WeakReference<TextView>(textView);
        this.bleatAction = bleatAction;
    }

    protected Bleat doInBackground(Bleat... bleat) {
        bleatAction.upvoteBleat(bleat[0]);
        return bleat[0];
    }

    protected void onPostExecute(Bleat bleat) {
        if (textViewReference == null) return;
        int number = bleat.computeNetUpvotes();
        final TextView textView = textViewReference.get();
        if (textView != null) {
            textView.setText(Integer.toString(number));
        }
    }
}

