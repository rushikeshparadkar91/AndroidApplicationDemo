package app.packman.netwrok;

/**
 * Created by mlshah on 2/28/16.
 */

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.packman.volley.VolleyArrayResponseListener;
import app.packman.volley.VolleySingleton;

/**
 * This class can be used to obtain jsonArray response from volley
 * Created by mlshah on 2/15/16.
 */
public class NetworkJsonArrayLoader {

    public NetworkJsonArrayLoader() {
    }

    // TODO: Handle all exception handling
    public void requestJSON(final int method, final String url, final String requestBody, final VolleyArrayResponseListener listener, final String socialId) {
        final String JSON_ARRAY_LOADER_TAG = "NetworkJsonArrayLoader_LOG";

        // Volley request
        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(JSON_ARRAY_LOADER_TAG, "json array response" + response.toString());
                        try {
                            listener.onResponse(response);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(JSON_ARRAY_LOADER_TAG, volleyError.toString());
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                }
                Log.d(JSON_ARRAY_LOADER_TAG, "in json array error " + volleyError);
                listener.onError(volleyError.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Android app");
                params.put("user_id", socialId);
                params.put("Accept-Language", "fr");

                return params;
            }
        };

        jsonArrayReq.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonArrayReq.setShouldCache(false);

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonArrayReq);
    }

}