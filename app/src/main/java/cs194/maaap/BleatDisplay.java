package cs194.maaap;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BleatDisplay extends Activity {

    private LinearLayout scroll;
    private Bleat bleat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Intent i = getIntent();

        String myBID = (String)i.getSerializableExtra("myBID");
        bleat = DataStore.getInstance().getBleat(myBID);
        DataStore.getInstance().addSeenBleat(myBID);

        TextView message = (TextView)findViewById(R.id.bleat_content);
        if(bleat.getMessage().length()<200) {
            message.setText(bleat.getMessage());
            Log.d("val", "lol");
        }
        else
        {
            Log.d("val","hi from val");
            ViewGroup parent = (ViewGroup) message.getParent();
            int index = parent.indexOfChild(message);
            Log.d("val", "xy" + index);
            parent.removeView(message);
            final ImageView msgPhoto = (ImageView) getLayoutInflater().inflate(R.layout.bleatsingle_photo, parent, false);
            msgPhoto.setImageResource(R.drawable.blank);
            final BleatAction bleatAction = new BleatAction(this, "BleatDisplay");
            try {
                bleatAction.downloadPhoto(bleat.getPhotoID(), msgPhoto);
            } catch (Exception e) {
            }

            msgPhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                }
            });
            parent.addView(msgPhoto, index);

        }

        final ImageView up = (ImageView)findViewById(R.id.up);
        final ImageView down = (ImageView)findViewById(R.id.down);
        final TextView number = (TextView)findViewById(R.id.num);
        int num = bleat.getUpvotes().size() - bleat.getDownvotes().size();
        final BleatAction bleatAction = new BleatAction(this, "BleatDisplay");
        number.setText(Integer.toString(num));

        up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UpvoteBleat upvoteBleat = new UpvoteBleat(bleatAction, number, up, down);
                try {
                    upvoteBleat.execute(bleat);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DownvoteBleat downvoteBleat = new DownvoteBleat(bleatAction, number, up, down);
                try {
                    downvoteBleat.execute(bleat);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
            }
        });

        scroll = (LinearLayout)findViewById(R.id.comment_layout);
        final CommentAction commentAction = new CommentAction(this, bleat.getBID());

        GetComments getComments = new GetComments(commentAction, this);
        try {
            getComments.execute();
        } catch (Exception e) { }

        final EditText tv = (EditText)findViewById(R.id.new_comment);
//        tv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                tv.requestLayout();
//                BleatDisplay.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//                return false;
//            }
//        });
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Button reportBtn = (Button) findViewById(R.id.report_button);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(BleatDisplay.this).create();
                alertDialog.setTitle("Report Received");
                alertDialog.setMessage("Thank you for reporting spam.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which)
                    {
                        // Write your code here to execute after dialog    closed
                        alertDialog.dismiss();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

            }
        });

        Button button = (Button)findViewById(R.id.comment_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String res = tv.getText().toString();
                if (res.length() > 0) {
                    SaveComment saveComment = new SaveComment(commentAction, BleatDisplay.this);
                    try {
                        saveComment.execute(res);
                    } catch (Exception e) { }
                }
                tv.setText("");
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(tv.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    public void addView(final Comment comment) {
        int cnt = scroll.getChildCount();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final CommentAction commentAction = new CommentAction(this, bleat.getBID());

        if (!comment.getBID().equals(bleat.getBID())) return;
        View view = inflater.inflate(R.layout.comment_fragment, null);
        if (cnt % 2 == 1){
            view.setBackgroundColor(getResources().getColor((R.color.material_grey_300)));
        }
        cnt++;
        scroll.addView(view);
        TextView commentMessage = (TextView)view.findViewById(R.id.comment_content);
        commentMessage.setText(comment.getMessage());

        final TextView commentNetVotes = (TextView)view.findViewById(R.id.num);
        commentNetVotes.setText(Integer.toString(comment.computeNetUpvotes()));

        final ImageView commentUp = (ImageView)view.findViewById(R.id.up);
        final ImageView commentDown = (ImageView)view.findViewById(R.id.down);

        commentUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UpvoteComment upvoteComment = new UpvoteComment(commentAction, commentNetVotes, commentUp, commentDown);
                try {
                    upvoteComment.execute(comment);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
            }
        });
        commentDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DownvoteComment downvoteComment = new DownvoteComment(commentAction, commentNetVotes, commentUp, commentDown);
                try {
                    downvoteComment.execute(comment);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
            }
        });
    }
}