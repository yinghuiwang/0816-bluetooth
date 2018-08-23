package com.example.wangyinghui.bluetooth.devicecontrol;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.example.wangyinghui.bluetooth.R;
import com.example.wangyinghui.bluetooth.bean.CharacteristicBean;
import com.example.wangyinghui.bluetooth.service.BluetoothLeService;
import java.util.List;

/**
 * Created by wangyinghui on 2018/8/20.
 */

public class DeviceControlPresenter implements DeviceControlContract.iDeviceControlPresenter {
    private static final String TAG = DeviceControlPresenter.class.getSimpleName();
    private DeviceControlContract.iDeviceControlView mDeviceControlView;
    private DeviceControlModel mDeviceControlModel = new DeviceControlModel();
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    private boolean mConnected;

    public DeviceControlPresenter(DeviceControlContract.iDeviceControlView iDeviceControlView, String deviceAddress) {
        mDeviceControlView = iDeviceControlView;
        mDeviceControlView.setPresenter(this);
        mDeviceAddress = deviceAddress;
    }

    public void start() {
        Intent gattServiceIntent = new Intent(mDeviceControlView.getActivity(), BluetoothLeService.class);
        boolean bll = mDeviceControlView.getActivity().bindService(gattServiceIntent, mServiceConnection,
            mDeviceControlView.getActivity().BIND_AUTO_CREATE);
        if (bll) {
            System.out.println("---------------");
        } else {
            System.out.println("===============");
        }
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
            IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                mDeviceControlView.getActivity().finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        mDeviceControlModel.clearData();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            mDeviceControlModel.addCharacteristic(uuid, CharacteristicBean.SERVICE);
            Log.d(TAG, "displayGattServices: "+uuid);
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                mDeviceControlModel.addCharacteristic(uuid, CharacteristicBean.CHARACTERISTIC);
                //if (uuid.contains("fff4")) {
                //    Log.e("console", "2gatt Characteristic: " + uuid);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    mBluetoothLeService.readCharacteristic(gattCharacteristic);
                //}
            }
        }
        mDeviceControlView.notifyDataSetChanged(mDeviceControlModel.getCharacteristicBeans());
    }

    /**
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
            .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        return intentFilter;
    }

    public void connect(String mDeviceAddress) {
        connectStatus(true);
        mBluetoothLeService.connect(mDeviceAddress);
    }

    @Override
    public void disconnect() {
        connectStatus(true);
        mBluetoothLeService.disconnect();
    }

    @Override
    public void registerReceiver() {
        mDeviceControlView.getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public void unregisterReceiver() {
        mDeviceControlView.getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void destroy() {
        mBluetoothLeService.close();
        mDeviceControlView.getActivity().unbindService(mServiceConnection);
        mBluetoothLeService = null;

    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "已连接");
                Log.d(TAG, "已连接" + Thread.currentThread().toString());
                mConnected = true;
                connectStatus(false);
                updateConnectionState();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "断开连接");
                Log.d(TAG, "断开连接" + Thread.currentThread().toString());
                mConnected = false;
                connectStatus(false);
                updateConnectionState();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void updateConnectionState() {
        mDeviceControlView.setConnectionStatus(mConnected);
    }

    StringBuffer sb = new StringBuffer();
    private void displayData(String data) {
        sb.append(data);
        Log.d(TAG, "displayData: "+sb.toString());
    }

    private void connectStatus(boolean isConnecting) {
        if (isConnecting) {
            mDeviceControlView.showProgressBar(true);
            mDeviceControlView.showRefreshBtn(false);
        } else {
            mDeviceControlView.showProgressBar(false);
            mDeviceControlView.showRefreshBtn(true);
        }
    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }
}
