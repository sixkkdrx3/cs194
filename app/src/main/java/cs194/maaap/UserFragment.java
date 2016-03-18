package cs194.maaap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserFragment extends Fragment {

    private MainActivity parentActivity;
    private String id;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_user, container, false);

        parentActivity = (MainActivity)getActivity();
        id = Settings.Secure.getString(parentActivity.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final LinearLayout trophyCabinet = (LinearLayout) v.findViewById(R.id.trophy_cabinet);


        try {
            BleatAction bleatAction = new BleatAction(parentActivity, "Multi");
            GetBleats getBleats = new GetBleats(bleatAction);
            Log.d("UserFragment", "executing getBleats");
            getBleats.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) { }

        try {
            CommentAction commentAction = new CommentAction(parentActivity, Constants.DEFAULT_BLAH);
            GetComments getComments = new GetComments(commentAction, null);
            Log.d("UserFragment", "executing getComments");
            getComments.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) { }

        AsyncTask<Void, Void, Achievement[]> setAchievements = new AsyncTask<Void, Void, Achievement[]>() {

            private int reputation = 0;

            @Override
            protected Achievement[] doInBackground(Void... Params) {
                Log.d("UserFragment", "Retrieving achievements");
                DataStore store = DataStore.getInstance();
                synchronized(store) {
                    while(!store.bleatsDownloaded) {
                        Log.d("Achievement", "waiting for bleats to download");
                        try {
                            store.wait();
                        } catch(InterruptedException e) {}
                    }
                }
                synchronized(store) {
                    while(!store.commentsDownloaded) {
                        Log.d("Achievement", "waiting for comments to download");
                        try {
                            store.wait();
                        } catch(InterruptedException e) {}
                    }
                }

                List<Bleat> bleats = store.getOwnBleats(id);
                List<Comment> comments = store.getOwnComments(id);
                Achievement[] achievements = Achievement.getAchievements(id, bleats, comments);
                for(Bleat bleat : bleats) {
                    reputation += bleat.computeNetUpvotes();
                }
                for(Comment comment : comments) {
                    reputation += comment.computeNetUpvotes();
                }
                Log.d("UserFragment", "Achievements retrieved");
                return achievements;
            }

            @Override
            protected void onPostExecute(Achievement[] achievements) {
                Log.d("UserFragment", "Drawing achievements");
                int num_rows = (achievements.length + 3 - 1) / 3;
                LinearLayout[] trophyRows = new LinearLayout[num_rows];
                for (int i = 0; i < num_rows; i++) {
                    trophyRows[i] = (LinearLayout) inflater.inflate(R.layout.trophy_row, trophyCabinet, false);
                    trophyCabinet.addView(trophyRows[i]);
                    for (int j = i * 3; j < (i + 1) * 3 && j < achievements.length; j++) {
                        LinearLayout trophy = (LinearLayout) inflater.inflate(R.layout.trophy_elem, trophyRows[i], false);
                        trophyRows[i].addView(trophy);
                        ImageView trophyImage = (ImageView) trophy.findViewById(R.id.trophy_image);
                        TextView trophyName = (TextView) trophy.findViewById(R.id.trophy_name);
                        TextView trophyDescription = (TextView) trophy.findViewById(R.id.trophy_description);
                        trophyImage.setImageResource(achievements[j].resid);
                        trophyName.setText(achievements[j].name);
                        trophyDescription.setText(achievements[j].description);
                        Log.d("User Fragment", achievements[j].name);
                    }
                }
                Log.d("UserFragment", "Achievements Drawn");

                TextView reputationView = (TextView) v.findViewById(R.id.reputation);
                reputationView.setText(Integer.toString(reputation));
                Log.d("reputation", "reputation=" + reputation);
            }
        };

        setAchievements.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        final LinearLayout button1 = (LinearLayout)v.findViewById(R.id.button1);
        final LinearLayout button2 = (LinearLayout)v.findViewById(R.id.button2);
        final LinearLayout button3 = (LinearLayout)v.findViewById(R.id.button3);


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

        /*button1.setTransformationMethod(null);
        button2.setTransformationMethod(null);
        button3.setTransformationMethod(null);*/
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                intent.putExtra("title","My Bleats");
                intent.putExtra("myBIDs", Utils.extractBIDs(DataStore.getInstance().getOwnBleats(id)));
                parentActivity.startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                intent.putExtra("title","Bleats I Liked");
                intent.putExtra("myBIDs", Utils.extractBIDs(DataStore.getInstance().getVotedBleats(id)));
                parentActivity.startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                intent.putExtra("title","Bleats I Commented On");
                intent.putExtra("myBIDs", Utils.extractBIDs(DataStore.getInstance().getCommentedBleats(id)));
                parentActivity.startActivity(intent);
            }
        });
        return v;
    }
}