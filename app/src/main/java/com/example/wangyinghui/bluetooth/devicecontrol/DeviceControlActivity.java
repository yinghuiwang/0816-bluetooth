package com.example.wangyinghui.bluetooth.devicecontrol;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.wangyinghui.bluetooth.R;
import com.example.wangyinghui.bluetooth.bean.CharacteristicBean;
import com.example.wangyinghui.bluetooth.devicescan.DeviceAdapter;
import java.util.ArrayList;

public class DeviceControlActivity extends AppCompatActivity implements DeviceControlContract.iDeviceControlView,
    View.OnClickListener {
    private static final String TAG = DeviceControlActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private DeviceControlContract.iDeviceControlPresenter mPresenter;
    private String mDeviceName;
    private String mDeviceAddress;

    private TextView mDeviceNameTv;
    private TextView mAddressTv;
    private TextView mStatusTv;
    private TextView mContentTv;
    private Button mConnectBtn;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        new DeviceControlPresenter(this, mDeviceAddress);
        mPresenter.start();

        mDeviceNameTv = findViewById(R.id.name_tv);
        mAddressTv = findViewById(R.id.address_tv);
        mStatusTv = findViewById(R.id.connect_status_tv);
        mContentTv = findViewById(R.id.content_tv);
        mConnectBtn = findViewById(R.id.connect_btn);
        mProgressBar = findViewById(R.id.progressBar2);
        mDeviceNameTv.setText("设备名称：" + mDeviceName);
        mAddressTv.setText("MAC地址：" + mDeviceAddress);
        mRecyclerView = findViewById(R.id.characters_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new DeviceControlAdapter());

        mConnectBtn.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.registerReceiver();
        //mPresenter.connect(mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unregisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void setPresenter(DeviceControlContract.iDeviceControlPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setConnectionStatus(boolean isConnect) {
        if (isConnect) {
            mStatusTv.setText(R.string.connected);
            mConnectBtn.setText(R.string.disconnected);
        } else {
            mStatusTv.setText(R.string.disconnected);
            mConnectBtn.setText(R.string.connected);
        }
    }

    @Override
    public void onClick(View v) {
        if (!mPresenter.isConnected()) {
            mPresenter.connect(mDeviceAddress);
        } else {
            mPresenter.disconnect();
        }
    }

    @Override
    public void showRefreshBtn(boolean show) {
        mConnectBtn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void notifyDataSetChanged(ArrayList<CharacteristicBean> characteristicBeans) {
        DeviceControlAdapter adapter = (DeviceControlAdapter) mRecyclerView.getAdapter();
        adapter.setCharacteristicBeans(characteristicBeans);
        adapter.notifyDataSetChanged();
    }
}
