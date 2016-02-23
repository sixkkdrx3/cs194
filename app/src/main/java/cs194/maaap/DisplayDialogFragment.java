package cs194.maaap;


import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

public class DisplayDialogFragment extends DialogFragment {
    private Bleat bleat;

    public DisplayDialogFragment(Bleat bleat) {
        if(bleat == null) {
            Log.e("map", "null bleat");
            int bla = 1/0;
        }
        this.bleat=bleat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.output_dialog_fragment2, container, false);
        TextView message = (TextView)v.findViewById(R.id.msg);
        message.setText(bleat.getMessage());
//        final ImageView up = (ImageView)v.findViewById(R.id.up);
//        up.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                upvoteCnt++;
//                up.setText("Upvotes : " + Integer.toString(upvoteCnt));
//            }
//        });
        // Watch for button clicks.

        final ImageView up = (ImageView)v.findViewById(R.id.up);
        final ImageView down = (ImageView)v.findViewById(R.id.down);
        final TextView number = (TextView)v.findViewById(R.id.num);
        if(bleat.isUpvoted(getActivity()))
        {
            up.setImageResource(R.drawable.upvote_highlight);
        }
        else if(bleat.isDownvoted(getActivity()))
        {
            down.setImageResource(R.drawable.downvote_highlight);
        }
        int num = bleat.getUpvotes().size() - bleat.getDownvotes().size();
        number.setText(Integer.toString(num));

        up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BleatAction bleatAction = new BleatAction(((MapsActivity)getActivity()), "MapsActivity");
                UpvoteBleat upvoteBleat = new UpvoteBleat(bleatAction);
                try {
                    upvoteBleat.execute(bleat).get();
                    up.setImageResource(R.drawable.upvote_highlight);
                    down.setImageResource(R.drawable.downvote);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                };
                int num = bleat.computeNetUpvotes();
                number.setText(Integer.toString(num));

            }
        });
        down.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                BleatAction bleatAction = new BleatAction(((MapsActivity)getActivity()), "MapsActivity");
                DownvoteBleat downvoteBleat = new DownvoteBleat(bleatAction);
                try {
                    downvoteBleat.execute(bleat).get();
                    down.setImageResource(R.drawable.downvote_highlight);
                    up.setImageResource(R.drawable.upvote);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
                int num = bleat.getUpvotes().size() - bleat.getDownvotes().size();
                number.setText(Integer.toString(num));
            }
        });
        Rect displayRectangle = new Rect();
        Activity activity = getActivity();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View v2 =  v.findViewById(R.id.msg);
        v2.setMinimumWidth((int) (displayRectangle.width() * 0.7f));
        v2.setMinimumHeight((int) (displayRectangle.height() * 0.35f));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return v;
    }
}
