package cs194.maaap;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class InputDialogFragment extends DialogFragment {

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static InputDialogFragment newInstance(int num) {
        InputDialogFragment f = new InputDialogFragment();
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_dialog_fragment, container, false);
        final EditText tv = (EditText)v.findViewById(R.id.msg);
        // Watch for button clicks.
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String res = tv.getText().toString();
                BleatAction bleatAction = new BleatAction(((MapsActivity)getActivity()), "MapsActivity");
                SaveBleat saveBleat = new SaveBleat(bleatAction);
                saveBleat.execute(res);
                dismiss();
            }
        });

        final TextView bleatBottom = (TextView)v.findViewById(R.id.bleat_wc);
        tv.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s)
            {
                for(int i = 0; i < s.length(); i++){
                    if(s.subSequence(i, i+1).toString().equals("\n")) {
                        s.replace(i, i + 1, "");
                        i--;
                    }
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                bleatBottom.setText((140 - s.length()) + " characters left");
            }
        });

        Rect displayRectangle = new Rect();
        Activity activity = getActivity();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View v2 =  v.findViewById(R.id.input);
        v2.setMinimumWidth((int) (displayRectangle.width() * 0.7f));
        v2.setMinimumHeight((int) (displayRectangle.height() * 0.65f));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return v;
    }
}
