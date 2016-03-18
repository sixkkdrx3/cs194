package cs194.maaap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public static Achievement[] getAchievements(String id, List<Bleat> bleats, List<Comment> comments) {
        ArrayList<Achievement> achievements = new ArrayList<Achievement>();

        DataStore store = DataStore.getInstance();

        int num_bleats = bleats.size();
        if(num_bleats > 0) { //check achievement for number of bleats
            int[] thresholds = {1, 5, 10, 25, 50, 100, 200, 500, 1000};
            int threshold = getThreshold(thresholds, num_bleats);
            achievements.add(new Achievement(R.drawable.sheep,"Bleat on!", threshold == 1 ? "Posted a bleat" : threshold + " bleat club",threshold));
        }

        int num_photos = 0;
        for(Bleat bleat : bleats) {
            if(bleat.getPhotoID() != "")
                num_photos++;
        }
        if(num_photos > 0) { //check achievement for number of photos
            int[] thresholds = {1, 5, 10, 25, 50, 100, 200, 500, 1000};
            int threshold = getThreshold(thresholds, num_photos);
            achievements.add(new Achievement(R.drawable.camera_retro,"Photographer",threshold == 1 ? "Posted a photo" :threshold + " photo club",threshold));
        }

        int num_comments = comments.size();
        if(num_comments > 0) { //check achievement for number of comments
            int[] thresholds = {1, 5, 10, 25, 50, 100, 200, 500, 1000};
            int threshold = getThreshold(thresholds, num_comments);
            achievements.add(new Achievement(R.drawable.comments_trophy,"Chit Chat Crew",threshold == 1 ? "Posted a comment" :threshold + " comment club",threshold));
        }


        double max_distance = 0.0;
        for(int i = 0; i < bleats.size(); i++) {
            for(int j = i+1; j < bleats.size(); j++) {
                double lati = bleats.get(i).getLatitude();
                double lngi = bleats.get(i).getLongitude();
                double latj = bleats.get(j).getLatitude();
                double lngj = bleats.get(j).getLongitude();
                Location loc1 = new Location("");
                loc1.setLatitude(lati);
                loc1.setLongitude(lngi);
                Location loc2 = new Location("");
                loc2.setLatitude(latj);
                loc2.setLongitude(lngj);
                double distance = loc1.distanceTo(loc2)/1609.34;
                if(distance > max_distance)
                    max_distance = distance;
            }
        }
        Log.d("Achievement", "Max distance: " + max_distance);
        if(max_distance >= 200) { //world traveller
            int[] thresholds = {200, 500, 1000, 2000, 5000, 7500, 10000};
            int threshold = getThreshold(thresholds, (int) max_distance);
            achievements.add(new Achievement(R.drawable.globe, "Globetrotter", "Bleated " + threshold + " miles away", threshold));
        }

        long current_time = Calendar.getInstance().getTimeInMillis();
        long max_age = 0;
        for(Bleat bleat : bleats) {
            long time = bleat.getTime();
            if(current_time-time > max_age) {
                max_age = current_time-time;
            }
        }
        max_age /= 1000;
        Log.d("Achievement", "Max Age: " + max_age/3600/24 + " days");

        if(max_age > 3600L*24L*7) { //veteran
            int[] thresholds = {3600*24*7, 3600*24*14, 3600*24*30, 3600*24*30*2, 3600*24*30*3, 3600*24*182, 3600*24*365};
            String[] timeNames = {"1 week", "2 week", "1 month", "2 month", "3 month", "6 month"};
            String[] trophyNames = {"Newbie", "Newbie", "Veteran", "Veteran", "Veteran", "Veteran"};
            int[] resids = {R.drawable.child, R.drawable.child, R.drawable.birthday_cake, R.drawable.birthday_cake, R.drawable.birthday_cake, R.drawable.birthday_cake};
            int threshold = -1;
            String timeName = null;
            String trophyName = null;
            int resid = 0;
            for (int i = 1; i < thresholds.length; i++) {
                if (thresholds[i] > max_age) {
                    threshold = thresholds[i - 1];
                    timeName = timeNames[i-1];
                    trophyName = trophyNames[i-1];
                    resid = resids[i-1];
                    break;
                }
            }
            if(threshold == -1) {
                timeName = (max_age/3600/24/365) + " year";
                trophyName = "Elder";
                resid = R.drawable.birthday_cake;
            }
            achievements.add(new Achievement(resid, trophyName, timeName + " club", threshold));
        }

        for(Bleat bleat : bleats) {
            Date date = new Date(bleat.getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour >= 1 && hour <= 4) {
                achievements.add(new Achievement(R.drawable.owl, "Night Owl", "Late night bleats", 0));
                break;
            }
        }

        for(Bleat bleat : bleats) {
            Date date = new Date(bleat.getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour >= 5 && hour <= 7) {
                achievements.add(new Achievement(R.drawable.bird, "Early Bird", "Early morning bleats", 0));
                break;
            }
        }
        return achievements.toArray(new Achievement[achievements.size()]);
    }
}
