package com.example.wangyinghui.bluetooth.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by wangyinghui on 2018/8/20.
 */

public class DeviceBean {
    private int rssi;
    private String deviceName;
    private String macAddress;
    private int bondState;

    private BluetoothDevice mDevice;

    public DeviceBean(BluetoothDevice device) {
        mDevice = device;
        deviceName = mDevice.getName();
        macAddress = mDevice.getAddress();
        bondState = mDevice.getBondState();
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    public int getBondState() {
        return bondState;
    }

    public String getLinkState() {
        switch (bondState) {
            case BluetoothDevice.BOND_NONE:
                return "无配对";
            case BluetoothDevice.BOND_BONDING:
                return "配对中";
            case BluetoothDevice.BOND_BONDED:
                return "已配对";
        }
        return "";
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
