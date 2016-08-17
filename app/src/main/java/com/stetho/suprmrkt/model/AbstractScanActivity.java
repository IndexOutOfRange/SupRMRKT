package com.stetho.suprmrkt.model;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.stetho.suprmrkt.R;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import rx.subscriptions.CompositeSubscription;

public abstract class AbstractScanActivity extends AppCompatActivity implements ProcessScanInterface {

    // -------------------------------
    // CONSTANTS
    // --------------------------------
    protected static final int VIBRATION_DURATION = 300;
    protected static final long BULK_MODE_SCAN_DELAY_MS = 1000L;
    private static final int MULTIPLIEUR_TEMPS_VIBRATION_ERREUR = 3;

    // -------------------------------
    // ATTRIBUTES
    // --------------------------------
    protected BeepManager mBeepManager;
    protected CompositeSubscription mCompositeSubscription;

    private boolean mLightIsOn;
    private DecoratedBarcodeView barcodeView;

    // -------------------------------
    // VIEWS
    // -------------------------------
    protected View mBottomBar;
    protected Button mBtnBottom;
    protected View mViewInfoFakeActionBar;
    protected TextView mTextViewInfoTop;
    protected TextView mTextViewInfoBottom;
    private Toolbar mScanToolbar;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mCompositeSubscription = new CompositeSubscription();
        mBeepManager = new BeepManager(this);

        setContentView(R.layout.scan_base_activity_layout);

        initializeLayout();
        initializeListeners();
        setActionBarTitle(getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        mLightIsOn = false;
        barcodeView.pause();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    /**
     * Methode overridée pour ne pas avoir le menu de ZXing
     *
     * @param menu {@link Menu}
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // NOPMD
        return true;
    }

    /**
     * Methode overridée pour ne pas avoir le menu de ZXing
     *
     * @param item {@link MenuItem}
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void initializeLayout() {
        mScanToolbar = (Toolbar) findViewById(R.id.scan_toolbar);
        setSupportActionBar(mScanToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBottomBar = findViewById(R.id.bottombar);
        mBtnBottom = (Button) findViewById(R.id.btn_validateScan);
        mViewInfoFakeActionBar = findViewById(R.id.fakeactionbar_text_layout);
        mTextViewInfoTop = (TextView) findViewById(R.id.fakeactionbar_text_t1);
        mTextViewInfoBottom = (TextView) findViewById(R.id.fakeactionbar_text_t2);
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText(null);
    }

    private void initializeListeners() {
    }

    protected void setActionBarTitle(String title) {
        mScanToolbar.setTitle(title);
    }

    protected View getFakeActionBar() {
        return mScanToolbar;
    }

    protected View getBottomBar() {
        return mBottomBar;
    }


    @Override
    public void cabAlreadyScanned(String cabAlreadyScanned) {
        // vibration + son
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATION_DURATION);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.error); // NOPMD
        mediaPlayer.start();

        getFakeActionBar().setBackgroundResource(R.color.gris_bar_alpha75);
        getBottomBar().setBackgroundResource(R.color.gris_bar_alpha75);
//        FlurryAgent.logEvent(getString(R.string.flashage_cab_deja_scanne), false);
    }

    @Override
    public void cabRejete(String cabNonLaPoste) {
        // vibration + son
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATION_DURATION * MULTIPLIEUR_TEMPS_VIBRATION_ERREUR);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.error); //NOPMD
        mediaPlayer.start();

        // pack couleurs
        getFakeActionBar().setBackgroundResource(R.color.rouge_alpha75);
        getBottomBar().setBackgroundResource(R.color.rouge_alpha75);
//        FlurryAgent.logEvent(getString(R.string.flashage_cab_rejetee), false);
    }

    @Override
    public void cabSuccessfullyTraite(@NonNull String colis) {
        // vibration + son
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATION_DURATION);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beep); //NOPMD
        mediaPlayer.start();

        getFakeActionBar().setBackgroundResource(R.color.vert_alpha75);
        getBottomBar().setBackgroundResource(R.color.vert_alpha75);
//        FlurryAgent.logEvent(getString(R.string.flashage_cab_succes), false);
    }

    //-------------------------------
    // ABSTRACT METHODS
    //-------------------------------

    protected abstract void processBarCode(String cab);

    // -------------------------------
    // INNER CLASS
    // --------------------------------
    private final class OnToggleFlashClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            mLightIsOn ^= true;
            if (mLightIsOn) {
                barcodeView.setTorchOn();
            } else {
                barcodeView.setTorchOff();
            }
        }
    }

    private final class OnHomeClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                processBarCode(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<com.google.zxing.ResultPoint> resultPoints) {

        }

    };
}
