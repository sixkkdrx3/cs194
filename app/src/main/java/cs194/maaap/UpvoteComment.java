package cs194.maaap;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by kaidi on 2/21/16.
 */

public class UpvoteComment extends AsyncTask<Comment, Void, Comment> {

    private CommentAction commentAction;
    private final WeakReference<TextView> textViewReference;

    public UpvoteComment(CommentAction commentAction, TextView textView) {
        textViewReference = new WeakReference<TextView>(textView);
        this.commentAction = commentAction;
    }

    protected Comment doInBackground(Comment... comment) {
        commentAction.upvoteComment(comment[0]);
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

