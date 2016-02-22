package cs194.maaap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Display extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("valll", "hi from val");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Log.d("valll", "hiiii");
        Intent i = getIntent();
        Bleat bleat = (Bleat)i.getSerializableExtra("myBleat");
        TextView message = (TextView)findViewById(R.id.bleat_content);
        Log.d("valll", bleat.getMessage());
        message.setText(bleat.getMessage());
    }

}
