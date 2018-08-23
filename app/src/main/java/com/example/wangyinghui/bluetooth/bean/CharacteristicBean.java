package com.example.wangyinghui.bluetooth.bean;

/**
 * Created by wangyinghui on 2018/8/23.
 */

public class CharacteristicBean {
    public static final int SERVICE = 1;
    public static final int CHARACTERISTIC = 2;

    private String uuid;
    private int type;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUuid() {

        return uuid;
    }

    public int getType() {
        return type;
    }

}
