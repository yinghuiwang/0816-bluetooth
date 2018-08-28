package com.example.wangyinghui.bluetooth.devicescan;

import android.app.Activity;
import java.util.List;

/**
 * Created by wangyinghui on 2018/8/20.
 */

public interface DeviceScanContract {
    interface iDeviceScanView {
        void setPresenter(iDeviceScanPresenter presenter);

        Activity getActivity();

        void showToast(int id);
        void notifyDataSetChanged(List<String> list);

        void showRefreshBtn(boolean show);
        void showProgressBar(boolean show);
        String getRssi();
        String getTimeInterval();
        Boolean getIsAutoScan();
    }

    interface iDeviceScanPresenter {

        void start();

        void startScan();
        void stopScan();
        void stopAutoScan();

        void jumpPage(int position);
    }
}
