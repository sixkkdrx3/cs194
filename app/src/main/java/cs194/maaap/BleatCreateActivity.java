package cs194.maaap;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class BleatCreateActivity extends Activity {

    public double[] coords;
    public static int TAKE_PHOTO = 0;
    private boolean isPhoto;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PHOTO) {
            if(resultCode == Activity.RESULT_OK){
                File imageFile = (File) data.getSerializableExtra("image");
                Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                View msgView = findViewById(R.id.msg);
                ViewGroup parent = (ViewGroup) msgView.getParent();
                int index = parent.indexOfChild(msgView);
                parent.removeView(msgView);
                ImageView msgPhoto = (ImageView) getLayoutInflater().inflate(R.layout.bleatphoto, parent, false);
                msgPhoto.setImageBitmap(myBitmap);
                parent.addView(msgPhoto, index);
                isPhoto = true;

                View buttonView = (FloatingActionButton) findViewById(R.id.camera_button);
                ((ViewGroup)buttonView.getParent()).removeView(buttonView);
                /*CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) buttonView.getLayoutParams();
                params.setAnchorId(R.id.msg_photo);
                buttonView.setLayoutParams(params);*/

                imageFile.delete();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coords = getIntent().getDoubleArrayExtra("coords");
        isPhoto = false;
        setContentView(R.layout.input_dialog_fragment);
        // Watch for button clicks.
        FloatingActionButton cameraButton = (FloatingActionButton) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(BleatCreateActivity.this, CameraActivity.class);
                BleatCreateActivity.this.startActivityForResult(intent, TAKE_PHOTO);
            }
        });


        final TextView sendButton = (TextView) findViewById(R.id.bleat_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String res;
                String id = "";
                File file = null;
                BleatAction bleatAction = new BleatAction(BleatCreateActivity.this, "BleatCreateActivity");
                if(!isPhoto) {
                    EditText tv = (EditText) findViewById(R.id.msg);
                    res = tv.getText().toString();
                }
                else
                {
                    ImageView msgView = (ImageView) findViewById(R.id.msg_photo);
                    Bitmap fullBitmap = ((BitmapDrawable) msgView.getDrawable()).getBitmap();
                    Log.d("BleatCreateActivity", "image size: " + fullBitmap.getWidth() + "*" + fullBitmap.getHeight());
                    Pair<Integer, Integer> size = MapFragment.scalePreserveRatio(fullBitmap.getWidth(), fullBitmap.getHeight(), 1024, 1024);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, size.first, size.second, true);
                    Log.d("BleatCreateActivity", "scaled image size: " + scaledBitmap.getWidth() + "*" + scaledBitmap.getHeight());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] imageBytes = stream.toByteArray();
                    res = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    Log.d("BleatCreateActivity", "res size: " + res.length());

                    id = UUID.randomUUID().toString();
                    id = id + ".jpeg";
                    stream = new ByteArrayOutputStream();
                    fullBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] fullImageBytes = stream.toByteArray();
                    try {
                        file = new File(getCacheDir(), id);
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        ostream.write(fullImageBytes);
                        ostream.flush();
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SaveBleat saveBleat = new SaveBleat(bleatAction);

                if (isPhoto) {
                    saveBleat.execute(res, id);
                    UploadPhoto uploadPhoto = new UploadPhoto(bleatAction);
                    uploadPhoto.execute(file);
                } else {
                    saveBleat.execute(res);
                }
                BleatCreateActivity.this.finish();
            }
        });

        final TextView bleatBottom = (TextView) findViewById(R.id.bleat_wc);
        EditText tv = (EditText) findViewById(R.id.msg);
        tv.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < s.length(); i++) {
                    if (s.subSequence(i, i + 1).toString().equals("\n")) {
                        s.replace(i, i + 1, "");
                        i--;
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bleatBottom.setText((140 - s.length()) + " characters left");
            }
        });

        /*Rect displayRectangle = new Rect();
        Activity activity = getActivity();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View v2 =  v.findViewById(R.id.input);
        v2.setMinimumWidth((int) (displayRectangle.width() * 0.7f));
        v2.setMinimumHeight((int) (displayRectangle.height() * 0.65f));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return v;*/
    }
}
