package cs194.maaap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Citation: http://stackoverflow.com/questions/5486789/how-do-i-make-a-splash-screen.
 */
public class Start extends Activity {
    private final int LOADING_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.start);

        /* New Handler to start the Maps activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(Start.this,MainActivity.class);
                Start.this.startActivity(mainIntent);
                Start.this.finish();
            }
        }, LOADING_LENGTH);
    }


}
