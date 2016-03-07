package cs194.maaap;

import android.provider.ContactsContract;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SCQ on 3/6/2016.
 */
public class DataStore {
    private HashMap<String, Bleat> downloadedBleats;
    private HashMap<String, Comment> downloadedComments;

    public long bleatLastUpdated;
    public long commentLastUpdated;

    public DataStore() {
        bleatLastUpdated = commentLastUpdated = 0;
    }

    public HashMap<String, Bleat> getDownloadedBleats() {return downloadedBleats;}
    public HashMap<String, Comment> getDownloadedComments() {return downloadedComments;}

    public Bleat getBleat(String BID) {return downloadedBleats.get(BID);}
    public Comment getComment(String CID) {return downloadedComments.get(CID);}

    public void updateBleats(Bleat bleat) {
        downloadedBleats.put(bleat.getBID(), bleat);
        bleatLastUpdated = Calendar.getInstance().getTimeInMillis();
    }
    public void updateBleats(List<Bleat> bleats) {
        for (Bleat bleat : bleats)
            downloadedBleats.put(bleat.getBID(), bleat);
        bleatLastUpdated = Calendar.getInstance().getTimeInMillis();
    }

    public void updateComments(Comment comment) {
        downloadedComments.put(comment.getCID(), comment);
        commentLastUpdated = Calendar.getInstance().getTimeInMillis();
    }
    public void updateComments(Comment[] comments) {
        for (Comment comment : comments)
            downloadedComments.put(comment.getCID(), comment);
        commentLastUpdated = Calendar.getInstance().getTimeInMillis();
    }

    private static final DataStore holder = new DataStore();
    public static DataStore getInstance() {return holder;}
}
