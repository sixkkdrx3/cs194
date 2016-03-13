package cs194.maaap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jiahan on 3/11/16.
 */
public class Achievement {
    public int resid;
    public String name;
    public String description;
    public int threshold = 0;
    public Achievement(int resid, String name, String description, int threshold) {
        this.resid = resid;
        this.name = name;
        this.description = description;
        this.threshold = threshold;
    }

    public static int getThreshold(int[] thresholds, int target) {
        for (int i = 1; i < thresholds.length; i++) {
            if (thresholds[i] > target) {
                return thresholds[i - 1];
            }
        }
        return thresholds[thresholds.length - 1];
    }

    public static Achievement[] getAchievements() {
        ArrayList<Achievement> achievements = new ArrayList<Achievement>();


        int num_bleats = 50;
        if(num_bleats > 0) { //check achievement for number of bleats
            int[] thresholds = {1, 5, 10, 25, 50, 100, 200, 500, 1000};
            int threshold = getThreshold(thresholds, num_bleats);
            achievements.add(new Achievement(R.drawable.sheep,"Bleat on!", threshold == 1 ? "Posted a bleat" : threshold + " bleat club",threshold));
        }

        int num_photos = 10;
        if(num_photos > 0) { //check achievement for number of photos
            int[] thresholds = {1, 5, 10, 25, 50, 100, 200, 500, 1000};
            int threshold = getThreshold(thresholds, num_photos);
            achievements.add(new Achievement(R.drawable.camera_retro,"Photographer",threshold == 1 ? "Posted a photo" :threshold + " photo club",threshold));
        }

        int num_comments = 100;
        if(num_comments > 0) { //check achievement for number of comments
            int[] thresholds = {1, 5, 10, 25, 50, 100, 200, 500, 1000};
            int threshold = getThreshold(thresholds, num_comments);
            achievements.add(new Achievement(R.drawable.comments,"Chit Chat Crew",threshold == 1 ? "Posted a comment" :threshold + " comment club",threshold));
        }


        int distance = 1000;
        if(true) { //world traveller
            int[] thresholds = {500, 1000, 2000, 5000, 7500, 10000};
            int threshold = getThreshold(thresholds, distance);
            achievements.add(new Achievement(R.drawable.globe, "Globetrotter", "Bleated " + distance + " miles away", threshold));
        }

        long time = 3600L*24L*60L;
        if(time > 3600L*24L*7) { //veteran
            int[] thresholds = {3600*24*7, 3600*24*14, 3600*24*30, 3600*24*30*2, 3600*24*30*3, 3600*24*182, 3600*24*365};
            String[] timeNames = {"1 week", "2 week", "1 month", "2 month", "3 month", "6 month"};
            String[] trophyNames = {"Newbie", "Newbie", "Veteran", "Veteran", "Veteran", "Veteran"};
            int[] resids = {R.drawable.child, R.drawable.child, R.drawable.birthday_cake, R.drawable.birthday_cake, R.drawable.birthday_cake, R.drawable.birthday_cake};
            int threshold = -1;
            String timeName = null;
            String trophyName = null;
            int resid = 0;
            for (int i = 1; i < thresholds.length; i++) {
                if (thresholds[i] > time) {
                    threshold = thresholds[i - 1];
                    timeName = timeNames[i-1];
                    trophyName = trophyNames[i-1];
                    resid = resids[i-1];
                }
            }
            if(threshold == -1) {
                timeName = (time/3600/24/365) + " year";
                trophyName = "Elder";
                resid = R.drawable.birthday_cake;
            }
            achievements.add(new Achievement(resid, trophyName, timeName + " club", 0));
        }
        return achievements.toArray(new Achievement[achievements.size()]);
    }
}
