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
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BleatDisplay extends Activity {

    private LinearLayout scroll;
    private Bleat bleat;
    public Handler handler;
    private Handler parentHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Intent i = getIntent();

        String myBID = (String)i.getSerializableExtra("myBID");
        parentHandler = (Handler)i.getSerializableExtra("parentHandler");

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d("handler", "received message with what = " + msg.what);
                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(BleatDisplay.this).create();
                alertDialog.setTitle("No Internet Connection");
                alertDialog.setMessage("There is no Internet Connection now.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int which) {
                                BleatDisplay.this.finish();
                                // TODO: may need to before previous line
                                if (parentHandler != null)
                                    parentHandler.sendEmptyMessage(Constants.CONNECTION_ERROR);
                            }
                        }

                );
                alertDialog.show();
            }
        };

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
            ImageView msgPhoto = (ImageView) getLayoutInflater().inflate(R.layout.bleatsingle_photo, parent, false);
            byte[] decodedByte = Base64.decode(bleat.getMessage(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            msgPhoto.setImageBitmap(bitmap);
            final BleatAction bleatAction = new BleatAction(this, "bleatDisplay");

            msgPhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("click", "clicked");
                    DownloadPhoto downloadPhoto = new DownloadPhoto(bleatAction);
                    try {
                        downloadPhoto.execute(bleat.getPhotoID());
                    } catch (Exception e) { }
                    /* TODO: display file */
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
                UpvoteComment upvoteComment = new UpvoteComment(commentAction, commentNetVotes);
                try {
                    upvoteComment.execute(comment);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
            }
        });
        commentDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DownvoteComment downvoteComment = new DownvoteComment(commentAction, commentNetVotes);
                try {
                    downvoteComment.execute(comment);
                } catch (Exception e) {
                    Log.d("map", "ggwp");
                }
            }
        });
    }
}