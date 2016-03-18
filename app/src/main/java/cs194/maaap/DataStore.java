package cs194.maaap;

import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by SCQ on 3/6/2016.
 */
public class DataStore {
    private HashMap<String, Bleat> downloadedBleats;
    private HashMap<String, Comment> downloadedComments;
    private HashSet<String> seenBleats;

    public long bleatLastUpdated;
    public long commentLastUpdated;
    public boolean bleatsDownloaded;
    public boolean commentsDownloaded;


    public DataStore() {
        downloadedBleats = new HashMap<String, Bleat>();
        downloadedComments = new HashMap<String, Comment>();
        seenBleats = new HashSet<String>();
        bleatLastUpdated = commentLastUpdated = 0;
        bleatsDownloaded = false;
        commentsDownloaded = false;
    }

    public synchronized HashMap<String, Bleat> getDownloadedBleats() {return downloadedBleats;}
    public synchronized HashMap<String, Comment> getDownloadedComments() {return downloadedComments;}
    public synchronized HashSet<String> getSeenBleats() {return seenBleats;}

    public synchronized Bleat getBleat(String BID) {return downloadedBleats.get(BID);}
    public synchronized Bleat[] getBleats(String[] BIDs) {
        Bleat[] bleats = new Bleat[BIDs.length];
        for (int i=0;i<BIDs.length;++i) bleats[i] = getBleat(BIDs[i]);
        return bleats;
    }
    public synchronized Comment getComment(String CID) {return downloadedComments.get(CID);}

    public synchronized void updateBleats(Bleat bleat) {
        if (bleat == null) return;
        downloadedBleats.put(bleat.getBID(), bleat);
        bleatLastUpdated = Calendar.getInstance().getTimeInMillis();
    }
    public synchronized void updateBleats(List<Bleat> bleats) {
        if (bleats == null) return;
        for (Bleat bleat : bleats)
            downloadedBleats.put(bleat.getBID(), bleat);
        bleatLastUpdated = Calendar.getInstance().getTimeInMillis();
    }

    public synchronized void updateComments(Comment comment) {
        if (comment == null) return;
        downloadedComments.put(comment.getCID(), comment);
        commentLastUpdated = Calendar.getInstance().getTimeInMillis();
    }
    public synchronized void updateComments(List<Comment> comments) {
        if (comments == null) return;
        for (Comment comment : comments)
            downloadedComments.put(comment.getCID(), comment);
        Log.d("DataStore", "Number of comments: " + downloadedComments.size());
        commentLastUpdated = Calendar.getInstance().getTimeInMillis();
    }

    public synchronized List<Bleat> getOwnBleats(String id) {
        List<Bleat> result = new ArrayList<Bleat>();
        for (Bleat bleat : downloadedBleats.values()) {
            if (bleat.getAuthorID() == null) continue;
            if (bleat.getAuthorID().equals(id)) {
                result.add(bleat);
            }
        }
        return result;
    }

    public synchronized List<Bleat> getVotedBleats(String id) {
        List<Bleat> result = new ArrayList<Bleat>();
        for (Bleat bleat : downloadedBleats.values()) {
            if (bleat.getUpvotes().contains(id) || bleat.getDownvotes().contains(id)) {
                result.add(bleat);
            }
        }
        return result;
    }

    public synchronized List<Comment> getOwnComments(String id) {

        List<Comment> result = new ArrayList<Comment>();
        for (Comment comment : downloadedComments.values()) {
            if (comment.getAuthorID() == null) continue;
            if (comment.getAuthorID().equals(id)) {
                result.add(comment);
            }
        }

        return result;
    }

    public synchronized List<Bleat> getCommentedBleats(String id) {
        HashSet<String> commentedBIDs = new HashSet<String>();
        for (Comment comment : downloadedComments.values()) {
            if (comment.getAuthorID() == null) continue;
            if (comment.getAuthorID().equals(id)) {
                commentedBIDs.add(comment.getBID());
            }
        }

        List<Bleat> result = new ArrayList<Bleat>();
        for (Bleat bleat : downloadedBleats.values()) {
            if (commentedBIDs.contains(bleat.getBID())) {
                result.add(bleat);
            }
        }
        return result;
    }

    public synchronized void addSeenBleat(String bid) {
        seenBleats.add(bid);
    }

    public synchronized Boolean hasSeenBleat(String bid) {
        return seenBleats.contains(bid);
    }


    private static final DataStore holder = new DataStore();
    public static DataStore getInstance() {return holder;}
}
