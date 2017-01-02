package com.nightcap.podium;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            // Handle the about action
            Intent about = new Intent(this, AboutActivity.class);
            startActivity(about);
        } else if (id == R.id.nav_achievements) {
            // Handle the achievements action

            // TEST
            Intent showAd = new Intent(this, AdActivity.class);
            startActivity(showAd);

        } else if (id == R.id.nav_share) {
            shareDownloadUrl();
        } else if (id == R.id.nav_feedback) {
            sendFeedbackEmail();;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareDownloadUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // Add data to the intent, the receiving app will decide what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Download Podium");
        share.putExtra(Intent.EXTRA_TEXT, "https://www.dropbox.com/s/dc53nkkzi5r6p1n/app-debug.apk?dl=0");

        startActivity(Intent.createChooser(share, "Share Podium with"));
    }

    private void sendFeedbackEmail() {
        // Create email intent showing only email client options
        Intent feedback = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "paul.wong.88@gmail.com", null));

        // Build and assign email subject string
        String versionNumber;
        try {
            versionNumber = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionNumber = "<Unknown>";
    }
        String subject = getString(R.string.feedback_subject) + " | "
                + getString(R.string.app_name) + " (" + versionNumber + ")";
        feedback.putExtra(Intent.EXTRA_SUBJECT, subject);

        // Create chooser
        startActivity(Intent.createChooser(feedback, getString(R.string.feedback_dialog_title)));
    }

    public void startGame(View view) {
        Intent startGameIntent = new Intent(this, QuizActivityFacebook.class);
        startActivity(startGameIntent);
    }
}
