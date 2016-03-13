package cs194.maaap;

import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.List;

/**
 * Created by kaidi on 2/21/16.
 */

public class GetComments extends AsyncTask<Void, Void, List<Comment> > {

    private CommentAction commentAction;
    private BleatDisplay bleatDisplay;

    public GetComments(CommentAction commentAction, BleatDisplay bleatDisplay) {
        this.bleatDisplay = bleatDisplay;
        this.commentAction = commentAction;
    }

    protected List<Comment> doInBackground(Void... Params) {
        return commentAction.getComments();
    }

    protected void onPostExecute(List<Comment> comments) {
        if (bleatDisplay == null) {
            DataStore.getInstance().updateComments(comments);
            return;
        }
        for (Comment comment : comments) {
            bleatDisplay.addView(comment);
        }
    }
}
