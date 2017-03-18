package app.packman.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.List;

import app.packman.R;

public class ValidationActivity extends AppCompatActivity implements Validator.ValidationListener {

    public boolean isValidated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        isValidated = false;
    }


    @Override
    public void onValidationSucceeded() {
        isValidated = true;
        Log.d("ShipmentDetailsActivity", "onValidationSuccess");
        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            Log.d("ShipmentDetailsActivity", "onValidationFail" );
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
