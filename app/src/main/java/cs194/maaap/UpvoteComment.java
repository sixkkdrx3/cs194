package cs194.maaap;

import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by kaidi on 2/21/16.
 */

public class UpvoteComment extends AsyncTask<Comment, Void, Comment> {

    private CommentAction commentAction;
    private final WeakReference<TextView> textViewReference;
    private final WeakReference<ImageView> upView;
    private final WeakReference<ImageView> downView;
    private boolean isUpvoted;

    public UpvoteComment(CommentAction commentAction, TextView textView, ImageView upImg, ImageView downImg) {
        textViewReference = new WeakReference<TextView>(textView);
        this.commentAction = commentAction;
        upView =  new WeakReference<ImageView>(upImg);
        downView = new WeakReference<ImageView>(downImg);
        isUpvoted = false;
    }

    protected Comment doInBackground(Comment... comment) {
        isUpvoted = commentAction.upvoteComment(comment[0]);
        return comment[0];
    }

    protected void onPostExecute(Comment comment) {
        if (textViewReference == null) return;
        int number = comment.computeNetUpvotes();
        final TextView textView = textViewReference.get();
        final ImageView up = upView.get();
        final ImageView down = downView.get();
        if (textView != null) {
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

