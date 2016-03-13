package cs194.maaap;

import android.app.Activity;
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

    public Comment saveComment(String message) {
        String cid = UUID.randomUUID().toString();
        String id = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setCID(cid);
        comment.setBID(bid);
        comment.setTime(Calendar.getInstance().getTimeInMillis());
        comment.setAuthorID(id);
        DataStore.getInstance().updateComments(comment);
        mapper.save(comment);

        return comment;
    }

    public boolean upvoteComment(Comment comment) {
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
    }

    public boolean downvoteComment(Comment comment) {
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
    }

    public List<Comment> getComments() {
        PaginatedScanList<Comment> result = null;
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            result = mapper.scan(Comment.class, scanExpression);
        } catch (Throwable t) {
            Log.d("error", "ERROR IN COMMENT");
        }
        return result;
    }
}

