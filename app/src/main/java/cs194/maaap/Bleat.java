package cs194.maaap;

import android.app.Activity;
import android.provider.Settings;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

@DynamoDBTable(tableName = "MaaapBleats")
public class Bleat implements Serializable {
    private String bid;
    private String message;
    private double latitude, longitude;
    private long time;
    private HashSet<String> upvotes;
    private HashSet<String> downvotes;
    private HashMap<String, String> reports;
    private String authorID;
    private String photoID;

    public Bleat() {
        upvotes = new HashSet<String>();
        downvotes = new HashSet<String>();
        reports = new HashMap<String,String>();
        upvotes.add(Constants.DEFAULT_BLAH);
        downvotes.add(Constants.DEFAULT_BLAH);
        reports.put(Constants.DEFAULT_BLAH, Constants.DEFAULT_BLAH);
        photoID = "";
    }

    @DynamoDBHashKey(attributeName = "BID")
    public String getBID() {
        return bid;
    }

    public void setBID(String bid) {
        this.bid = bid;
    }

    @DynamoDBAttribute(attributeName = "Message")
    public String getMessage() {
        if(message == null)
            return "";
        else
            return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDBAttribute(attributeName = "AuthorID")
    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String id) {
        authorID = id;
    }

    @DynamoDBAttribute(attributeName = "Longitude")
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @DynamoDBAttribute(attributeName = "Latitude")
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @DynamoDBAttribute(attributeName = "PhotoID")
    public String getPhotoID() {
        return photoID;
    }
    public void setPhotoID(String photoID) { this.photoID = photoID; }

    public void setCoordinates(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public boolean isUpvoted(Activity activity)
    {
        String id = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(this.getUpvotes().contains(id))
            return true;
        else
            return false;
    }

    public boolean isDownvoted(Activity activity)
    {
        String id = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(this.getDownvotes().contains(id))
            return true;
        else
            return false;
    }

    @DynamoDBAttribute(attributeName = "Time")
    public long getTime() {return time;}
    public void setTime(long time) {this.time = time;}

    @DynamoDBAttribute(attributeName = "Upvotes")
    public HashSet<String> getUpvotes() { return upvotes; }
    public void setUpvotes(HashSet<String> upvotes) {this.upvotes = upvotes; }

    @DynamoDBAttribute(attributeName = "Downvotes")
    public HashSet<String> getDownvotes() { return downvotes; }
    public void setDownvotes(HashSet<String> downvotes) {this.downvotes = downvotes; }

    public int computeNetUpvotes() {
        return getUpvotes().size()-getDownvotes().size();
    }

    @DynamoDBAttribute(attributeName = "Reports")
    public HashMap<String, String> getReports() { return reports; }
    public void setReports(HashMap<String, String> reports) {this.reports = reports; }

    public boolean gt(Bleat other)
    {
        return (computeNetUpvotes() > other.computeNetUpvotes() ||
                ((computeNetUpvotes() == other.computeNetUpvotes()) && (getTime() > other.getTime())));
    }
}
