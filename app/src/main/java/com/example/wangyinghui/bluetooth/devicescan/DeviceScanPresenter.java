package com.example.wangyinghui.bluetooth.devicescan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wangyinghui.bluetooth.R;
import com.example.wangyinghui.bluetooth.bean.DeviceBean;
import com.example.wangyinghui.bluetooth.devicecontrol.DeviceControlActivity;

/**
 * Created by wangyinghui on 2018/8/20.
 */

public class DeviceScanPresenter implements DeviceScanContract.iDeviceScanPresenter {
    public static final String TAG = "DeviceScanPresenter";
    public static final int REQUEST_ENABLE_BT = 1;
    private DeviceScanContract.iDeviceScanView mDeviceScanView;
    private DeviceScanModel mDeviceScanModel = new DeviceScanModel();

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Button mRefreshBtn;
    private TextView mContentTv;
    private String mContent = "";
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;

    private Handler mAutoScanHandler;
    private boolean mStopAutoScan;

    public DeviceScanPresenter(DeviceScanContract.iDeviceScanView view) {
        mDeviceScanView = view;
        mDeviceScanView.setPresenter(this);
    }

    @Override
    public void start() {

        mHandler = new Handler();

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!mDeviceScanView.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mDeviceScanView.showToast(R.string.ble_not_supported);
            mDeviceScanView.getActivity().finish();
        }

        final BluetoothManager bluetoothManager =
            (BluetoothManager) mDeviceScanView.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        startScan();
    }

    @Override
    public void startScan() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mDeviceScanView.getActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (mDeviceScanView.getIsAutoScan()) {
            mStopAutoScan = false;
            mAutoScanHandler= new Handler();
            scanLeDevice(true);
            String timeIntervalStr = mDeviceScanView.getTimeInterval();
            final int timeInterval = Math.abs(Integer.valueOf(timeIntervalStr).intValue()) * 1000;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    //do something
                    //每隔1s循环执行run方法
                    if (!mStopAutoScan) {
                        mAutoScanHandler.postDelayed(this, timeInterval);
                        scanLeDevice(true);
                    }
                }
            };
            mHandler.postDelayed(r, timeInterval);
        } else {
            scanLeDevice(true);
        }

    }

    @Override
    public void stopScan() {
        scanLeDevice(false);
    }

    public void checkThreshold() {
        int rssi = 0 - Math.abs(Integer.valueOf(mDeviceScanView.getRssi()));
        DeviceBean[] deviceBeans = mDeviceScanModel.getDeviceBeans();
        if (deviceBeans != null && deviceBeans.length > 0) {
            DeviceBean deviceBean = deviceBeans[0];
            if (deviceBean.getRssi() > rssi) {
                stopAutoScan();
                jumpPage(0);
            }
        }
    }

    @Override
    public void stopAutoScan() {
        mStopAutoScan = true;
    }

    @Override
    public void jumpPage(int position) {
        DeviceBean deviceBean = mDeviceScanModel.getDeviceBeans()[position];
        final BluetoothDevice device = deviceBean.getDevice();
        if (device == null) return;
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning =false;
        }

        final Intent intent = new Intent(mDeviceScanView.getActivity(), DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        mDeviceScanView.getActivity().startActivity(intent);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi, byte[] bytes) {
            mDeviceScanView.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bluetoothDevice.getName() != null) {
                        Log.d(TAG, "{-----------------------------------------------------------\n");
                        Log.d(TAG, "Device name:" + bluetoothDevice.getName() + "\n");
                        Log.d(TAG, "    address:" + bluetoothDevice.getAddress() + "\n");
                        Log.d(TAG, "       rssi:" + String.valueOf(rssi) + "\n");
                        Log.d(TAG, "------------------------------------------------------------}\n");
                        DeviceBean deviceBean = new DeviceBean(bluetoothDevice);
                        deviceBean.setRssi(rssi);
                        mDeviceScanModel.addDeviceBean(deviceBean);
                    }
                }
            });
        }
    };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            //在预定义的扫描周期后停止扫描。
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    changeScanState();
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    checkThreshold();
                    mDeviceScanView.notifyDataSetChanged(mDeviceScanModel.getDevices());
                }
            }, SCAN_PERIOD);

            mScanning = true;
            changeScanState();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            changeScanState();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private void changeScanState() {
        if (mScanning) {
            mDeviceScanView.showProgressBar(true);
            mDeviceScanView.showRefreshBtn(false);
        } else {
            mDeviceScanView.showProgressBar(false);
            mDeviceScanView.showRefreshBtn(true);
        }
    }
}
