package app.packman.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.exception.ConversionException;

import org.joda.time.LocalDateTime;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.packman.R;
import app.packman.broadcastReceivers.PackmanBroadCastReceiver;
import app.packman.fragment.PriceCalculatorFragment;
import app.packman.model.Address;
import app.packman.model.Box;
import app.packman.model.Shipment;
import app.packman.model.User;
import app.packman.services.ShipmentIntentService;
import app.packman.utils.Constants;
import app.packman.utils.CustomValidators.impl.PickupTime;
import app.packman.utils.InputFormValidator;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import app.packman.utils.enums.ShipmentStatus;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * CreateShipmentActivity - Allows user to create a new shipment and submit the form, hadnles the
 * UI components and their data
 * Created by sujaysudheendra on 12/22/15.
 */
public class CreateShipmentActivity extends AppCompatActivity implements Validator.ValidationListener {

    private static String CREATE_SHIPMENT_LOG = "PACKMAN_CREATE_SHIPMENT";
    private static final String SHIPMENT_TO_EDIT = "EDIT_SHIPMENT";
    private static final int CAMERA_REQUEST_CODE = 1888;
    private static final int PICK_CONTACT = 254;

    private Validator validator;
    private boolean isValidated;

    private InputFormValidator formValidator;
    private Bitmap shipmentImage;
    private Calendar selectedDateTime;
    private LocalDateTime pickupTime;
    private PackmanBroadCastReceiver shipmentPostBroadcastReceiver;
    private PriceCalculatorFragment priceCalculator;
    private User user;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private Shipment existingShipment;

    @NotEmpty
    private String stateSpinnerValue;

    @InjectView(R.id.create_shipment_layout)
    CoordinatorLayout createShipmentCoordinatorLayout;
    @InjectView(R.id.collapsable_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @InjectView(R.id.imageView_box_picture)
    ImageView boxPicture;
    @InjectView(R.id.shipment_toolbar)
    Toolbar toolbar;

    @Order(1)
    @NotEmpty(message = "Scehduled date and time is required")
    @PickupTime(dateFormat = "dd MMM yyyy HH:mm:ss")
    @InjectView(R.id.input_time)
    TextView dateTimePicker;

    @Order(2)
    @NotEmpty(message = "Receiver's name is required")
    @InjectView(R.id.input_receiver_name)
    TextView editTextReceiverContactName;
    @Order(3)
    @NotEmpty(message = "Street 1 field is required")
    @InjectView(R.id.input_address_street1)
    TextView editTextAddressStreet1;

    @Optional
    @InjectView(R.id.input_address_street2)
    TextView editTextAddressStreet2;

    @Optional
    @InjectView(R.id.input_address_landmark)
    TextView editTextLandMark;

    @Order(4)
    @NotEmpty(message = "city field is required")
    @Length(max = 20, message = "Max Allowed length of city is 20")
    @InjectView(R.id.input_address_city)
    TextView editTextCity;

    @InjectView(R.id.state_spinner)
    Spinner stateSpinner;

    @Order(6)
    @NotEmpty(message = "pincode field is required")
    @Digits(integer = 6, message = "Please Enter 6 Digits pincode")
    @InjectView(R.id.input_address_pin)
    TextView editTextPincode;

    @InjectView(R.id.receiver_name)
    TextInputLayout receiverContactName;

    @InjectView(R.id.address_street1)
    TextInputLayout addressStreet1;

    @InjectView(R.id.address_street2)
    TextInputLayout addressStreet2;

    @InjectView(R.id.address_landmark)
    TextInputLayout landMark;

    @InjectView(R.id.address_city)
    TextInputLayout city;

    @InjectView(R.id.address_pin)
    TextInputLayout pincode;

    @InjectView(R.id.btn_reg_schedule_pickup)
    Button btnScheduleRegularPickup;

    @InjectView(R.id.show_price_calculator)
    TextView calculatePriceTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shipment);
        Validator.registerAnnotation(PickupTime.class);
        validator = new Validator(this);
        validator.setValidationListener(this);

        validator.registerAdapter(TextInputLayout.class, new ViewDataAdapter<TextInputLayout, String>() {
            @Override
            public String getData(TextInputLayout view) throws ConversionException {
                return view.getEditText().getText().toString();
            }
        });
        isValidated = false;

        ButterKnife.inject(this);
        collapsingToolbar.setTitle("New Shipment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prepareInputForm();
        addFabButton();
        initializeTimePicker();
        initializeStateSpiner();
        initializeGetContactName();
        regsiterIntentWithService();
        checkForEditableShipment();
    }

    private void checkForEditableShipment() {
        existingShipment = (Shipment) getIntent().getSerializableExtra(SHIPMENT_TO_EDIT);
        if (existingShipment != null)
            populateShipmentData(existingShipment);
        else
            openCamera();
    }

