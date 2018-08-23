package com.example.wangyinghui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "wyh";
    public static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Button mRefreshBtn;
    private TextView mContentTv;
    private String mContent = "";

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "{-----------------------------------------------------------\n");
                    Log.d(TAG, "Device name:" + bluetoothDevice.getName() + "\n");
                    Log.d(TAG, "    address:" + bluetoothDevice.getAddress() + "\n");
                    Log.d(TAG, "       rssi:" + String.valueOf(rssi) + "\n");
                    Log.d(TAG, "------------------------------------------------------------}\n");

                    if (bluetoothDevice.getName() != null) {
                        mContent = mContent +
                            "{-----------------------------------------------------------\n" +
                            "Device name:" + bluetoothDevice.getName() + "\n" +
                            "    address:" + bluetoothDevice.getAddress() + "\n" +
                            "       rssi:" + String.valueOf(rssi) + "\n" +
                            "------------------------------------------------------------}\n";
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefreshBtn = findViewById(R.id.refresh_btn);
        mContentTv = findViewById(R.id.content_tv);

        mContentTv.setMovementMethod(new ScrollingMovementMethod(){

        });
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                scanLeDevice(true);
            }
        });

        mHandler = new Handler();

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            finish();
        }

        final BluetoothManager bluetoothManager =
            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            //在预定义的扫描周期后停止扫描。
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mContentTv.setText(mContent);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}
