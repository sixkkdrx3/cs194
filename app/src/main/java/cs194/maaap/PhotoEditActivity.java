package cs194.maaap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by jiahan on 3/7/16.
 */


public class PhotoEditActivity extends Activity {

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo_text);

        Intent intent = getIntent();

        final File imageFile = (File) intent.getSerializableExtra("image");
        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        final ImageView imgView = (ImageView) findViewById(R.id.photo_editimg);
        imgView.setImageBitmap(myBitmap);

        final EditText editText = (EditText) findViewById(R.id.photo_edittext);
        ImageButton addTextButton = (ImageButton) findViewById(R.id.addTextButton);
        addTextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setVisibility(View.VISIBLE);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        ImageButton acceptPhotoButton = (ImageButton) findViewById(R.id.acceptPhotoButton);
        acceptPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Bitmap bitmap = Bitmap.createBitmap(imgView.getWidth(), imgView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            imgView.draw(canvas);
            //editText.layout(editText.getLeft(), editText.getTop(), editText.getRight(), editText.getBottom());
            if(!editText.getText().toString().equals("")) {
                canvas.save();
                canvas.translate(0, editText.getTop());
                TextView textView = (TextView) findViewById(R.id.photo_textview);
                textView.setText(editText.getText().toString());
                textView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.INVISIBLE);
                textView.draw(canvas);
                canvas.restore();
                Log.d("pea", "coords " + editText.getLeft() + " " + editText.getTop() + " " + editText.getRight() + " " + editText.getBottom());
            }
            //textView.setVisibility(View.INVISIBLE);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            File editedFile = null;
            byte[] fullImageBytes = stream.toByteArray();
            try {
                editedFile = File.createTempFile("bleatEditedPhoto", ".jpg", getCacheDir());
                editedFile.createNewFile();
                FileOutputStream ostream = new FileOutputStream(editedFile);
                ostream.write(fullImageBytes);
                ostream.flush();
                ostream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("image", editedFile);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();


            }
        });
    }
}
