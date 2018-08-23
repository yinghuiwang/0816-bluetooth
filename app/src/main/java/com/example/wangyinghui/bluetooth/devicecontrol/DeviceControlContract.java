package com.example.wangyinghui.bluetooth.devicecontrol;

import android.app.Activity;
import com.example.wangyinghui.bluetooth.bean.CharacteristicBean;
import java.util.ArrayList;

/**
 * Created by wangyinghui on 2018/8/20.
 */

public interface DeviceControlContract {
    interface iDeviceControlView {
        void setPresenter(iDeviceControlPresenter presenter);
        Activity getActivity();
        void setConnectionStatus(boolean isConnect);
        void showRefreshBtn(boolean show);
        void showProgressBar(boolean show);
        void notifyDataSetChanged(ArrayList<CharacteristicBean> characteristicBeans);
    }

    interface iDeviceControlPresenter {
        void connect(final String address);
        void disconnect();

        void registerReceiver();
        void unregisterReceiver();
        void destroy();
        void start();
        boolean isConnected();
    }
}
