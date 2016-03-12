package cs194.maaap;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class MultiBleatDisplay extends Activity {

    private int maxPhotoWidth = 10000;
    private int maxPhotoHeight = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multibleat_display);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("title");
        TextView tv = (TextView)findViewById(R.id.multi_title);
        tv.setText(msg);
        String[] myBIDs = (String[])intent.getSerializableExtra("myBIDs");

        ArrayList<Bleat> bleats = new ArrayList<Bleat>();
        for (String bid : myBIDs) bleats.add(DataStore.getInstance().getBleat(bid));

        LinearLayout bleatLayout = (LinearLayout) findViewById(R.id.bleat_scroll_layout);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (final Bleat bleat : bleats) {
            View view = inflater.inflate(R.layout.bleat_item, null);

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Intent displayIntent;
                    Log.d("val", "valerie!");
                    displayIntent = new Intent(MultiBleatDisplay.this, BleatDisplay.class);
                    displayIntent.putExtra("myBID", bleat.getBID());
                    MultiBleatDisplay.this.startActivity(displayIntent);
                }
            });


            final ImageView up = (ImageView) view.findViewById(R.id.up);
            final ImageView down = (ImageView) view.findViewById(R.id.down);
            final TextView number = (TextView) view.findViewById(R.id.num);
            int num = bleat.getUpvotes().size() - bleat.getDownvotes().size();
            final BleatAction bleatAction = new BleatAction(this, "BleatDisplay");
            number.setText(Integer.toString(num));

            up.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    UpvoteBleat upvoteBleat = new UpvoteBleat(bleatAction, number);
                    try {
                        upvoteBleat.execute(bleat);
                    } catch (Exception e) {
                        Log.d("map", "ggwp");
                    }
                }
            });
            down.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DownvoteBleat downvoteBleat = new DownvoteBleat(bleatAction, number);
                    try {
                        downvoteBleat.execute(bleat);
                    } catch (Exception e) {
                        Log.d("map", "ggwp");
                    }
                }
            });

            bleatLayout.addView(view);
            TextView bleatText = (TextView)view.findViewById(R.id.bleat_item_text);
            if(bleat.getMessage().length() < 200) {
                bleatText.setText(bleat.getMessage());
            }
            else
            {
                ViewGroup parent = (ViewGroup) bleatText.getParent();
                int index = parent.indexOfChild(bleatText);
                parent.removeView(bleatText);
                ImageView msgPhoto = (ImageView) getLayoutInflater().inflate(R.layout.bleatlist_photo, parent, false);
                parent.addView(msgPhoto, index);
                byte[] decodedByte = Base64.decode(bleat.getMessage(), 0);
                Bitmap fullBitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                Pair<Integer, Integer> size = MapFragment.scalePreserveRatio(fullBitmap.getWidth(), fullBitmap.getHeight(), maxPhotoWidth, maxPhotoHeight);
                Bitmap bitmap = fullBitmap.createScaledBitmap(fullBitmap, size.first, size.second, true);
                msgPhoto.setImageBitmap(bitmap);
            }

            final TextView voteText = (TextView)view.findViewById(R.id.num);
            voteText.setText(Integer.toString(bleat.computeNetUpvotes()));
        }
    }
}
