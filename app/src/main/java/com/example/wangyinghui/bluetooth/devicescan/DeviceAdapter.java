package com.example.wangyinghui.bluetooth.devicescan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.wangyinghui.bluetooth.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyinghui on 2018/8/20.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder>
    implements View.OnClickListener {

    private List<String> mList = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    public DeviceAdapter() {

    }
    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_device_item, viewGroup, false);
        DeviceHolder deviceHolder = new DeviceHolder(view);
        view.setOnClickListener(this);
        return deviceHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder deviceHolder, int i) {
        deviceHolder.mDeviceInfoTv.setText(mList.get(i));
        deviceHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick((Integer) v.getTag());
        }
    }

    public class DeviceHolder extends RecyclerView.ViewHolder {
        private TextView mDeviceInfoTv;

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
            mDeviceInfoTv = itemView.findViewById(R.id.device_info_tv);
        }
    }

    public void setList(List<String> mList) {
        this.mList = mList;
    }
    public List<String> getList() {
        return mList;
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
