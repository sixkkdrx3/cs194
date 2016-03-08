package cs194.maaap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserFragment extends Fragment {

    private MainActivity parentActivity;
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        final TextView button1 = (TextView)v.findViewById(R.id.button1);
        final TextView button2 = (TextView)v.findViewById(R.id.button2);
        final TextView button3 = (TextView)v.findViewById(R.id.button3);

        parentActivity = (MainActivity)getActivity();
        id = Settings.Secure.getString(parentActivity.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            BleatAction bleatAction = new BleatAction(parentActivity, "Multi");
            GetBleats getBleats = new GetBleats(bleatAction);
            DataStore.getInstance().updateBleats(getBleats.execute().get());
        } catch (Exception e) { }

        try {
            CommentAction commentAction = new CommentAction(parentActivity, Constants.DEFAULT_BLAH);
            GetComments getComments = new GetComments(commentAction);
            DataStore.getInstance().updateComments(getComments.execute().get());
        } catch (Exception e) { }

    /*    List<Bleat> ownBleats = DataStore.getInstance().getOwnBleats(id);
        if (ownBleats.size() != 0){
            TextView tv1 = (TextView)v.findViewById(R.id.content_latest_bleat);
            tv1.setText(ownBleats.get(0).getMessage());
        }

        List<Bleat> voted = DataStore.getInstance().getVotedBleats(id);
        if (voted.size() != 0){
            TextView tv2 = (TextView)v.findViewById(R.id.content_upvoted_bleat);
            tv2.setText(voted.get(0).getMessage());
        }

        List<Bleat> commented = DataStore.getInstance().getCommentedBleats(id);
        if (commented.size() != 0){
            TextView tv3 = (TextView)v.findViewById(R.id.content_commented_bleat);
            tv3.setText(commented.get(0).getMessage());
        }*/

        button1.setTransformationMethod(null);
        button2.setTransformationMethod(null);
        button3.setTransformationMethod(null);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                intent.putExtra("title","Your bleats");
                intent.putExtra("myBIDs", Utils.extractBIDs(DataStore.getInstance().getOwnBleats(id)));
                parentActivity.startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                intent.putExtra("title","Your liked bleats");
                intent.putExtra("myBIDs", Utils.extractBIDs(DataStore.getInstance().getVotedBleats(id)));
                parentActivity.startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                intent.putExtra("title","Your commented bleats");
                intent.putExtra("myBIDs", Utils.extractBIDs(DataStore.getInstance().getCommentedBleats(id)));
                parentActivity.startActivity(intent);
            }
        });
        return v;
    }
}