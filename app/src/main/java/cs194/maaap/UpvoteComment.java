package cs194.maaap;

import android.os.AsyncTask;

/**
 * Created by kaidi on 2/21/16.
 */

public class UpvoteComment extends AsyncTask<Comment, Void, Void> {

    private CommentAction commentAction;

    public UpvoteComment(CommentAction commentAction) { this.commentAction = commentAction; }

    protected Void doInBackground(Comment... comment) {
        commentAction.upvoteComment(comment[0]);
        return null;
    }
}

