package app.packman.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.List;

import app.packman.R;
import app.packman.adapters.ViewPagerAdapter;
import app.packman.broadcastReceivers.PackmanBroadCastReceiver;
import app.packman.fragment.NavigationDrawerFragment;
import app.packman.fragment.ShipmentListFragment;
import app.packman.model.DeviceRegisteration;
import app.packman.model.Shipment;
import app.packman.model.User;
import app.packman.services.PersonIntentService;
import app.packman.services.ShipmentIntentService;
import app.packman.utils.Constants;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import butterknife.ButterKnife;
import butterknife.InjectView;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity implements NavigationDrawerFragment.FragmentDrawerListener, GoogleApiClient.OnConnectionFailedListener {

    private static String HOME_ACTIVITY = "HOME_ACTIVITY_LOG";
    ViewPagerAdapter adapter;
    ShipmentListFragment incomingShipmentsFragment, outgoingShipmentsFragment;
    boolean shouldExecuteOnResume;
    private PackmanBroadCastReceiver inShipmentBroadcastReceiver, outShipmentBroadcastReceiver, userUpdateBroadcastReceiver;
    private GoogleApiClient mGoogleApiClient;
    private NavigationDrawerFragment drawerFragment;
    @InjectView(R.id.home_view_pager)
    ViewPager viewPager;
    @InjectView(R.id.home_tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.home_coordinatorLayout)
    CoordinatorLayout homeCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        regsiterIntentWithService();
        runPushyOnBackgroundThread(this);
        new registerForPushNotificationsAsync().execute();
        setSupportActionBar(toolbar);
        progressBar.setVisibility(View.GONE);
        shouldExecuteOnResume = false;
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        // api call to get the user's incoming and outgoing shipments
        addFabButton();
        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        setupSignOutOption();
        populateShipments();
    }

    private void regsiterIntentWithService() {
        // The filter's action is BROADCAST_IN_SHIPMENT_ACTION
        IntentFilter inShipmentServiceFilter = new IntentFilter(
                Constants.BROADCAST_IN_SHIPMENT_ACTION);
        // Instantiates a new PackmanBroadcastReceiver
        inShipmentBroadcastReceiver = new PackmanBroadCastReceiver(this) {
            @Override
            public void updateUI(Object data) {
                Log.d(HOME_ACTIVITY, "received incoming shipment");
                List<Shipment> ship = (List<Shipment>) data;
                incomingShipmentsFragment.setUpShipments(ship);
            }

            @Override
            public void updateError(Object data) {
                UtilityClass.showSnackBar(homeCoordinatorLayout, "Error Communicating with server");
                if(incomingShipmentsFragment!= null) incomingShipmentsFragment.hideSwipeLayout();
                if(outgoingShipmentsFragment!= null) outgoingShipmentsFragment.hideSwipeLayout();
            }
        };
        // Registers the inShipmentBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                inShipmentBroadcastReceiver,
                inShipmentServiceFilter);

        IntentFilter outShipmentServiceFilter = new IntentFilter(
                Constants.BROADCAST_OUT_SHIPMENT_ACTION);
        // Instantiates a new PackmanBroadcastReceiver
        outShipmentBroadcastReceiver = new PackmanBroadCastReceiver(this) {
            @Override
            public void updateUI(Object data) {
                Log.d(HOME_ACTIVITY, "received outgoing shipment");
                List<Shipment> ship = (List<Shipment>) data;
                outgoingShipmentsFragment.setUpShipments(ship);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void updateError(Object data) {
                UtilityClass.showSnackBar(homeCoordinatorLayout, "Error Communicating with server");
            }
        };
        // Registers the outShipmentBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                outShipmentBroadcastReceiver,
                outShipmentServiceFilter);


        // The filter's action is BROADCAST_USER_ACTION
        IntentFilter userUpdateServiceFilter = new IntentFilter(
                Constants.BROADCAST_USER_ACTION);
        // Instantiates a new PackmanBroadcastReceiver
        userUpdateBroadcastReceiver = new PackmanBroadCastReceiver(this) {
            @Override
            public void updateUI(Object data) {
                Log.d("userUpdated", "user updated");
                //onBackPressed();
            }
            @Override
            public void updateError(Object data) {
                Log.d("loginActivity", "error registering intent with service");
                //TODO: do something
            }
        };
        // Registers the userProfileBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                userUpdateBroadcastReceiver,
                userUpdateServiceFilter);
    }

    private void setupViewPager(ViewPager viewPager) {
        incomingShipmentsFragment = new ShipmentListFragment(ContextCompat.getColor(getApplicationContext(), R.color.windowBackground));
        outgoingShipmentsFragment = new ShipmentListFragment(ContextCompat.getColor(getApplicationContext(), R.color.windowBackground));
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(incomingShipmentsFragment, getString(R.string.incoming_tab));
        adapter.addFrag(outgoingShipmentsFragment, getString(R.string.outgoing_tab));
        viewPager.setAdapter(adapter);
    }

    private void addFabButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkForDefaultAddress()) {
                    Intent createPackageIntent = new Intent(getApplicationContext(), CreateShipmentActivity.class);
                    startActivity(createPackageIntent);
                }
            }
        });
    }

    private boolean checkForDefaultAddress(){
        User user = PackmanSharePreference.getUser(this);
        if (user.getDefaultAddress() == null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please set the default address before proceeding")
                    .setTitle("No Default address set");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent addAddress = new Intent(getApplicationContext(), AddAddressActivity.class);
                    startActivity(addAddress);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
    }


    public void populateShipments() {
        if (UtilityClass.isNetworkAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            populateIncomingShipments();
            populateOutgoingShipments();
        } else {
            UtilityClass.showSnackBar(homeCoordinatorLayout, Constants.NO_INTERNET);
        }
    }

    /**
     * This method makes the api call
     */
    private void populateIncomingShipments() {
        User user = PackmanSharePreference.getUser(this);
        Intent service = new Intent(this, ShipmentIntentService.class);
        service.putExtra(Constants.SHIPMENT_ACTION, Constants.ACTION_GET_INCOMING);
        service.putExtra(Constants.USER_OBJECT, user.getPerson().getSocialId());
        service.putExtra(Constants.USER_ID, user.getUserId());
        this.startService(service);
    }

    private void populateOutgoingShipments() {
        User user = PackmanSharePreference.getUser(this);
        Intent service = new Intent(this, ShipmentIntentService.class);
        service.putExtra(Constants.SHIPMENT_ACTION, Constants.ACTION_GET_OUTGOING);
        service.putExtra(Constants.USER_OBJECT, user.getPerson().getSocialId());
        service.putExtra(Constants.USER_ID, user.getUserId());
        this.startService(service);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            populateIncomingShipments();
            populateOutgoingShipments();
        } else {
            shouldExecuteOnResume = true;
        }
    }

    /**
     * Pushy.listen is a synchronous call, so running it on
     * background thread
     */
    private void runPushyOnBackgroundThread(final Context context) {
        Log.d("pushyListen", "in runPushyOnBackground");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Pushy.listen(context);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            Log.i(HOME_ACTIVITY, "Refresh menu item selected");

            // Signal SwipeRefreshLayout to start the progress indicator

            // Start the refresh background task.
            // This method calls setRefreshing(false) when it's finished.
            if (UtilityClass.isNetworkAvailable(this)) {
                incomingShipmentsFragment.showSwipeLayout();
                outgoingShipmentsFragment.showSwipeLayout();
                populateShipments();
            } else {
                UtilityClass.showSnackBar(homeCoordinatorLayout, Constants.NO_INTERNET);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class registerForPushNotificationsAsync extends AsyncTask<Void, Void, Exception>
    {
        protected Exception doInBackground(Void... params)
        {
            try
            {
                // Acquire a unique registration ID for this device
                String registrationId = Pushy.register(getApplicationContext());
                Log.d("registerationId", registrationId);
                // Send the registration ID to your backend server and store it for later
                sendRegistrationIdToBackendServer(registrationId);
            }
            catch( Exception exc )
            {
                // Return exc to onPostExecute
                return exc;
            }

            // We're good
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc)
        {
            // Failed?
            if ( exc != null )
            {
                // Show error as toast message
                Log.e("Pushy", "failed to register device with pushy");
                return;
            }
            // Succeeded, do something to alert the user
        }
    }

    // Example implementation
    void sendRegistrationIdToBackendServer(String registrationId) throws Exception
    {
        Log.d("deviceRegURL", "" + Constants.USER_URL);
        User user = PackmanSharePreference.getUser(getApplicationContext());
        List<DeviceRegisteration> deviceRegisterations = (List<DeviceRegisteration>) user.getPerson().getDeviceRegisteration();
        for(DeviceRegisteration device : deviceRegisterations) {
            // if device is already registered then don't add the registeration id
            if(device.getRegisterationId().equals(registrationId))
                return;
        }

        user.getPerson().getDeviceRegisteration().add(new DeviceRegisteration(registrationId));
        PackmanSharePreference.updateUser(user, getApplicationContext());
        Intent service = new Intent(this, PersonIntentService.class);
        service.putExtra(Constants.USER_OBJECT, user);
        this.startService(service);
    }

    private void setupSignOutOption() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        PackmanSharePreference.deleteUser(getApplicationContext());
                        finish();
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(HOME_ACTIVITY, "connection failed");
    }

}
