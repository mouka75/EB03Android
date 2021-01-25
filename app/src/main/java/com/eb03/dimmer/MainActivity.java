package com.eb03.dimmer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circletd.CircleSeekBar;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    private final static int BT_CONNECT_CODE = 1;
    private final static int PERMISSIONS_REQUEST_CODE = 0;
    private final static String[] BT_DANGEROUS_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private TextView mStatus;

    private static final String TAG = "TAG";
    private CircleSeekBar mSlider;
    private TextView mTv;
    private int mPourcentage;

    private boolean connected = false ;
    private onConnectListener connectListener;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = findViewById(R.id.textView);
        mSlider = findViewById(R.id.CircleSeekBar);

        setConnectListener(connexion -> {
            connected = connexion ;

            if(connected) {
                mSlider.setmCircleListener(pourcentage -> {
                    mTv.setText(String.format("%d %c", pourcentage, '%'));
                    mPourcentage = pourcentage;
                });

            }
        } );


        mStatus = findViewById(R.id.status);
        if (savedInstanceState != null) {
            mPourcentage = savedInstanceState.getInt("pourcentage");
            mTv.setText(String.format("%d %c", mPourcentage, '%'));
        }




        verifyBtRights();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItem = item.getItemId();
        switch(menuItem){
            case R.id.connect:
                Intent BTConnect;
                BTConnect = new Intent(this,BTConnectActivity.class);
                startActivityForResult(BTConnect,BT_CONNECT_CODE);
        }
        return true;
    }



    private void verifyBtRights(){
        if(BluetoothAdapter.getDefaultAdapter() == null){
            Toast.makeText(this,"Cette application nécessite un adaptateur BT",Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if(SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
                requestPermissions(BT_DANGEROUS_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this,"Les autorisations BT sont requises pour utiliser l'application",Toast.LENGTH_LONG).show();
                finish();
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BT_CONNECT_CODE:
                connectListener.onChange(true);

                if (resultCode == RESULT_OK) {

                    String address = data.getStringExtra("device");
                    mStatus.setText(address);

                    OscilloManager oscilloManager = new OscilloManager() ;
                    oscilloManager.attachTransceiver(address);
                }

                break;
            default:
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pourcentage", mPourcentage);
    }

    public interface onConnectListener {
        void onChange(boolean connexion);
    }

    public void setConnectListener(onConnectListener connectListener) {
        this.connectListener = connectListener;
    }
}



