package cs194.maaap;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by kaidi on 2/7/16.
 */
public class UpvoteBleat extends AsyncTask<Bleat, Void, Bleat> {

    private BleatAction bleatAction;
    private final WeakReference<TextView> textViewReference;
    private final WeakReference<ImageView> upView;
    private final WeakReference<ImageView> downView;
    private boolean isUpvoted;

    public UpvoteBleat(BleatAction bleatAction, TextView textView, ImageView upImg, ImageView downImg) {
        textViewReference = new WeakReference<TextView>(textView);
        upView =  new WeakReference<ImageView>(upImg);
        downView = new WeakReference<ImageView>(downImg);
        this.bleatAction = bleatAction;
        isUpvoted = false;
    }

    protected Bleat doInBackground(Bleat... bleat) {
        isUpvoted = bleatAction.upvoteBleat(bleat[0]);
        return bleat[0];
    }

    protected void onPostExecute(Bleat bleat) {
        if (textViewReference == null) return;
        int number = bleat.computeNetUpvotes();
        final TextView textView = textViewReference.get();
        final ImageView up = upView.get();
        final ImageView down = downView.get();
        if (textView != null ) {
            textView.setText(Integer.toString(number));
            if (!isUpvoted) {
                up.setImageResource(R.drawable.up_highlight);
                down.setImageResource(R.drawable.sarrowdown);
            }
            else{
                up.setImageResource(R.drawable.sarrowup);
                down.setImageResource(R.drawable.sarrowdown);
            }
        }
    }
}

