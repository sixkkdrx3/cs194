package cs194.maaap;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by SCQ on 2/21/2016.
 */
@DynamoDBTable(tableName = "MaaapComments")
public class Comment implements Serializable {
    private String bid;
    private String cid;
    private String message;
    private long time;
    private HashSet<String> upvotes;
    private HashSet<String> downvotes;
    private String authorID;

    public Comment() {
        upvotes = new HashSet<String>();
        downvotes = new HashSet<String>();
        upvotes.add(Constants.DEFAULT_BLAH);
        downvotes.add(Constants.DEFAULT_BLAH);
    }

    @DynamoDBHashKey(attributeName = "BID")
    public String getBID() {
        return bid;
    }

    public void setBID(String bid) {
        this.bid = bid;
    }

    @DynamoDBHashKey(attributeName = "CID")
    public String getCID() {
        return cid;
    }

    public void setCID(String cid) {
        this.cid = cid;
    }

    @DynamoDBAttribute(attributeName = "AuthorID")
    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String id) { authorID = id; }

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
}
