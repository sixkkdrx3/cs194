package cs194.maaap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaidi on 3/7/16.
 */
public class Utils {
    static public List<String> extractBIDs(List<Bleat> bleats) {
        List<String> BIDs = new ArrayList<String>();
        for (Bleat bleat : bleats) BIDs.add(bleat.getBID());
        return BIDs;
    }
}
