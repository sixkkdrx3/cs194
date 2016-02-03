package cs194.maaap;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


@DynamoDBTable(tableName = "MaaapBleats")
public class Bleat {
    private String bid;
    private String message;
    private double latitude, longitude;
    private long time;

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
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLongitude() {
        return longitude;
    }

    @DynamoDBAttribute(attributeName = "Latitude")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setCoordinates(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    @DynamoDBAttribute(attributeName = "Time")
    public long getTime() {return time;}

    public void setTime(long time) {this.time = time;}
}
