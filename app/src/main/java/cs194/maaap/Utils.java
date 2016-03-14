package cs194.maaap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

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

    private static final String PREFERENCES_FILE = "materialsample_settings";


    public static int getToolbarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.statusbar_size);
        return height;
    }


    public static Drawable tintMyDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }


    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }
}
