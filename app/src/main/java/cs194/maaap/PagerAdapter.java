package cs194.maaap;

/**
 * Created by SCQ on 2/28/2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    MapFragment tab1;
    UserFragment tab2;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.tab1 = null;
        this.tab2 = null;
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (tab1 == null) tab1 = new MapFragment();
                return tab1;
            case 1:
                if (tab2 == null) tab2 = new UserFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
