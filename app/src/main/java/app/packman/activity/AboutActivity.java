package app.packman.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import app.packman.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutActivity extends AppCompatActivity {

    @InjectView(R.id.textview_Aboutus_detail) TextView aboutusText;
    @InjectView(R.id.collapsable_bar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("About Us");
        aboutusText.setText(Html.fromHtml(getResources().getString(R.string.aboutus)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addFabButton();
    }

    private void addFabButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_feedback);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
