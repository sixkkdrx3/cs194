package cs194.maaap;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kaidi on 2/28/16.
 */

public class MultiBleatsDisplaySorted extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multibleats_display_sorted);

        Intent intent = getIntent();
        Bleat[] bleats = (Bleat[])intent.getSerializableExtra("myBleats");

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");

        tab1.setIndicator("New");
        tab1.setContent(new Intent(this, MultiBleatDisplay.class)
                .putExtra("order", "new")
                .putExtra("myBleats", bleats));


        tab2.setIndicator("Hot");
        tab2.setContent(new Intent(this, MultiBleatDisplay.class)
                .putExtra("order", "hot")
                .putExtra("myBleats", bleats));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }
}
