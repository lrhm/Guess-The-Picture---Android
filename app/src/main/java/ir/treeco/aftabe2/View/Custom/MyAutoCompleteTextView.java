package ir.treeco.aftabe2.View.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

/**
 * Created by al on 12/28/15.
 */
public class MyAutoCompleteTextView extends AutoCompleteTextView {



    private OnKeyboardDismiss onKeyboardDismiss;

    public MyAutoCompleteTextView(Context context) {
        super(context);
    }
    public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onKeyboardDismiss.onKeyboardDismiss();
        }

        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnKeyboardDismiss(OnKeyboardDismiss onKeyboardDismiss) {
        this.onKeyboardDismiss = onKeyboardDismiss;
    }

    public interface OnKeyboardDismiss{
        void onKeyboardDismiss();
    }

}
