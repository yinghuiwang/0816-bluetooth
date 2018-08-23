package com.example.wangyinghui.bluetooth.devicecontrol;

import com.example.wangyinghui.bluetooth.bean.CharacteristicBean;
import com.example.wangyinghui.bluetooth.bean.DeviceBean;
import java.util.ArrayList;

/**
 * Created by wangyinghui on 2018/8/23.
 */

public class DeviceControlModel {
    private ArrayList<CharacteristicBean> mCharacteristicBeans;

    public void addCharacteristic(String uuid, int type) {
        if (mCharacteristicBeans == null) {
            mCharacteristicBeans = new ArrayList<>();
        }
        CharacteristicBean characteristicBean = new CharacteristicBean();
        characteristicBean.setUuid(uuid);
        characteristicBean.setType(type);
        mCharacteristicBeans.add(characteristicBean);
    }

    public void clearData() {
        if (mCharacteristicBeans != null) {
            mCharacteristicBeans.clear();
        }
    }

    public ArrayList<CharacteristicBean> getCharacteristicBeans() {
        return mCharacteristicBeans;
    }

}