    private void populateShipmentData(Shipment shipment) {
        // TODO: take receiver name from shipment object adn not receiver object once updated on server side
        collapsingToolbar.setTitle("TODO");
        editTextReceiverContactName.setText("TODO");
        if (shipment.getToAddress() != null) {
            Address receiveAaddress = shipment.getToAddress();
            editTextAddressStreet1.setText(receiveAaddress.getStreet1());
            editTextAddressStreet2.setText(receiveAaddress.getStreet2());
            editTextCity.setText(receiveAaddress.getCity());
            editTextLandMark.setText(receiveAaddress.getLandmark());
            stateSpinner.setSelection(spinnerAdapter.getPosition(receiveAaddress.getState()));
            editTextPincode.setText(receiveAaddress.getPinCode());
        }

        if (shipment.getPickupTime() != null) {
            dateTimePicker.setText(shipment.getPickupTime().toString());
        }

        if (shipment.getBox().getImage().length > 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(shipment.getBox().getImage(), 0, shipment.getBox().getImage().length);
            boxPicture.setImageBitmap(bmp);
            setShipmentImage(bmp);
        } else {
            boxPicture.setImageResource(R.drawable.box_clipart_default);
            Bitmap boxImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.box_clipart_default);
            setShipmentImage(boxImage);
        }
    }

    private void regsiterIntentWithService() {
        // The filter's action is BROADCAST_USER_ACTION
        IntentFilter shipmentPostServiceFilter = new IntentFilter(
                Constants.BROADCAST_SHIPMENT_ACTION);
        // Instantiates a new PackmanBroadcastReceiver
        shipmentPostBroadcastReceiver = new PackmanBroadCastReceiver(this) {
            @Override
            public void updateUI(Object data) {
                UtilityClass.showSnackBar(createShipmentCoordinatorLayout, "Your Shipment is saved!");
                finish();
            }

            @Override
            public void updateError(Object data) {
                UtilityClass.showSnackBar(createShipmentCoordinatorLayout, "Error Communicating with server");
            }
        };
        // Registers the userProfileBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                shipmentPostBroadcastReceiver,
                shipmentPostServiceFilter);
    }

    private void initializeStateSpiner() {
        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateSpinner.setAdapter(spinnerAdapter);

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

    /**
     * Initializes all the view objects in the create shipment form
     * and adds listeners to each field
     * to validate the input
     */
    private void prepareInputForm() {
        dateTimePicker.setInputType(InputType.TYPE_NULL);

        FragmentManager fm = getFragmentManager();
        priceCalculator = (PriceCalculatorFragment) fm.findFragmentById(R.id.fragment_price_calculator);
        priceCalculator.getView().setVisibility(View.GONE);

        /**
         * click listener for submit form button
         */
        btnScheduleRegularPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(CREATE_SHIPMENT_LOG, "onSchedulePickUp click");
                validator.validate();
                if (isValidated) {
                    submitForm(ShipmentStatus.READY);
                }
            }
        });

        calculatePriceTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showHideFragment();
            }
        });
    }

    /**
     * wrapper method to get form data and submit it
     */
    private void submitForm(ShipmentStatus status) {
        Log.d(CREATE_SHIPMENT_LOG, "submit form called");
        Shipment shipment = getFormData(status);
        shipment.setCurrency("INR");
        //shipmentService.postShipment(shipment);
        // Starts the IntentService
            /*
             * Creates a new Intent to start the RSSPullService
             * IntentService. Passes a URI in the
             * Intent's "data" field.
             */
        if (UtilityClass.isNetworkAvailable(this)) {
            if (user == null) {
                UtilityClass.BuildSimpleAlert(this, "You must be logged in", "Please login!");
                return;
            }
            Intent service = new Intent(this, ShipmentIntentService.class);
            service.putExtra(Constants.SHIPMENT_OBJECT, shipment);
            service.putExtra(Constants.SHIPMENT_ACTION, Constants.ACTION_POST);
            this.startService(service);
        } else
            UtilityClass.showSnackBar(createShipmentCoordinatorLayout, Constants.NO_INTERNET);
    }

    /**
     * Gathers all the form data and creates a new Shipment object
     *
     * @return Shipment - model.Shipment object to be posted
     */
    private Shipment getFormData(ShipmentStatus status) {
        Shipment shipment;
        if (existingShipment == null)
            shipment = new Shipment();
        else
            shipment = existingShipment;

        user = PackmanSharePreference.getUser(getApplicationContext());
        Log.d(CREATE_SHIPMENT_LOG, "" + user);
        if (user == null) {
            Log.d(CREATE_SHIPMENT_LOG, "User object is null");
            UtilityClass.BuildSimpleAlert(this, "You need to be logged in", "Please Login!").show();
            return shipment;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getShipmentImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] arrayImage = stream.toByteArray();
        shipment.setBox(new Box(arrayImage));

        if (selectedDateTime != null) {
            pickupTime = new LocalDateTime(selectedDateTime.getTime());
            shipment.setPickupTime(pickupTime);
        }
        shipment.setStatus(status);
        shipment.setToAddress(getToAddress());
        shipment.setSender(user);
        return shipment;
    }

    /**
     * Initializes the Receiver name field and adds focus listener to open contact list
     * to select receiver
     */
    private void initializeGetContactName() {
        editTextReceiverContactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextReceiverContactName.setText("");
                    if (ContextCompat.checkSelfPermission(CreateShipmentActivity.this,
                            Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateShipmentActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                PICK_CONTACT);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, PICK_CONTACT);
                    }
                }
            }
        });
    }

    /**
     * Initialize Date and Time picker
     */
    private void initializeTimePicker() {
        dateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date value = new Date();
                final Calendar cal = Calendar.getInstance();
                cal.setTime(value);
                new DatePickerDialog(CreateShipmentActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  int y, int m, int d) {
                                cal.set(Calendar.YEAR, y);
                                cal.set(Calendar.MONTH, m);
                                cal.set(Calendar.DAY_OF_MONTH, d);
                                // now show the time picker
                                new TimePickerDialog(CreateShipmentActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view,
                                                                  int h, int min) {
                                                cal.set(Calendar.HOUR_OF_DAY, h);
                                                cal.set(Calendar.MINUTE, min);
                                                selectedDateTime = cal;
                                                DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                                                dateTimePicker.setText(formatter.format(cal.getTime()));
                                            }
                                        }, cal.get(Calendar.HOUR_OF_DAY),
                                        cal.get(Calendar.MINUTE), true).show();
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    /**
     * Overriden method which receives the response from other activities and performs
     * necessary operations
     *
     * @param requestCode - Int code sent when starting an activity
     * @param resultCode  - Int value which specifies whether
     *                    the started activity returned successfully
     * @param data        - Intent returned from the returning activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("RESULT CODE", Integer.toString(resultCode));
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == 0) {
            boxPicture.setImageResource(R.drawable.box_clipart_default);
            Bitmap boxImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.box_clipart_default);
            setShipmentImage(boxImage);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.d("PHOTO_VALUE", photo.toString());
            setShipmentImage(photo);
            boxPicture.setImageBitmap(photo);
        }

        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            setContactName(data);
        }
    }

    /**
     * Sets the selected contact's name to the receiver's name field
     *
     * @param data - Intent returned from the contact picker activity
     */
    private void setContactName(Intent data) {
        Uri contactData = data.getData();
        Cursor c = managedQuery(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (hasPhone.equalsIgnoreCase("1")) {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                phones.moveToFirst();
                String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                UtilityClass.showSnackBar(createShipmentCoordinatorLayout, cNumber);
                String nameContact = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                editTextReceiverContactName.setText(nameContact);
            }
        }
    }

    /**
     * Opens the camera activity
     */
    private void openCamera() {
        //openActivity(Manifest.permission.CAMERA, CAMERA_REQUEST_CODE, android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("came", "inside");
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

                } else {
                    boxPicture.setImageResource(R.drawable.box_clipart_default);
                }
                return;
            }
            case PICK_CONTACT: {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    /**
     * Adds the floating button, used to open camera activity
     */
    private void addFabButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_pic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    /**
     * Getter for ShipmentImage
     *
     * @return ShipmentImage - Bitmap image of shipment photo taken
     */
    public Bitmap getShipmentImage() {
        return shipmentImage;
    }

    /**
     * Setter for shipmentImage
     *
     * @param shipmentImage - the Bitmap value fo photo taken
     */
    public void setShipmentImage(Bitmap shipmentImage) {
        this.shipmentImage = shipmentImage;
    }

    /**
     * Helper method to create an address object from the input form
     *
     * @return
     */
    public Address getToAddress() {
        Address address = new Address();
        address.setStreet1(editTextAddressStreet1.getText().toString());
        address.setStreet2(editTextAddressStreet2.getText().toString());
        address.setLandmark(editTextLandMark.getText().toString());
        address.setCity(editTextCity.getText().toString());
        address.setState(stateSpinnerValue);
        address.setPinCode(editTextPincode.getText().toString());
        return address;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * saves the data enterred so far as a draft shipment
     */
    public void saveShipment() {
        Log.d(CREATE_SHIPMENT_LOG, "saving shipment");
        if (existingShipment != null && !existingShipment.getStatus().equals("DRAFT")) {
            submitForm(ShipmentStatus.READY);
        } else {
            submitForm(ShipmentStatus.DRAFT);
        }
    }

    @Override
    public void onValidationSucceeded() {
        isValidated = true;
        Log.d("CreateShipmentActivity", "onValidationSuccess");
        UtilityClass.showSnackBar(createShipmentCoordinatorLayout, "Yay! we got it right!");
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            Log.d("CreateShipmentActivity", "onValidationFail");
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                UtilityClass.showSnackBar(createShipmentCoordinatorLayout, message);
            }
        }
    }


    public void showHideFragment() {
        //
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        if (priceCalculator.isHidden()) {
            ft.show(priceCalculator);
            Log.d("hidden", "Show");
        } else {
            ft.hide(priceCalculator);
            Log.d("Shown", "Hide");
        }
        ft.commit();
    }

    @Override
    public void onBackPressed()
    {
        Log.d(CREATE_SHIPMENT_LOG, "on backpressed");
        saveShipment();
        super.onBackPressed();  // optional depending on your needs
    }
}
