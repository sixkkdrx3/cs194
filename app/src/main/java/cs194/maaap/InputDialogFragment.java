package cs194.maaap;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by waldenpond on 1/24/16.
 */
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
        Button button = (Button)v.findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String res = tv.getText().toString();
                BleatAction bleatAction = new BleatAction(getActivity().getApplicationContext());
                SaveBleat saveBleat = new SaveBleat(bleatAction);
                saveBleat.execute(res);
                dismiss();
            }
        });

        return v;
    }
}
