package com.example.wangyinghui.bluetooth.devicescan;

import com.example.wangyinghui.bluetooth.bean.DeviceBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangyinghui on 2018/8/20.
 */

public class DeviceScanModel {

    private HashMap<String, DeviceBean> mDeviceBeanHashMap = new HashMap<>();
    private DeviceBean[] mDeviceBeans;

    public void addDeviceBean(DeviceBean bean) {
        mDeviceBeanHashMap.put(bean.getMacAddress(), bean);
    }

    public List<String> getDevices() {
        ArrayList<String> devices = new ArrayList<>();
        DeviceBean[] deviceBeans = new DeviceBean[mDeviceBeanHashMap.size()];
        mDeviceBeanHashMap.values().toArray(deviceBeans);
        Arrays.sort(deviceBeans, new Comparator<DeviceBean>() {
            @Override
            public int compare(DeviceBean t2, DeviceBean t1) {
                if (t1.getRssi() > t2.getRssi()) {
                    return 1;
                }

                if (t1.getRssi() < t2.getRssi()) {
                    return -1;
                }

                return 0;
            }
        });

        mDeviceBeans = deviceBeans;

        

        for (DeviceBean deviceBean: deviceBeans) {
            devices.add(
                "Device name: " + deviceBean.getDeviceName() + "\n" +
                "       address: " + deviceBean.getMacAddress() + "\n" +
                "              rssi: " + String.valueOf(deviceBean.getRssi()) + "\n" +
                "配对状态：" + deviceBean.getLinkState()
            );
        }
        return devices;
    }

    public DeviceBean[] getDeviceBeans() {
        return mDeviceBeans;
    }

}
