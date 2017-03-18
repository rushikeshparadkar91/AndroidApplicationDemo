package app.packman.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import app.packman.R;
import app.packman.model.Person;
import app.packman.model.User;
import app.packman.services.LoginService;
import app.packman.utils.Constants;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import app.packman.volley.VolleyResponseListener;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Activity class to implement the login screen.
 *
 * @author Rushikesh Paradkar
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int RC_SIGN_IN = 9001;
    private TextView mStatusTextView;
    private VolleyResponseListener userForLoginListner;
    private User user;
    private LoginService loginService;
    boolean didLogin = false;
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;


    //injectors for the texts
//    @InjectView(R.id.input_email)
//    EditText _emailText;
//    @InjectView(R.id.input_password)
//    EditText _passwordText;
//    @InjectView(R.id.btn_login)
//    Button _loginButton;
//    @InjectView(R.id.link_signup)
//    TextView _signupLink;
//    @InjectView(R.id.skip)
//    TextView skip;
    @InjectView(R.id.sign_in_button)
    SignInButton _signinButton;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        userForLoginListner = initializeVolleyObjectListener();
        loginService = new LoginService(this, userForLoginListner);
//        _loginButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                login();
//            }
//        });
//
//        _signupLink.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
//            }
//        });
//        skip.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Home activity
//                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("593443888502-m7us0rso34ekq8pkdkd2vudbrn5rkcgj.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        _signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    /**
     * Handle the result of the google sign-in process
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.d(TAG, "login success");
            GoogleSignInAccount account = result.getSignInAccount();
            saveUser(account);
        } else {
            Log.d(TAG, "login failed");
            onLoginFailed();
        }
    }

    /**
     * Create a packman user object from the ggole account object
     * @param account
     */
    private void saveUser(GoogleSignInAccount account){
        if (UtilityClass.isNetworkAvailable(this)) {
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_DialogTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            String idToken = account.getIdToken();
            Log.d(TAG, "verifying with token" + idToken);
            loginService.getUser(idToken);
        } else {
            UtilityClass.BuildSimpleAlert(this, "No Internet Connection", "Login failed");
        }
    }

    private void loadProfile(final User user){
        Picasso.with(this).load(user.getPerson().getProfilePicUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] arrayImage = stream.toByteArray();
                user.getPerson().setProfilePic(arrayImage);
                PackmanSharePreference.saveUser(user, getApplicationContext());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                PackmanSharePreference.saveUser(user, getApplicationContext());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Method that performs the authentication logic for the application.
     */
    public void login() {

//        Log.d(TAG, "Login");
//        if (!validate()) {
//            onLoginFailed();
//            return;
//        }
//
//        _loginButton.setEnabled(false);
//
//        progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_DialogTheme);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();
//
//        String email = _emailText.getText().toString();
//        String password = _passwordText.getText().toString();
//
//        // TODO: Implement authentication logic here.
//        if (UtilityClass.isNetworkAvailable(this)) {
//            loginService.getUser("1");
//        } else {
//            UtilityClass.BuildSimpleAlert(this, "No Internet Connection", "Login failed");
//        }
    }

    private void completeLogin() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (didLogin)
                            onLoginSuccess();
                        else
                            onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * Logic to be performed on successful login.
     */
    public void onLoginSuccess() {
        //_loginButton.setEnabled(true);
        progressDialog.dismiss();
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Logic to be performed for login failure.
     */
    public void onLoginFailed() {
        //_loginButton.setEnabled(true);
        AlertDialog dialog = UtilityClass.BuildSimpleAlert(this, Constants.USER_DETAILS_ERROR, "Login failed");
        dialog.show();
    }

    /**
     * Method that perform the validation on the input fields.
     *
     * @return true if the validation successful, false otherwise.
     */
    public boolean validate() {
        boolean valid = true;

//        String email = _emailText.getText().toString();
//        String password = _passwordText.getText().toString();
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }
//
//        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
//            _passwordText.setError("between 4 and 10 alphanumeric characters");
//            valid = false;
//        } else {
//            _passwordText.setError(null);
//        }

        return valid;
    }

    private VolleyResponseListener initializeVolleyObjectListener() {
        VolleyResponseListener userForLoginListner = new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.d("Login activity", message);
                onLoginFailed();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) throws IOException {
                Log.d("Login Activity", " success get user");
                ObjectMapper mapper = new ObjectMapper();
                user = mapper.readValue(response.toString(), User.class);
                loadProfile(user);
                didLogin = true;
                completeLogin();
            }
        };
        return userForLoginListner;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        onLoginFailed();
    }
}