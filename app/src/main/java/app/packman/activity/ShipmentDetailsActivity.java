package app.packman.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

import org.joda.time.DateTimeZone;

import java.util.Date;

import app.packman.R;
import app.packman.broadcastReceivers.PackmanBroadCastReceiver;
import app.packman.model.Address;
import app.packman.model.Shipment;
import app.packman.services.ShipmentIntentService;
import app.packman.utils.Constants;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import app.packman.utils.enums.ShipmentStatus;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShipmentDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.boxIV)
    ImageView boxPicture;
    @InjectView(R.id.shipemntDetailCollapsableToolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @InjectView(R.id.shipmentDetailToolbar)
    Toolbar toolbar;

    @InjectView(R.id.tvReceiverName)
    TextView tvReceiverName;
    @InjectView(R.id.tvAddressLine)
    TextView tvAddressLine;
    @InjectView(R.id.tvCity)
    TextView tvCity;
    @InjectView(R.id.tvState)
    TextView tvState;
    @InjectView(R.id.tvPincode)
    TextView tvPincode;
    @InjectView(R.id.tvPickupTime)
    TextView tvPickupTime;
    @InjectView(R.id.tvStatus)
    TextView tvStatus;
    @InjectView(R.id.tvPrice)
    TextView tvPrice;
    @InjectView(R.id.fabEditShipment)
    FloatingActionButton fabEditShipment;
    @InjectView(R.id.btn_delete_shipment)
    Button deleteShipment;
    @InjectView(R.id.shipment_detail_layout)
    CoordinatorLayout shipmentSetailCoordinatorLayout;

    private Shipment shipment;
    private static final String SHIPMENT_DETAILS = "SHIPMENT_DETAILS";
    private static final String SHIPMENT_TO_EDIT = "EDIT_SHIPMENT";
    private PackmanBroadCastReceiver shipmentPostBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipement_detail);
        ButterKnife.inject(this);
        shipment = (Shipment) getIntent().getSerializableExtra(SHIPMENT_DETAILS);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setUpShipmentDetails(shipment);
        addFabButton();
    }

    private void regsiterIntentWithService() {
        // The filter's action is BROADCAST_USER_ACTION
        IntentFilter shipmentPostServiceFilter = new IntentFilter(
                Constants.BROADCAST_SHIPMENT_ACTION);
        // Instantiates a new PackmanBroadcastReceiver
        shipmentPostBroadcastReceiver = new PackmanBroadCastReceiver(this) {
            @Override
            public void updateUI(Object data) {
                finish();
            }

            @Override
            public void updateError(Object data) {
                UtilityClass.showSnackBar(shipmentSetailCoordinatorLayout, "Error Communicating with server");
            }
        };
        // Registers the userProfileBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                shipmentPostBroadcastReceiver,
                shipmentPostServiceFilter);
    }

    /**
     * This method sets up the shipment details on the UI
     *
     * @param shipment
     */
    private void setUpShipmentDetails(Shipment shipment) {
        collapsingToolbar.setTitle(shipment.getStatus());

        // TODO: take receiver name from shipment object adn not receiver object once updated on server side
        tvReceiverName.setText("TODO");
        if (shipment.getToAddress() != null) {
            Address receiveAaddress = shipment.getToAddress();
            tvAddressLine.setText(receiveAaddress.getStreet1() + ", " + receiveAaddress.getStreet2());
            tvCity.setText(receiveAaddress.getCity());
            tvState.setText(receiveAaddress.getState());
            tvPincode.setText(receiveAaddress.getPinCode());
        }

        if(shipment.getPickupTime() != null) {
            Date javaUtilDate = shipment.getPickupTime().toDateTime(DateTimeZone.UTC).toDate();
            PrettyTime p = new PrettyTime();
            tvPickupTime.setText(p.format(javaUtilDate));
        }

        deleteShipment.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               deleteShipment();
           }
        });

        tvStatus.setText(shipment.getStatus());

        if (shipment.getBox().getImage().length > 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(shipment.getBox().getImage(), 0, shipment.getBox().getImage().length);
            boxPicture.setImageBitmap(bmp);
        } else {
            boxPicture.setImageResource(R.drawable.box_clipart_default);
        }

        // If shipment status is draft or ready then set up methods allowing user to edit shipment
        // else hide edit button
        if (!(shipment.getStatus().equals(ShipmentStatus.DRAFT.toString()) || shipment.getStatus().equals(ShipmentStatus.READY.toString()))) {
            tvPrice.setText(shipment.getPrice().toString());
            fabEditShipment.setVisibility(View.GONE);
            deleteShipment.setVisibility(View.GONE);
        }
    }

    private void addFabButton() {
        fabEditShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(getApplicationContext(), CreateShipmentActivity.class);
                editIntent.putExtra(SHIPMENT_TO_EDIT, shipment);
                startActivity(editIntent);
                finish();
            }
        });
    }

    /**
     * deletes the shipment in consideration
     */
    private void deleteShipment() {
        if (UtilityClass.isNetworkAvailable(this)) {
            Intent service = new Intent(this, ShipmentIntentService.class);
            service.putExtra(Constants.SHIPMENT_ID, String.valueOf(shipment.getShipmentId()));
            service.putExtra(Constants.USER_OBJECT, PackmanSharePreference.getUser(this).getPerson().getSocialId());
            service.putExtra(Constants.SHIPMENT_ACTION, Constants.ACTION_DELETE);
            this.startService(service);
            UtilityClass.showSnackBar(shipmentSetailCoordinatorLayout, "Your Shipment is being deleted!");
        } else
            UtilityClass.showSnackBar(shipmentSetailCoordinatorLayout, Constants.NO_INTERNET);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
