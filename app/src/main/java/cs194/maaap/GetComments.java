package cs194.maaap;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by kaidi on 2/21/16.
 */

public class GetComments extends AsyncTask<Void, Void, List<Comment> > {

    private CommentAction commentAction;

    public GetComments(CommentAction commentAction) { this.commentAction = commentAction; }

    protected List<Comment> doInBackground(Void... Params) { return commentAction.getComments(); }
}
