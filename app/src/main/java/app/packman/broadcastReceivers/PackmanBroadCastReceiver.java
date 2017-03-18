package app.packman.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import app.packman.model.Shipment;
import app.packman.utils.Constants;

/**
 * Broadcast receiver for receiving status updates from the IntentService
 * Created by sujaysudheendra on 3/14/16.
 */
public abstract class PackmanBroadCastReceiver extends BroadcastReceiver {

    private static String LOG_TAG = "PACKMAN_BROADCASTRECEIVER_LOG";
    private Context onAtivity; // the activity on which this instance of receiver is registered

    public PackmanBroadCastReceiver(Context context) {
        onAtivity = context;
    }

    public abstract void updateUI(Object data);
    public abstract void updateError(Object data);

    /**
     * This method is called by the system when a broadcast Intent is matched by this class'
     * intent filters
     *
     * @param context An Android context
     * @param intent  The incoming broadcast Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // user and address operations block
        if (intent.getAction().equals(Constants.BROADCAST_USER_ACTION)) {
            String response = intent.getStringExtra(Constants.BROADCAST_REPLY_USER_UPDATE);

            if (null != response) {
                Log.d(LOG_TAG, "person object updated");
                updateUI("");
            } else {

                updateUI("");  // this line needs to be removed once we send valid json on response
                response = intent.getStringExtra(Constants.BROADCAST_USER_SERVCICE_ERROR);
                Log.d(LOG_TAG, "error updating" + response);
            }
        }

        // create update delete shipment
        if (intent.getAction().equals(Constants.BROADCAST_SHIPMENT_ACTION)) {
            String response = intent.getStringExtra(Constants.BROADCAST_SHIPMENT_SERVCICE_SUCCESS);

            if (null != response) {
                Log.d(LOG_TAG, "shipment object updated");
                updateUI("");
            } else {
                //updateUI("done"); // remove this line once we pass valid json response
                response = intent.getStringExtra(Constants.BROADCAST_SHIPMENT_SERVCICE_ERROR);
                Log.d(LOG_TAG, "error in service call" + response);
                updateError(response);
            }
        }

        //incoming shipment block
        if (intent.getAction().equals(Constants.BROADCAST_IN_SHIPMENT_ACTION)) {
            String response = intent.getStringExtra(Constants.INCOMING_SHIPMENTS);
            final List<Shipment> shipments;

            if (null != response) {
                Log.d(LOG_TAG, "incoming shipment list received");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    shipments = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, Shipment.class));
                    Log.d(LOG_TAG, "" + shipments.toString());
                    updateUI(shipments);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                response = intent.getStringExtra(Constants.BROADCAST_SHIPMENT_SERVCICE_ERROR);
                Log.d(LOG_TAG, "error in service call" + response);
                updateError(response);
            }
        }

        //out going shipment block
        if (intent.getAction().equals(Constants.BROADCAST_OUT_SHIPMENT_ACTION)) {
            String response = intent.getStringExtra(Constants.OUTGOING_SHIPMENTS);
            final List<Shipment> shipments;

            if (null != response) {
                Log.d(LOG_TAG, "outgoing shipment list received");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    shipments = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, Shipment.class));
                    Log.d(LOG_TAG, "" + shipments.toString());
                    updateUI(shipments);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                response = intent.getStringExtra(Constants.BROADCAST_SHIPMENT_SERVCICE_ERROR);
                Log.d(LOG_TAG, "error in service call" + response);
                updateError(response);
            }
        }
    }
}
