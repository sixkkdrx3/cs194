package cs194.maaap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaidi on 3/7/16.
 */
public class Utils {
    static public String[] extractBIDs(List<Bleat> bleats) {
        String[] BIDs = new String[bleats.size()];
        int cnt = 0;
        for (Bleat bleat : bleats) BIDs[cnt++] = bleat.getBID();
        return BIDs;
    }

    static public String[] extractBIDs(Bleat[] bleats) {
        String[] BIDs = new String[bleats.length];
        int cnt = 0;
        for (Bleat bleat : bleats) BIDs[cnt++] = bleat.getBID();
        return BIDs;
    }
}
