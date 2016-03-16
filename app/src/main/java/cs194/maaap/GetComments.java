package cs194.maaap;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

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
        Log.d("GetComments", "downloading comments");
        List<Comment> result = commentAction.getComments();
        Log.d("GetComments", "comments downloaded");
        return result;
    }

    protected void onPostExecute(List<Comment> comments) {
        DataStore store = DataStore.getInstance();
        Log.d("GetComments", "comments downloaded onPostExecute");
        synchronized(store) {
            store.updateComments(comments);
            store.commentsDownloaded = true;
            store.notifyAll();
        }
        if (bleatDisplay != null) {
            for (Comment comment : comments) {
                bleatDisplay.addView(comment);
            }
        }
    }
}
