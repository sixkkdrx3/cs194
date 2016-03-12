package cs194.maaap;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by kaidi on 2/21/16.
 */

public class DownvoteComment extends AsyncTask<Comment, Void, Comment> {

    private CommentAction commentAction;
    private final WeakReference<TextView> textViewReference;

    public DownvoteComment(CommentAction commentAction, TextView textView) {
        this.commentAction = commentAction;
        textViewReference = new WeakReference<TextView>(textView);
    }

    protected Comment doInBackground(Comment... comment) {
        commentAction.downvoteComment(comment[0]);
        return comment[0];
    }

    protected void onPostExecute(Comment comment) {
        if (textViewReference == null) return;
        int number = comment.computeNetUpvotes();
        final TextView textView = textViewReference.get();
        if (textView != null) {
            textView.setText(Integer.toString(number));
        }
    }
}
