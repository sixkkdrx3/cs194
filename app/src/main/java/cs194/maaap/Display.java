package cs194.maaap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Display extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("valll", "hi from val");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Log.d("valll", "hiiii");
        Intent i = getIntent();
        final Bleat bleat = (Bleat)i.getSerializableExtra("myBleat");
        TextView message = (TextView)findViewById(R.id.bleat_content);
        Log.d("valll", bleat.getMessage());
        message.setText(bleat.getMessage());

        final ImageView up = (ImageView)findViewById(R.id.upvote);
        final ImageView down = (ImageView)findViewById(R.id.downvote);
        final TextView number = (TextView)findViewById(R.id.number);
        int num = bleat.getUpvotes().size() - bleat.getDownvotes().size();
        number.setText(Integer.toString(num));

        up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BleatAction bleatAction = new BleatAction(((MapsActivity)maaap));
                UpvoteBleat upvoteBleat = new UpvoteBleat(bleatAction);
                try {
                    upvoteBleat.execute(bleat).get();
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
                ;
                int num = bleat.computeNetUpvotes();
                number.setText(Integer.toString(num));

            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BleatAction bleatAction = new BleatAction(((MapsActivity) maaap));
                DownvoteBleat downvoteBleat = new DownvoteBleat(bleatAction);
                try {
                    downvoteBleat.execute(bleat).get();
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
                int num = bleat.getUpvotes().size() - bleat.getDownvotes().size();
                number.setText(Integer.toString(num));
            }
        });
    }

}
