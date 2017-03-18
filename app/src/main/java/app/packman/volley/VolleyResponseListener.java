package app.packman.volley;

import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;

/**
 * Created by mlshah on 2/15/16.
 */
public interface VolleyResponseListener {
    // returns string from volley request if any error occurs
    void onError(String message);

    // returns the success response from volley request
    void onResponse(Object response) throws IOException;
}