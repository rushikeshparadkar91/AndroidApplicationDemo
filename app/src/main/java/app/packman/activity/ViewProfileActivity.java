package app.packman.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.io.ByteArrayOutputStream;

import app.packman.R;
import app.packman.broadcastReceivers.PackmanBroadCastReceiver;
import app.packman.model.User;
import app.packman.services.PersonIntentService;
import app.packman.utils.Constants;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static String VIEW_PROFILE = "PACKMAN_VIEW_PROFILE_ACTIVITY_LOG";
    private static final int CAMERA_REQUEST_CODE = 1282;
    private User currentUser;
    private Boolean isUserDirty = false;
    private PackmanBroadCastReceiver userProfileBroadcastReceiver;

    @InjectView(R.id.view_profile_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.view_profile_collapsable_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @InjectView(R.id.ivProfilePicture)
    ImageView profilePic;
    @InjectView(R.id.fab_new_profile_pic)
    FloatingActionButton btnNewProfilePic;
    @InjectView(R.id.tvUserPhoneNo)
    TextView tvPhoneNo;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.btnViewAddress)
    TextView btnViewAddress;
    @InjectView(R.id.editUserName)
    Button btnEditUserName;
    @InjectView(R.id.editPhoneNo)
    Button btnEditPhoneNo;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.view_profile_layout)
    CoordinatorLayout viewProfileCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.inject(this);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("UserName@gmail.com");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupButtons();
        currentUser = PackmanSharePreference.getUser(getApplicationContext());
        populateInfoFields(currentUser);
        regsiterIntentWithService();
    }

    private void regsiterIntentWithService() {
        // The filter's action is BROADCAST_USER_ACTION
        IntentFilter userUpdateServiceFilter = new IntentFilter(
                Constants.BROADCAST_USER_ACTION);
        // Instantiates a new PackmanBroadcastReceiver
        userProfileBroadcastReceiver = new PackmanBroadCastReceiver(this) {
            @Override
            public void updateUI(Object data) {
                PackmanSharePreference.updateUser(currentUser, getApplicationContext());
                progressBar.setVisibility(View.GONE);
                onBackPressed();
            }
            @Override
            public void updateError(Object data) {
                UtilityClass.showSnackBar(viewProfileCoordinatorLayout, "Error Communicating with server");
            }
        };
        // Registers the userProfileBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                userProfileBroadcastReceiver,
                userUpdateServiceFilter);
    }

    /**
     * Populate UI fields with user data
     *
     * @param currentUser
     */
    private void populateInfoFields(User currentUser) {
        if (currentUser == null) return;
        collapsingToolbar.setTitle(currentUser.getPerson().getEmail());

        if (currentUser.getPerson().getFirstName() != null &&
                currentUser.getPerson().getLastName() != null) {
            tvUserName.setText(currentUser.getPerson().getFirstName() +
                    " " + currentUser.getPerson().getLastName());
        } else
            tvUserName.setText(currentUser.getPerson().getFirstName());

        if (currentUser.getPerson().getPhone() != null)
            tvPhoneNo.setText(currentUser.getPerson().getPhone());

        if (currentUser.getPerson().getProfilePic() != null) {
            Bitmap userImage = BitmapFactory.decodeByteArray(
                    currentUser.getPerson().getProfilePic(), 0,
                    currentUser.getPerson().getProfilePic().length
            );
            profilePic.setImageBitmap(userImage);
        }

    }

    /**
     * setup click listeners for all button in activity
     */
    private void setupButtons() {
        btnNewProfilePic.setOnClickListener(this);
        btnEditUserName.setOnClickListener(this);
        btnEditPhoneNo.setOnClickListener(this);
        btnViewAddress.setOnClickListener(this);
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


    //TODO: need to save the image to user object

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
        Log.d(VIEW_PROFILE, "RESULT CODE" + Integer.toString(resultCode));
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.d(VIEW_PROFILE, "PROFILE_PIC_VALUE" + photo.toString());
            profilePic.setImageBitmap(photo);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] arrayImage = stream.toByteArray();
            currentUser.getPerson().setProfilePic(arrayImage);
            isUserDirty = true;
        }
    }

    /**
     * Handle menu options on toolbar
     *
     * @param item - menu item clicked on tool bar
     */
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
     * setup a dialog interface to edit the phone number
     */
    private void setupPhoneEditDialog() {

        int dialogLayout = R.layout.edit_phone_no_dialog;
        AlertDialog.Builder addressDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(dialogLayout, null);
        addressDialog.setView(dialogView);

        final TextView phoneno = (TextView) dialogView.findViewById(R.id.input_phone_no);
        addressDialog.setTitle("Edit phone number");

        addressDialog.setCancelable(true);
        addressDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        });

        addressDialog.setNegativeButton("Cancel", null);
        final AlertDialog alertDialog = addressDialog.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adding the validation for profile edit here itself
                //TODO: find if there is any other way to do it
                Log.d("PhNoEdit", "on update click");
                String data = phoneno.getText().toString();
                if (data.matches("[0-9]+") && data.length() == 10) {
                    tvPhoneNo.setText(data);
                    currentUser.getPerson().setPhone(data);
                    isUserDirty = true;
                    alertDialog.dismiss();
                } else if (data.matches("[0-9]+")) {
                    phoneno.setError("Please enter digits only");
                } else if (data.length() != 10) {
                    Log.d("PhNoEdit", "phone number does not contain 10 digits");
                    phoneno.setError("Please enter valid 10 digit phone number");
                }
            }
        });
    }

    /**
     * setup a dialog interface to edit the user name
     */
    public void setupUserNameEditDialog() {

        int dialogLayout = R.layout.edit_user_name;
        AlertDialog.Builder addressDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(dialogLayout, null);
        addressDialog.setView(dialogView);
        addressDialog.setTitle("Edit Username");
        final TextView username = (TextView) dialogView.findViewById(R.id.input_user_name);
        final TextView lastname = (TextView) dialogView.findViewById(R.id.input_user_last_name);
        addressDialog.setCancelable(true);

        addressDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }

        });

        addressDialog.setNegativeButton("Cancel", null);
        final AlertDialog alertDialog = addressDialog.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adding the validation for profile edit here itself
                //TODO: find if there is any other way to do it
                Log.d("NameEdit", "on update click");
                String firstName = username.getText().toString();
                String lastName = lastname.getText().toString();
                if (validate(firstName) && validate(lastName)) {
                    tvUserName.setText(firstName + " " + lastName);
                    currentUser.getPerson().setFirstName(firstName);
                    currentUser.getPerson().setLastName(lastName);
                    isUserDirty = true;
                    alertDialog.dismiss();
                } else if (!validate(firstName)) {
                    username.setError("Please enter a valid firstName");
                } else if (!validate(lastName)) {
                    lastname.setError("Please enter a valid lastName");
                }
            }

            private boolean validate(String data) {
                if (data == null || data.trim().isEmpty() || !data.matches("[a-zA-Z]+")) {
                    return false;
                }
                return true;
            }
        });
    }


    /**
     * update the user object with new data
     */
    private void updateUserData() {
        if (currentUser == null) return;
        if (isUserDirty) {
            if (UtilityClass.isNetworkAvailable(this)) {
                Log.d(VIEW_PROFILE, "user object is dirty");
                progressBar.setVisibility(View.VISIBLE);

                Intent service = new Intent(this, PersonIntentService.class);
                service.putExtra(Constants.USER_OBJECT, currentUser);
                this.startService(service);
            } else
                UtilityClass.showSnackBar(viewProfileCoordinatorLayout, "No Internet Connection");

        }
    }

    /**
     * Overriden onBackPressed method
     */
    @Override
    public void onBackPressed() {
        updateUserData();
        super.onBackPressed();
    }

    /*
     * This callback is invoked when the system is about to destroy the Activity.
     */
    @Override
    public void onDestroy() {

        // Unregisters the userProfileBroadcastReceiver instance
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(this.userProfileBroadcastReceiver);
        // Must always call the super method at the end.
        super.onDestroy();
    }

    /**
     * Handle click events for all the view objects registered in activity
     *
     * @param v - view on which click event was performed
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.editPhoneNo:
                setupPhoneEditDialog();
                break;
            case R.id.editUserName:
                setupUserNameEditDialog();
                break;
            case R.id.fab_new_profile_pic:
                openCamera();
                break;
            case R.id.btnViewAddress:
                Intent viewAddressActivity = new Intent(this, AddressActivity.class);
                startActivity(viewAddressActivity);
                break;
        }
    }
}
