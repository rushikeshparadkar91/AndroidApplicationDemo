package app.packman.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * InputFormValditor - Helper class to watch on the input fields and validate the user input before
 * form is submitted
 * Created by sujaysudheendra on 12/22/15.
 */
public abstract class InputFormValidator implements TextWatcher {
    private final TextView textView;

    /**
     * Constructor Initialize new Object for the text field specified
     * @param textView
     */
    public InputFormValidator(TextView textView) {
        this.textView = textView;
    }

    /**
     * Abstract method to be overriden to implement custom valdiation
     * @param textView - The text field for which the validation is to be performed
     * @param text - text data of the field
     */
    public abstract void validate(TextView textView, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        validate(textView, text);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
}
