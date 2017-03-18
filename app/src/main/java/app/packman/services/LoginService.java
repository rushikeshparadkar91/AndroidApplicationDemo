package app.packman.services;

import android.content.Context;

import com.android.volley.Request;

import app.packman.netwrok.NetworkJsonLoader;
import app.packman.utils.Constants;
import app.packman.utils.PackmanSharePreference;
import app.packman.volley.VolleyResponseListener;

/**
 * Created by sujaysudheendra on 3/12/16.
 */
public class LoginService {

    private NetworkJsonLoader jsonLoader;
    private Context context;
    private VolleyResponseListener listener;
    private String jsonInString;

    public LoginService(Context context, VolleyResponseListener listener) {
        this.context = context;
        this.listener = listener;
        jsonLoader = new NetworkJsonLoader();
    }

    public void getUser(String userTokenId) {
        jsonInString = "{idToken:"+ userTokenId+"}";
        jsonLoader.requestJSON(Request.Method.POST, Constants.LOGIN_SERVICE_URL,
                jsonInString, listener, null);
    }
}
