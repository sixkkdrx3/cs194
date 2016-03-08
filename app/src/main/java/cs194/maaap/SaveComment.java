package cs194.maaap;

import android.os.AsyncTask;

/**
 * Created by kaidi on 2/21/16.
 */

public class SaveComment extends AsyncTask<String, Void, Comment> {

    private CommentAction commentAction;

    public SaveComment(CommentAction commentAction) {
        this.commentAction = commentAction;
    }

    protected Comment doInBackground(String... message) {
        Comment comment = commentAction.saveComment(message[0]);
        return comment;
    }
}