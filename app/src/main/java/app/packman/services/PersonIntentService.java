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

import java.io.IOException;

import app.packman.model.User;
import app.packman.netwrok.NetworkJsonLoader;
import app.packman.utils.Constants;
import app.packman.volley.VolleyResponseListener;

/**
 * Created by sujaysudheendra on 3/14/16.
 */
public class PersonIntentService extends IntentService {

    public static String PERSON_SERVICE = "PACKMAN_PERSON_SERVICE_LOG";
    private NetworkJsonLoader jsonLoader;
    private Context context;
    private VolleyResponseListener listener;
    private String jsonInString;
    private Context personIntent;

    /**
     * Creates an PersonIntentService.
     *
     */
    public PersonIntentService() {
        super("perons_service");
        personIntent = this;
        this.listener = initializeVolleyObjectListener();
        jsonLoader = new NetworkJsonLoader();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        User user = (User) intent.getSerializableExtra(Constants.USER_OBJECT);
        updateUser(user);
    }

    public void updateUser(User user) {
        Log.d(PERSON_SERVICE, "update user object");
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            jsonInString = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Log.d("Json of user",jsonInString);
        jsonLoader.requestJSON(Request.Method.POST, Constants.USER_SERVICE_URL + "update",
                jsonInString, listener, user.getPerson().getSocialId());
    }

    private VolleyResponseListener initializeVolleyObjectListener() {
        VolleyResponseListener updateUserListener = new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.d(PERSON_SERVICE, message);

                Intent localIntent =
                        new Intent(Constants.BROADCAST_USER_ACTION)
                                // Puts the error status into the Intent
                                .putExtra(Constants.BROADCAST_USER_SERVCICE_ERROR, message);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(personIntent).sendBroadcast(localIntent);
            }

            @Override
            public void onResponse(Object response) throws IOException {
                Log.d(PERSON_SERVICE, "UPDATE USER success");
                /*
                 * Creates a new Intent containing a Uri object
                 * BROADCAST_USER_ACTION is a custom Intent action
                 */
                Intent localIntent =
                        new Intent(Constants.BROADCAST_USER_ACTION)
                                // Puts the status into the Intent
                                .putExtra(Constants.BROADCAST_REPLY_USER_UPDATE, response.toString());
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(personIntent).sendBroadcast(localIntent);
            }
        };
        return updateUserListener;
    }
}
