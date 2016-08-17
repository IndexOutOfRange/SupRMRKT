package com.stetho.suprmrkt;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int RC_CAMERA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnFabClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @AfterPermissionGranted(RC_CAMERA)
    private void launchScanproduitActivity() {
        Intent intent = new Intent(MainActivity.this, ScanProduitActivity.class);
        MainActivity.this.startActivityForResult(intent, 1);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        for (String perm : perms) {
            Log.d("MainActivity", perm + " granted");
        }
        launchScanproduitActivity();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        for (String perm : perms) {
            Log.d("MainActivity", perm + " not granted. It can crash!");
        }
    }

    private class OnFabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String[] perms = {Manifest.permission.CAMERA};
            if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {
                launchScanproduitActivity();
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.camera_rationale),RC_CAMERA, perms);
            }
        }
    }

}
