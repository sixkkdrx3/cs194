package cs194.maaap;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import java.util.HashSet;

@DynamoDBTable(tableName = "MaaapBleats")
public class Bleat {
    private String bid;
    private String message;
    private double latitude, longitude;
    private long time;
    private HashSet<String> upvotes, downvotes;

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

    public void setCoordinates(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
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
}
