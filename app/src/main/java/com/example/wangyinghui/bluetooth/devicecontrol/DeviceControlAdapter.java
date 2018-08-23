package com.example.wangyinghui.bluetooth.devicecontrol;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.wangyinghui.bluetooth.R;
import com.example.wangyinghui.bluetooth.bean.CharacteristicBean;
import java.util.ArrayList;

/**
 * Created by wangyinghui on 2018/8/23.
 */

public class DeviceControlAdapter extends RecyclerView.Adapter<DeviceControlAdapter.ServicesAndCharacterHolder> {
    private ArrayList<CharacteristicBean> mCharacteristicBeans = new ArrayList<>();

    @NonNull
    @Override
    public ServicesAndCharacterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_characteristic_item, viewGroup, false);
        ServicesAndCharacterHolder holder = new ServicesAndCharacterHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesAndCharacterHolder servicesAndCharacterHolder,
        int i) {
        CharacteristicBean bean = mCharacteristicBeans.get(i);
        servicesAndCharacterHolder.mTitleTv.setText(bean.getType() == CharacteristicBean.SERVICE ?
            R.string.service : R.string.characteristic);
        servicesAndCharacterHolder.setTitleTvStyle(bean.getType() == CharacteristicBean.SERVICE);
        servicesAndCharacterHolder.mUuidTv.setText(bean.getUuid());
    }

    @Override
    public int getItemCount() {
        return mCharacteristicBeans.size();
    }

    public class ServicesAndCharacterHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTv;
        public TextView mUuidTv;
        public ServicesAndCharacterHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mUuidTv = itemView.findViewById(R.id.uuid_tv);
        }

        public void setTitleTvStyle(boolean isBig) {
            if (isBig) {
                mTitleTv.setTextSize(18);
                mTitleTv.setTextColor(Color.rgb(0,0,0));
            } else {
                mTitleTv.setTextSize(14);
                mTitleTv.setTextColor(Color.rgb(100,100,100));
            }

        }
    }

    public void setCharacteristicBeans(ArrayList<CharacteristicBean> mCharacteristicBeans) {
        this.mCharacteristicBeans = mCharacteristicBeans;
    }
}
