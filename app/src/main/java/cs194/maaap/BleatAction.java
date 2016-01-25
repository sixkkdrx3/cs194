package cs194.maaap;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;
import android.content.Context;
import java.util.UUID;

/**
 * Created by kaidi on 1/24/16.
 */
public class BleatAction {

    private DynamoDBMapper mapper;

    public BleatAction(Context context) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:3d4f8ee4-167c-4a7e-922d-8841869a6725", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
    }

    public void saveBleat(String message) {
        String bid = UUID.randomUUID().toString();

        Bleat bleat = new Bleat();
        bleat.setMessage(message);
        bleat.setBID(bid);

        mapper.save(bleat);
    }
}
