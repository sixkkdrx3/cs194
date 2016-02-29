package cs194.maaap;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import java.util.List;

/**
 * Created by kaidi on 2/28/16.
 */
public class MaaapActivity extends TabActivity {
    private TabHost tabHost;

    private void setUpTabHost() {
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maaap);

        setUpTabHost();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");

        tab1.setIndicator("Map");
        tab1.setContent(new Intent(this, MapsActivity.class));


        tab2.setIndicator("Me");
        BleatAction bleatAction = new BleatAction(this, "maaap");
        GetBleats getBleats = new GetBleats(bleatAction);
        List<Bleat> bleats = null;
        try { bleats = getBleats.execute().get(); } catch (Exception e) {}
        tab2.setContent(new Intent(this, MultiBleatsDisplaySorted.class).
                putExtra("myBleats", bleats.toArray(new Bleat[bleats.size()])));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }
}

