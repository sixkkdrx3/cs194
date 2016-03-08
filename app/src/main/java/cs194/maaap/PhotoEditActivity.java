package cs194.maaap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

/**
 * Created by jiahan on 3/7/16.
 */
public class PhotoEditActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo_text);

        Intent intent = getIntent();

        final File imageFile = (File) intent.getSerializableExtra("image");
        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        ImageView imgView = (ImageView) findViewById(R.id.photo_editimg);
        imgView.setImageBitmap(myBitmap);

        ImageButton addTextButton = (ImageButton) findViewById(R.id.addTextButton);
        addTextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.photo_edittext);
                editText.setVisibility(View.VISIBLE);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        ImageButton acceptPhotoButton = (ImageButton) findViewById(R.id.acceptPhotoButton);
        acceptPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Canvas canvas = new Canvas();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("image", imageFile);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
