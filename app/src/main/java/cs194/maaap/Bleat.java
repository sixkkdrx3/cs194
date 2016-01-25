package cs194.maaap;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

/**
 * Created by kaidi on 1/24/16.
 */

@DynamoDBTable(tableName = "MaaapBleats")
public class Bleat {
    private String bid;
    private String message;

    @DynamoDBHashKey(attributeName = "BID")
    public String getBID() {
        return bid;
    }

    public void setBID(String bid) {
        this.bid = bid;
    }

    @DynamoDBAttribute(attributeName = "Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
