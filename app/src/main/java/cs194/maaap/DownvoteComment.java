package cs194.maaap;

import android.os.AsyncTask;

/**
 * Created by kaidi on 2/21/16.
 */

public class DownvoteComment extends AsyncTask<Comment, Void, Void> {

    private CommentAction commentAction;

    public DownvoteComment(CommentAction commentAction) { this.commentAction = commentAction; }

    protected Void doInBackground(Comment... comment) {
        commentAction.downvoteComment(comment[0]);
        return null;
    }
}
