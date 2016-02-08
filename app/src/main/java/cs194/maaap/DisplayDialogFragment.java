package cs194.maaap;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

public class DisplayDialogFragment extends DialogFragment {
    private Bleat bleat;

    public DisplayDialogFragment(Bleat bleat) {
        this.bleat=bleat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.output_dialog_fragment, container, false);
        TextView message = (TextView)v.findViewById(R.id.msg);
        message.setText(bleat.getMessage());
        // Watch for button clicks.
        final Button up = (Button)v.findViewById(R.id.up);
        final Button down = (Button)v.findViewById(R.id.down);
        up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BleatAction bleatAction = new BleatAction(((MapsActivity)getActivity()));
                UpvoteBleat upvoteBleat = new UpvoteBleat(bleatAction);
                try {
                    upvoteBleat.execute(bleat).get();
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                };
                up.setText("Upvotes : " + Integer.toString(bleat.getUpvotes().size() - 1));
                down.setText("Downvotes : " + Integer.toString(bleat.getDownvotes().size() - 1));
            }
        });
        down.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                BleatAction bleatAction = new BleatAction(((MapsActivity)getActivity()));
                DownvoteBleat downvoteBleat = new DownvoteBleat(bleatAction);
                try {
                    downvoteBleat.execute(bleat).get();
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
                up.setText("Upvotes : " + Integer.toString(bleat.getUpvotes().size() - 1));
                down.setText("Downvotes : " + Integer.toString(bleat.getDownvotes().size() - 1));
            }
        });

        return v;
    }
}
