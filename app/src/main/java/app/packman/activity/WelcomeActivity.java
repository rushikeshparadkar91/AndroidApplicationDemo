package app.packman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import app.packman.R;
import app.packman.model.User;
import app.packman.utils.PackmanSharePreference;

public class WelcomeActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final User user = PackmanSharePreference.getUser(getApplicationContext());

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i;
                if (user != null)
                    i = new Intent(WelcomeActivity.this, HomeActivity.class);
                else
                    i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


}
