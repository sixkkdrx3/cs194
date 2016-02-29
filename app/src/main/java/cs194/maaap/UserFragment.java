package cs194.maaap;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserFragment extends Fragment {

    private MainActivity parentActivity;
    private String id;
    private List<Bleat> bleats = null;

    private List<Bleat> getOwnBleats() {
        List<Bleat> result = new ArrayList<Bleat>();
        for (Bleat bleat : bleats) {
            if (bleat.getAuthorID() == null) continue;
            if (bleat.getAuthorID().equals(id)) {
                result.add(bleat);
            }
        }
        return result;
    }

    private List<Bleat> getVotedBleats() {
        List<Bleat> result = new ArrayList<Bleat>();
        for (Bleat bleat : bleats) {
            if (bleat.getUpvotes().contains(id) || bleat.getDownvotes().contains(id)) {
                result.add(bleat);
            }
        }
        return result;
    }

    private List<Bleat> getCommentedBleats() {
        HashSet<String> commentedBIDs = new HashSet<String>();
        List<Comment> comments = null;
        CommentAction commentAction = new CommentAction(parentActivity, Constants.DEFAULT_BLAH);
        GetComments getComments = new GetComments(commentAction);
        try { comments = getComments.execute().get(); } catch (Exception e) { }
        for (Comment comment : comments) {
            if (comment.getAuthorID() == null) continue;
            if (comment.getAuthorID().equals(id)) {
                commentedBIDs.add(comment.getBID());
            }
        }

        List<Bleat> result = new ArrayList<Bleat>();
        for (Bleat bleat : bleats) {
            if (commentedBIDs.contains(bleat.getBID())) {
                result.add(bleat);
            }
        }
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        Button button1 = (Button)v.findViewById(R.id.button);
        Button button2 = (Button)v.findViewById(R.id.button2);
        Button button3 = (Button)v.findViewById(R.id.button3);

        parentActivity = (MainActivity)getActivity();
        id = Settings.Secure.getString(parentActivity.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        BleatAction bleatAction = new BleatAction(parentActivity, "Multi");
        GetBleats getBleats = new GetBleats(bleatAction);
        try { bleats = getBleats.execute().get(); } catch (Exception e) { }

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                List<Bleat> bleats = getOwnBleats();
                intent.putExtra("myBleats", bleats.toArray(new Bleat[bleats.size()]));
                parentActivity.startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                List<Bleat> bleats = getVotedBleats();
                intent.putExtra("myBleats", bleats.toArray(new Bleat[bleats.size()]));
                parentActivity.startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(parentActivity, MultiBleatDisplay.class);
                List<Bleat> bleats = getCommentedBleats();
                intent.putExtra("myBleats", bleats.toArray(new Bleat[bleats.size()]));
                parentActivity.startActivity(intent);
            }
        });
        return v;
    }
}