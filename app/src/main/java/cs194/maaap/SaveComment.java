package cs194.maaap;

import android.os.AsyncTask;

/**
 * Created by kaidi on 2/21/16.
 */

public class SaveComment extends AsyncTask<String, Void, Comment> {

    private CommentAction commentAction;
    private BleatDisplay bleatDisplay;

    public SaveComment(CommentAction commentAction, BleatDisplay bleatDisplay) {
        this.bleatDisplay = bleatDisplay;
        this.commentAction = commentAction;
    }

    protected Comment doInBackground(String... message) {
        Comment comment = commentAction.saveComment(message[0]);
        return comment;
    }

    protected void onPostExecute(Comment comment) {
        bleatDisplay.addView(comment);
    }

}