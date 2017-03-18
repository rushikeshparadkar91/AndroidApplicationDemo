package app.packman.netwrok;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.packman.utils.UtilityClass;
import app.packman.volley.VolleyResponseListener;
import app.packman.volley.VolleySingleton;

/**
 * This class can be used to obtain jsonObject response from volley
 * Created by mlshah on 2/15/16.
 */
public class NetworkJsonLoader {

    private String JSON_LOADER = "NETWORKJSON_LOADER_LOG";

    public NetworkJsonLoader() {
    }

    // TODO: Handle all exception handling
    public void requestJSON(int method, String url, String requestBody, final VolleyResponseListener listener, final String socialId) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        JSONObject jsonRequest = null;

        // Check if request body present in case of POST request
        if (requestBody != null) {
            try {
                jsonRequest = new JSONObject(requestBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Volley request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(method, url,
                jsonRequest, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(JSON_LOADER, "received response");
                    listener.onResponse(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(JSON_LOADER, error.toString());
                listener.onError(error.toString());
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

        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}