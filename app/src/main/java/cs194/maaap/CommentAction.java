package cs194.maaap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by kaidi on 2/21/16.
 */
public class CommentAction {

    private DynamoDBMapper mapper;
    double coords[];
    private Activity activity;
    private String bid;

    public CommentAction(Activity activity, String bid) {
        this.activity = activity;
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                activity.getApplicationContext(),
                "us-east-1:3d4f8ee4-167c-4a7e-922d-8841869a6725", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        this.bid = bid;
    }

    public void handleError() {
        Log.d("error", "error!!");
        Intent intent = new Intent(activity.getApplicationContext(), Start.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("ExitMe", true);
        activity.startActivity(intent);
    }

    public void checkOnlineState() {
        ConnectivityManager cm =
                (ConnectivityManager)activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()) return;
        handleError();
    }

    public Comment saveComment(String message) {
        checkOnlineState();
        Comment comment = new Comment();
        try {
            String cid = UUID.randomUUID().toString();
            String id = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            comment.setMessage(message);
            comment.setCID(cid);
            comment.setBID(bid);
            comment.setTime(Calendar.getInstance().getTimeInMillis());
            comment.setAuthorID(id);
            DataStore.getInstance().updateComments(comment);
            mapper.save(comment);
        } catch (Exception e) {
            handleError();
        }
        return comment;
    }

    public boolean upvoteComment(Comment comment) {
        checkOnlineState();
        try {
            String id = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            HashSet<String> upVotes = comment.getUpvotes();
            HashSet<String> downVotes = comment.getDownvotes();

            if (upVotes.contains(id)) { // un-do upvote
                upVotes.remove(id);
                comment.setUpvotes(upVotes);
                DataStore.getInstance().updateComments(comment);
                mapper.save(comment);
                return true;
            }
            if (downVotes.contains(id)) {
                downVotes.remove(id);
                comment.setDownvotes(downVotes);
            }
            upVotes.add(id);
            comment.setUpvotes(upVotes);
            DataStore.getInstance().updateComments(comment);
            mapper.save(comment);
            return false;
        } catch (Exception e) {
            handleError();
            return false;
        }
    }

    public boolean downvoteComment(Comment comment) {
        checkOnlineState();
        try {
            String id = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            HashSet<String> upVotes = comment.getUpvotes();
            HashSet<String> downVotes = comment.getDownvotes();

            if (downVotes.contains(id)) { // un-do downvote
                downVotes.remove(id);
                comment.setDownvotes(downVotes);
                DataStore.getInstance().updateComments(comment);
                mapper.save(comment);
                return true;
            }
            if (upVotes.contains(id)) {
                upVotes.remove(id);
                comment.setUpvotes(upVotes);
            }
            downVotes.add(id);
            comment.setDownvotes(downVotes);
            DataStore.getInstance().updateComments(comment);
            mapper.save(comment);
            return false;
        } catch (Exception e) {
            handleError();
            return false;
        }
    }

    public List<Comment> getComments() {
        checkOnlineState();
        PaginatedScanList<Comment> result = null;
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            result = mapper.scan(Comment.class, scanExpression);
        } catch (Exception e) {
            handleError();
        }
        return result;
    }
}

