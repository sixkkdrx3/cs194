package cs194.maaap;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;
import android.content.Context;

import java.util.Calendar;
import java.util.UUID;
import java.util.List;
import java.util.HashSet;
import android.provider.Settings.Secure;

/**
 * Created by kaidi on 1/24/16.
 */
public class BleatAction {

    private DynamoDBMapper mapper;
    double coords[];
    private MapsActivity activity;

    public BleatAction(MapsActivity activity) {
        this.activity = activity;
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                activity.getApplicationContext(),
                "us-east-1:3d4f8ee4-167c-4a7e-922d-8841869a6725", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        coords = activity.getGPS();
    }

    public void saveBleat(String message) {
        String bid = UUID.randomUUID().toString();

        Bleat bleat = new Bleat();
        bleat.setMessage(message);
        bleat.setBID(bid);
        bleat.setCoordinates(coords[0], coords[1]);
        bleat.setTime(Calendar.getInstance().getTimeInMillis());
        mapper.save(bleat);
    }



    public void upvoteBleat(Bleat bleat) {
        String id = Secure.getString(activity.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        HashSet<String> upVotes = bleat.getUpvotes();
        upVotes.add(id);

        if (bleat.getDownvotes().contains(id)) {
            HashSet<String> downVotes = bleat.getDownvotes();
            downVotes.remove(id);
            bleat.setDownvotes(downVotes);
        }
        bleat.setUpvotes(upVotes);
        mapper.save(bleat);
    }

    public void downvoteBleat(Bleat bleat) {
        String id = Secure.getString(activity.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        HashSet<String> downVotes = bleat.getDownvotes();
        downVotes.add(id);

        if (bleat.getUpvotes().contains(id)) {
            HashSet<String> upVotes = bleat.getUpvotes();
            upVotes.remove(id);
            bleat.setUpvotes(upVotes);
        }
        bleat.setDownvotes(downVotes);
        mapper.save(bleat);
    }

    public List<Bleat> getBleats() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Bleat> result = mapper.scan(Bleat.class, scanExpression);
        return result;
    }
}
