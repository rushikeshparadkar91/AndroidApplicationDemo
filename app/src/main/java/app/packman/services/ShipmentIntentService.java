package app.packman.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;

import java.io.IOException;

import app.packman.model.Shipment;
import app.packman.model.User;
import app.packman.netwrok.NetworkJsonArrayLoader;
import app.packman.netwrok.NetworkJsonLoader;
import app.packman.utils.Constants;
import app.packman.volley.VolleyArrayResponseListener;
import app.packman.volley.VolleyResponseListener;

/**
 * Created by sujaysudheendra on 3/15/16.
 */
public class ShipmentIntentService extends IntentService {

    public static String SHIPMENT_SERVICE = "PACKMAN_SHIPMENT_SERVICE_LOG";

    private NetworkJsonLoader jsonLoader;
    private NetworkJsonArrayLoader jsonArrayLoader;
    private Context context;
    private VolleyArrayResponseListener incomingShipmentListener, outgoingShipmentListener;
    ;
    private VolleyResponseListener shipmentListener;
    private String jsonInString;
    private Context shipmentIntent;

    public ShipmentIntentService() {
        super("Shipment_service");
        shipmentIntent = this;
        shipmentListener = initializeshipmentListener();
        incomingShipmentListener = initializeIncomingShipmentListListener();
        outgoingShipmentListener = initializeOutgoingShipmentListListener();
        jsonLoader = new NetworkJsonLoader();
        jsonArrayLoader = new NetworkJsonArrayLoader();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Long default_value = 0L;
        Log.d(SHIPMENT_SERVICE, "handle intent");
        Shipment shipment = (Shipment) intent.getSerializableExtra(Constants.SHIPMENT_OBJECT);
        String socialId = intent.getStringExtra(Constants.USER_OBJECT);
        Long userId = intent.getLongExtra(Constants.USER_ID, default_value);
        String action = intent.getStringExtra(Constants.SHIPMENT_ACTION);

        switch (action) {
            case "get_incoming":
                getIncomingShipment(socialId, userId.toString());
                break;
            case "get_outgoing":
                getOutgoingShipment(socialId, userId.toString());
                break;
            case "post":
                postShipment(shipment);
                break;
            case "update":
                updateShipment(shipment);
                break;
            case "delete":
                String shipmentId = intent.getStringExtra(Constants.SHIPMENT_ID);
                deleteShipment(shipmentId, socialId);
                break;
            case "get_single":
                break;
        }
    }

