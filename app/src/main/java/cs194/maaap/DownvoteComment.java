package cs194.maaap;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by kaidi on 2/21/16.
 */

public class DownvoteComment extends AsyncTask<Comment, Void, Comment> {

    private CommentAction commentAction;
    private final WeakReference<TextView> textViewReference;
    private final WeakReference<ImageView> upvote;
    private final WeakReference<ImageView> downvote;
    private boolean isDownvoted;

    public DownvoteComment(CommentAction commentAction, TextView textView, ImageView upView, ImageView downView) {
        this.commentAction = commentAction;
        textViewReference = new WeakReference<TextView>(textView);
        upvote = new WeakReference<ImageView>(upView);
        downvote = new WeakReference<ImageView>(downView);
        isDownvoted = false;
    }

    protected Comment doInBackground(Comment... comment) {
        isDownvoted = commentAction.downvoteComment(comment[0]);
        return comment[0];
    }

    protected void onPostExecute(Comment comment) {
        if (textViewReference == null) return;
        int number = comment.computeNetUpvotes();
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
