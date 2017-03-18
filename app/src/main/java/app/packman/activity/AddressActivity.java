package app.packman.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import app.packman.R;
import app.packman.adapters.AddressListViewAdapter;
import app.packman.broadcastReceivers.PackmanBroadCastReceiver;
import app.packman.fragment.BottomSheetDialog;
import app.packman.model.Address;
import app.packman.model.User;
import app.packman.services.PersonIntentService;
import app.packman.utils.Constants;
import app.packman.utils.Listeners.RecyclerItemClickListener;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddressActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {

    public static String VIEW_ADDRESS = "PACKMAN_ADDRESS_ACTIVITY_LOG";
    @InjectView(R.id.address_recycler_view)
    RecyclerView addressRecyclerListView;
    @InjectView(R.id.address_activity_fab)
    FloatingActionButton fab;
    @InjectView(R.id.address_activity_layout)
    CoordinatorLayout coordinatorLayout;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private AddressListViewAdapter addressListAdapter;
    private List<Address> addressList;
    private User currentUser;
    private PackmanBroadCastReceiver addressBroadcastReceiver;
    private int addressIndex;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.inject(this);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addFabButton();
        initializeAddressView();
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
                PackmanSharePreference.updateUser(currentUser, getApplicationContext());
                //addressListAdapter.notifyItemChanged(addressIndex);
                addressListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                UtilityClass.showSnackBar(coordinatorLayout, "Your address list has been updated");
            }

            @Override
            public void updateError(Object data) {
                UtilityClass.showSnackBar(coordinatorLayout, "Error Communicating with server");
            }
        };
        // Registers the userProfileBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                addressBroadcastReceiver,
                personServiceFilter);
    }

    /**
     * Add the fab button to allow user to add new address
     */
    private void addFabButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                Intent addAddressIntent = new Intent(getApplicationContext(), AddAddressActivity.class);
                startActivity(addAddressIntent);
                ;
            }
        });
    }

    /**
     * Initialize the the recycler view by populating the address list
     */
    private void initializeAddressView() {
        currentUser = PackmanSharePreference.getUser(getApplicationContext());
        if (currentUser == null) return;

        addressList = (List<Address>) currentUser.getAddress();
        toggleFabButton(addressList.size());
        // 2. set layoutManger
        addressRecyclerListView.setLayoutManager(new LinearLayoutManager(this));
        // Getting adapter by passing xml data ArrayList
        addressListAdapter = new AddressListViewAdapter(this, addressList);
        addressRecyclerListView.setAdapter(addressListAdapter);
        // 5. set item animator to DefaultAnimator
        addressRecyclerListView.setItemAnimator(new DefaultItemAnimator());
        addressRecyclerListView.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
    }

    /**
     * Toggle fab button based on the address list
     *
     * @param count - number of elements in address
     */
    private void toggleFabButton(int count) {
        if (count > 4)
            fab.setVisibility(View.GONE);
        else
            fab.setVisibility(View.VISIBLE);
    }

    /**
     * Delete the selected address from address list
     *
     * @param address - address to be deleted
     */
    public void deleteAddress(Address address) {
        if (!UtilityClass.serializeObject(currentUser.getDefaultAddress()).equals(UtilityClass.serializeObject(address))) {
            if (UtilityClass.isNetworkAvailable(this)) {
                Log.d(VIEW_ADDRESS, "deleting the address");
                currentUser.getAddress().remove(address);
                makeUpdates();
            } else
                UtilityClass.showSnackBar(coordinatorLayout, Constants.NO_INTERNET);
        } else {
            Log.d(VIEW_ADDRESS, "trying to delete default address");
            UtilityClass.BuildSimpleAlert(
                    this,
                    "You cannot delete a default address!",
                    "Please change you default address"
            ).show();
        }
    }

    private void makeUpdates() {
        progressBar.setVisibility(View.VISIBLE);
        Intent service = new Intent(this, PersonIntentService.class);
        service.putExtra(Constants.USER_OBJECT, currentUser);
        this.startService(service);
    }

    public void setDefaultAddress(Address address) {
        if (!UtilityClass.serializeObject(currentUser.getDefaultAddress()).equals(UtilityClass.serializeObject(address))) {
            if (UtilityClass.isNetworkAvailable(this)) {
                currentUser.setDefaultAddress(address);
                makeUpdates();
            } else {
                UtilityClass.showSnackBar(coordinatorLayout, Constants.NO_INTERNET);
            }
        }
    }

    /**
     * Open the edit add address acitvity with the given address
     *
     * @param selectedAddress - address selected by user
     * @param position        - position of the address in the address list
     */
    public void openAddAddressActivity(Address selectedAddress, int position) {
        fab.setVisibility(View.GONE);
        Intent addEditAddressIntent = new Intent(this, AddAddressActivity.class);
        addEditAddressIntent.putExtra(Constants.SELECTED_ADDRESS, selectedAddress);
        addEditAddressIntent.putExtra(Constants.ADDRESS_POSITION, position);
        startActivity(addEditAddressIntent);
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

    @Override
    public void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
        Log.d(VIEW_ADDRESS, "enter on resume");
        initializeAddressView();
        if (!UtilityClass.isNetworkAvailable(this))
            UtilityClass.showSnackBar(coordinatorLayout, "No Internet Connection");
    }

    @Override
    public void onItemClick(View view, int position) {
        addressIndex = position;
        bottomSheetDialog = BottomSheetDialog.newInstance(addressList.get(position), position);
        bottomSheetDialog.show(getSupportFragmentManager(), AddressActivity.class.getSimpleName());
    }
}
