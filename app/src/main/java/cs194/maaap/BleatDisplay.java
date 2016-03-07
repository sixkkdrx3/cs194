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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("valll", "hi from val");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Log.d("valll", "hiiii");
        Intent i = getIntent();
        final Bleat bleat = (Bleat)i.getSerializableExtra("myBleat");

        TextView message = (TextView)findViewById(R.id.bleat_content);
        if(bleat.getMessage().length()<200) {
            Log.d("valll", bleat.getMessage());
            message.setText(bleat.getMessage());
        }
        else
        {
            ViewGroup parent = (ViewGroup) message.getParent();
            int index = parent.indexOfChild(message);
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
                    File file = null;
                    try {
                        file = downloadPhoto.execute(bleat.getPhotoID()).get();
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

        /* test for creating comment
        CommentAction commentAction = new CommentAction(this, bleat.getBID());
        SaveComment saveComment = new SaveComment(commentAction);
        saveComment.execute("what the fk is this comment");
        */

        LinearLayout scroll = (LinearLayout)findViewById(R.id.comment_layout);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final CommentAction commentAction = new CommentAction(this, bleat.getBID());
        GetComments getComments = new GetComments(commentAction);
        List<Comment> allComments = null;

        try {
            allComments = getComments.execute().get();
        } catch (Exception e) { }

        int cnt = 0;
        for (final Comment comment : allComments) {

            if (!comment.getBID().equals(bleat.getBID())) continue;
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
                    UpvoteComment upvoteComment = new UpvoteComment(commentAction);
                    try {
                        upvoteComment.execute(comment).get();
                    } catch (Exception e) {
                        Log.d("map", "ggwp");
                    }
                    ;
                    int num = comment.computeNetUpvotes();
                    commentNetVotes.setText(Integer.toString(num));

                }
            });
            commentDown.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DownvoteComment downvoteComment = new DownvoteComment(commentAction);
                    try {
                        downvoteComment.execute(comment).get();
                    } catch (Exception e) {
                        Log.d("map", "ggwp");
                    }
                    int num = comment.computeNetUpvotes();
                    commentNetVotes.setText(Integer.toString(num));
                }
            });
        }

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
                    SaveComment saveComment = new SaveComment(commentAction);
                    saveComment.execute(res);
                }
                tv.setText("");
            }
        });
    }
}