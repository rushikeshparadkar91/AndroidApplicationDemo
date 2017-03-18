package app.packman.volley;

import org.json.JSONArray;

import java.io.IOException;

/**
 * Created by mlshah on 2/28/16.
 * This acts as a listener for volley api call
 * This methods are called once response is received from the volley api call
 */
public interface VolleyArrayResponseListener {

    // returns string from volley request if any error occurs
    void onError(String message);

    // returns the success array response from volley request
    void onResponse(JSONArray response) throws IOException;
}
