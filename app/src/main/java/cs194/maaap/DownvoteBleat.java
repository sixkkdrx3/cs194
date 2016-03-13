package cs194.maaap;

import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by kaidi on 2/7/16.
 */

public class DownvoteBleat extends AsyncTask<Bleat, Void, Bleat> {

    private BleatAction bleatAction;
    private final WeakReference<TextView> textViewReference;
    private final WeakReference<ImageView> upvote;
    private final WeakReference<ImageView> downvote;
    private boolean isDownvoted;

    public DownvoteBleat(BleatAction bleatAction, TextView textView, ImageView upView, ImageView downView) {
        textViewReference = new WeakReference<TextView>(textView);
        upvote = new WeakReference<ImageView>(upView);
        downvote = new WeakReference<ImageView>(downView);
        this.bleatAction = bleatAction;
        isDownvoted = false;
    }

    protected Bleat doInBackground(Bleat... bleat) {
        isDownvoted = bleatAction.downvoteBleat(bleat[0]);
        return bleat[0];
    }

    protected void onPostExecute(Bleat bleat) {
        if (textViewReference == null) return;
        int number = bleat.computeNetUpvotes();
        final ImageView voteup = upvote.get();
        final ImageView voteDown = downvote.get();
        final TextView textView = textViewReference.get();
        if (textView != null) {
            textView.setText(Integer.toString(number));
            if (!isDownvoted) {
                voteup.setImageResource(R.drawable.sarrowup);
                voteDown.setImageResource(R.drawable.down_highlight);
            }
            else{
                voteup.setImageResource(R.drawable.sarrowup);
                voteDown.setImageResource(R.drawable.sarrowdown);
            }
        }
    }
}