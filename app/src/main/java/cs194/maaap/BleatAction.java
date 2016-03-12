package cs194.maaap;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import android.app.Activity;
import android.content.Context;

import java.io.File;
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
    private Activity activity;
    private TransferUtility transferUtility;

    public BleatAction(Activity activity, String activityType) {
        this.activity = activity;
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                activity.getApplicationContext(),
                "us-east-1:3d4f8ee4-167c-4a7e-922d-8841869a6725", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        if (activityType.equals("MapsActivity"))
            coords = ((MapFragment)(((MainActivity)activity).adapter.getItem(0))).getGPS();
        else if (activityType.equals("BleatCreateActivity"))
        {
            coords = ((BleatCreateActivity) activity).coords;
        }

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        transferUtility = new TransferUtility(s3, activity.getApplicationContext());
    }

    public void saveBleat(String message) {
        String bid = UUID.randomUUID().toString();
        String id = Secure.getString(activity.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        Bleat bleat = new Bleat();
        bleat.setMessage(message);
        bleat.setBID(bid);
        bleat.setCoordinates(coords[0], coords[1]);
        bleat.setTime(Calendar.getInstance().getTimeInMillis());
        bleat.setAuthorID(id);
        DataStore.getInstance().updateBleats(bleat);
        mapper.save(bleat);
    }

    public void saveBleatWithPhoto(String message, String photoID) {
        String bid = UUID.randomUUID().toString();
        String id = Secure.getString(activity.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        Bleat bleat = new Bleat();
        bleat.setMessage(message);
        bleat.setBID(bid);
        bleat.setCoordinates(coords[0], coords[1]);
        bleat.setTime(Calendar.getInstance().getTimeInMillis());
        bleat.setAuthorID(id);
        bleat.setPhotoID(photoID);
        DataStore.getInstance().updateBleats(bleat);
        mapper.save(bleat);
    }



    public void upvoteBleat(Bleat bleat) {
        String id = Secure.getString(activity.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        HashSet<String> upVotes = bleat.getUpvotes();
        HashSet<String> downVotes = bleat.getDownvotes();

        if (upVotes.contains(id)) { // un-do upvote
            upVotes.remove(id);
            bleat.setUpvotes(upVotes);
            DataStore.getInstance().updateBleats(bleat);
            mapper.save(bleat);
            return;
        }
        if (downVotes.contains(id)) {
            downVotes.remove(id);
            bleat.setDownvotes(downVotes);
        }
        upVotes.add(id);
        bleat.setUpvotes(upVotes);
        DataStore.getInstance().updateBleats(bleat);
        mapper.save(bleat);
    }

    public void downvoteBleat(Bleat bleat) {
        String id = Secure.getString(activity.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);
        
        HashSet<String> upVotes = bleat.getUpvotes();
        HashSet<String> downVotes = bleat.getDownvotes();

        if (downVotes.contains(id)) { // un-do downvote
            downVotes.remove(id);
            bleat.setDownvotes(downVotes);
            DataStore.getInstance().updateBleats(bleat);
            mapper.save(bleat);
            return;
        }
        if (upVotes.contains(id)) {
            upVotes.remove(id);
            bleat.setUpvotes(upVotes);
        }
        downVotes.add(id);
        bleat.setDownvotes(downVotes);
        DataStore.getInstance().updateBleats(bleat);
        mapper.save(bleat);
    }

    public List<Bleat> getBleats() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Bleat> result = mapper.scan(Bleat.class, scanExpression);
        return result;
    }

    public void uploadPhoto(File file) {
        String id = file.getName();
        TransferObserver observer = transferUtility.upload(
                "cs194",
                id,
                file
        );
    }

    public File downloadPhoto(String id) {
        String filename = id;
        File file = new File(activity.getCacheDir(), filename);
        TransferObserver observer = transferUtility.download(
                "cs194",
                id,
                file
        );
        return file;
    }
}
