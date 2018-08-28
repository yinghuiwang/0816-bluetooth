package com.example.wangyinghui.bluetooth.devicescan;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wangyinghui.bluetooth.R;
import com.example.wangyinghui.bluetooth.bean.DeviceBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceScanActivity extends AppCompatActivity implements DeviceScanContract.iDeviceScanView, View.OnClickListener,
    DeviceAdapter.OnItemClickListener {
    private DeviceScanContract.iDeviceScanPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private Button mRefeshBtn;
    private ProgressBar mProgressBar;
    private EditText mTimeIntervalEt;
    private EditText mRSSIEt;
    private Switch mAutoSelectSwitch;

    private Boolean mIsAutoScan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        DeviceAdapter adapter = new DeviceAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setItemClickListener(this);
        mRefeshBtn = findViewById(R.id.refresh_btn);
        mRefeshBtn.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progressBar2);
        mTimeIntervalEt = findViewById(R.id.time_interval_et);
        mRSSIEt = findViewById(R.id.rssi_et);
        mAutoSelectSwitch = findViewById(R.id.auto_select_switch);

        mAutoSelectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsAutoScan = isChecked;
                if (!mIsAutoScan) {
                    mPresenter.stopAutoScan();
                }
            }
        });

        // 出事化presenter
        new  DeviceScanPresenter(this);
        mPresenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stopScan();
    }

    public void notifyDataSetChanged(List<String> list) {
        DeviceAdapter adapter = (DeviceAdapter) mRecyclerView.getAdapter();
        adapter.setList(list);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showRefreshBtn(boolean show) {
        mRefeshBtn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPresenter(DeviceScanContract.iDeviceScanPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.refresh_btn) {
            mPresenter.startScan();
        }
    }

    @Override
    public void onItemClick(int position) {
        mPresenter.jumpPage(position);
    }

    @Override
    public String getRssi() {
        return mRSSIEt.getText().toString();
    }

    @Override
    public String getTimeInterval() {
        return mTimeIntervalEt.getText().toString();
    }

    @Override
    public Boolean getIsAutoScan() {
        return mIsAutoScan;
    }
}
