package cs194.maaap;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DisplayDialogFragment extends DialogFragment {

    int upvoteCnt = 0;
    int downvoteCnt = 0;
    Bleat bleat;

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
        up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                upvoteCnt++;
                up.setText("Upvotes : " + Integer.toString(upvoteCnt));
            }
        });
        final Button down = (Button)v.findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                downvoteCnt++;
                down.setText("Downvotes : " + Integer.toString(downvoteCnt));
            }
        });

        return v;
    }
}