    public void postShipment(Shipment shipment) {
        Log.d(SHIPMENT_SERVICE, "posting shipment");
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            jsonInString = mapper.writeValueAsString(shipment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        jsonLoader.requestJSON(Request.Method.POST, Constants.SHIPMENT_SERVICE_URL + "save",
                jsonInString, shipmentListener, shipment.getSender().getPerson().getSocialId());
    }

    public void updateShipment(Shipment shipment) {
        Log.d(SHIPMENT_SERVICE, "updating shipment");
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            jsonInString = mapper.writeValueAsString(shipment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        jsonLoader.requestJSON(Request.Method.POST, Constants.SHIPMENT_SERVICE_URL + "update",
                jsonInString, shipmentListener, shipment.getSender().getPerson().getSocialId());
    }

    public void deleteShipment(String shipmentId, String socialId) {
        Log.d(SHIPMENT_SERVICE, "deleting shipment");

        jsonLoader.requestJSON(Request.Method.DELETE, Constants.SHIPMENT_SERVICE_URL + shipmentId,
                null, shipmentListener, socialId);
    }

    //TODO: make the user dynamic
    public void getIncomingShipment(String socialId, String userId) {
        Log.d(SHIPMENT_SERVICE, "get incoming shipment");

        jsonArrayLoader.requestJSON(Request.Method.GET,
                Constants.SHIPMENT_SERVICE_URL + "user/"+userId+"/" + Constants.INCOMING_SHIPMENTS, null, incomingShipmentListener, socialId);
    }

    public void getOutgoingShipment(String socialId, String userId) {
        Log.d(SHIPMENT_SERVICE, "get outgoing shipment");

        jsonArrayLoader.requestJSON(Request.Method.GET,
                Constants.SHIPMENT_SERVICE_URL + "user/"+userId+"/" + Constants.OUTGOING_SHIPMENTS, null, outgoingShipmentListener, socialId);
    }

    private VolleyArrayResponseListener initializeIncomingShipmentListListener() {
        VolleyArrayResponseListener shipmentListener = new VolleyArrayResponseListener() {
            @Override
            public void onError(String message) {
                Log.d(SHIPMENT_SERVICE, message);

                Intent localIntent =
                        new Intent(Constants.BROADCAST_IN_SHIPMENT_ACTION)
                                // Puts the error status into the Intent
                                .putExtra(Constants.BROADCAST_SHIPMENT_SERVCICE_ERROR, message);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(shipmentIntent).sendBroadcast(localIntent);
            }

            @Override
            public void onResponse(JSONArray response) throws IOException {
                Log.d(SHIPMENT_SERVICE, "incoming shipment response received");

                /*
                 * Creates a new Intent containing a Uri object
                 * BROADCAST_SHIPMENT_ACTION is a custom Intent action
                 */
                Intent localIntent =
                        new Intent(Constants.BROADCAST_IN_SHIPMENT_ACTION)
                                // Puts the status into the Intent
                                .putExtra(Constants.INCOMING_SHIPMENTS, response.toString());
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(shipmentIntent).sendBroadcast(localIntent);
            }
        };
        return shipmentListener;
    }


    private VolleyArrayResponseListener initializeOutgoingShipmentListListener() {
        VolleyArrayResponseListener shipmentListener = new VolleyArrayResponseListener() {
            @Override
            public void onError(String message) {
                Log.d(SHIPMENT_SERVICE, message);

                Intent localIntent =
                        new Intent(Constants.BROADCAST_OUT_SHIPMENT_ACTION)
                                // Puts the error status into the Intent
                                .putExtra(Constants.BROADCAST_SHIPMENT_SERVCICE_ERROR, message);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(shipmentIntent).sendBroadcast(localIntent);
            }

            @Override
            public void onResponse(JSONArray response) throws IOException {
                Log.d(SHIPMENT_SERVICE, "outgoing shipment response received");

                /*
                 * Creates a new Intent containing a Uri object
                 * BROADCAST_SHIPMENT_ACTION is a custom Intent action
                 */
                Intent localIntent =
                        new Intent(Constants.BROADCAST_OUT_SHIPMENT_ACTION)
                                // Puts the status into the Intent
                                .putExtra(Constants.OUTGOING_SHIPMENTS, response.toString());
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(shipmentIntent).sendBroadcast(localIntent);
            }
        };
        return shipmentListener;
    }


    private VolleyResponseListener initializeshipmentListener() {
        VolleyResponseListener shipmentListener = new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.d(SHIPMENT_SERVICE, message);

                Intent localIntent =
                        new Intent(Constants.BROADCAST_SHIPMENT_ACTION)
                                // Puts the error status into the Intent
                                .putExtra(Constants.BROADCAST_SHIPMENT_SERVCICE_ERROR, message);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(shipmentIntent).sendBroadcast(localIntent);
            }

            @Override
            public void onResponse(Object response) throws IOException {
                Log.d(SHIPMENT_SERVICE, "Shipment object received");
                /*
                 * Creates a new Intent containing a Uri object
                 * BROADCAST_SHIPMENT_ACTION is a custom Intent action
                 */
                Intent localIntent =
                        new Intent(Constants.BROADCAST_SHIPMENT_ACTION)
                                // Puts the status into the Intent
                                .putExtra(Constants.BROADCAST_SHIPMENT, response.toString());
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(shipmentIntent).sendBroadcast(localIntent);
            }
        };
        return shipmentListener;
    }
}
