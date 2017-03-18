package app.packman.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.util.List;

import app.packman.R;
import app.packman.broadcastReceivers.PackmanBroadCastReceiver;
import app.packman.model.Address;
import app.packman.model.User;
import app.packman.services.PersonIntentService;
import app.packman.utils.Constants;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class AddAddressActivity extends AppCompatActivity implements Validator.ValidationListener {

    public static String ADD_ADDRESS = "PACKMAN_ADD_ADDRESS_ACTIVITY_LOG";
    private Validator validator;
    private boolean isValidated;

    @NotEmpty
    private String stateSpinnerValue;

    @Order(1)
    @NotEmpty(message = "Street 1 field is required")
    @InjectView(R.id.input_address_street1)
    EditText street1;
    @Optional
    @InjectView(R.id.input_address_street2)
    EditText street2;
    @Optional
    @InjectView(R.id.input_address_landmark)
    EditText landmark;
    @Order(2)
    @NotEmpty(message = "city field is required")
    @Length(max = 20, message = "Max Allowed length of city is 20")
    @InjectView(R.id.input_address_city)
    EditText city;

    @InjectView(R.id.state_spinner)
    Spinner stateSpinner;

    @Order(4)
    @NotEmpty(message = "pincode field is required")
    @Digits(integer = 6, message = "Please Enter 6 Digits pincode")
    @InjectView(R.id.input_address_pin)
    EditText pincode;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    private Address currentAddress;
    private User currentUser;
    private int position;
    private PackmanBroadCastReceiver addressBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        validator = new Validator(this);
        validator.setValidationListener(this);
        isValidated = false;
        ButterKnife.inject(this);
        progressBar.setVisibility(View.GONE);
        Intent i = getIntent();
        currentAddress = (Address) i.getSerializableExtra(Constants.SELECTED_ADDRESS);
        position = i.getIntExtra(Constants.ADDRESS_POSITION, -1);
        if (currentAddress != null) updateFields(currentAddress);
        addFabButton();
        initializeStateSpiner();
        regsiterIntentWithService();
    }

    private void regsiterIntentWithService() {
        // The filter's action is BROADCAST_USER_ACTION
        IntentFilter personServiceFilter = new IntentFilter(
                Constants.BROADCAST_USER_ACTION);
        // Instantiates a new PackmanBroadcastReceiver
        addressBroadcastReceiver = new PackmanBroadCastReceiver(this) {
            @Override
            public void updateUI(Object data) {
                Log.d(ADD_ADDRESS, "address added/updated");
                PackmanSharePreference.updateUser(currentUser, getApplicationContext());
                progressBar.setVisibility(View.GONE);
                finish();
            }
            @Override
            public void updateError(Object data) {
                Toast.makeText(getApplicationContext(), "Error Communicating with server", Toast.LENGTH_LONG).show();
            }
        };
        // Registers the userProfileBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                addressBroadcastReceiver,
                personServiceFilter);
    }

    private void initializeStateSpiner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateSpinner.setAdapter(adapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateSpinnerValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                stateSpinnerValue = null;
            }
        });
    }

    private void addFabButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_new_address);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
                if(isValidated) {
                    postAddress();
                }
            }
        });
    }

    private void postAddress() {
        Address uiAddress = new Address();
        uiAddress.setStreet1(street1.getText().toString());
        uiAddress.setStreet2(street2.getText().toString());
        uiAddress.setCity(city.getText().toString());
        uiAddress.setLandmark(landmark.getText().toString());
        uiAddress.setState(stateSpinnerValue);
        uiAddress.setPinCode(pincode.getText().toString());
        //TODO: set this default in database and remove from here
        uiAddress.setCountry("India");
        uiAddress.setZoneId("0");

        currentUser = PackmanSharePreference.getUser(getApplicationContext());
        boolean updateUser = false;

        if(currentUser == null) {
            onBackPressed();
            return;
        }

        if (currentAddress != null && !UtilityClass.serializeObject(uiAddress).equals(UtilityClass.serializeObject(currentAddress))) {
            List<Address> addresses = (List<Address>) currentUser.getAddress();
            addresses.set(position, uiAddress);
            updateUser = true;
        } else if (currentAddress == null) {
            currentUser.getAddress().add(uiAddress);
            updateUser = true;
        } else {
            onBackPressed();
        }

        if (updateUser) {
            if (UtilityClass.isNetworkAvailable(this)) {
                Log.d(ADD_ADDRESS, ""+ currentUser.getDefaultAddress());
                if(currentUser.getDefaultAddress() == null)
                    currentUser.setDefaultAddress(uiAddress);

                Log.d(ADD_ADDRESS, ""+ currentUser.getDefaultAddress());
                PackmanSharePreference.updateUser(currentUser, this);
                progressBar.setVisibility(View.VISIBLE);
                Intent service = new Intent(this, PersonIntentService.class);
                service.putExtra(Constants.USER_OBJECT, currentUser);
                this.startService(service);
            }
        }
    }

    private void updateFields(Address address) {
        street1.setText(address.getStreet1());
        street2.setText(address.getStreet2());
        landmark.setText(address.getLandmark());
        city.setText(address.getCity());
        address.setState(stateSpinnerValue);
        pincode.setText(address.getPinCode());
    }


    @Override
    public void onValidationSucceeded() {
        isValidated = true;
        Log.d("AddAddressActivity", "onValidationSuccess");
        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            Log.d("AddAddressActivity", "onValidationFail" );
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
