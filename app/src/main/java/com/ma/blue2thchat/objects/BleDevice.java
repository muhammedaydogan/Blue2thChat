package com.ma.blue2thchat.objects;

public class BleDevice {

    private String name;
    private String macAddress;
    private int avatarNo;

    public BleDevice(String name, String macAddress, int avatarNo) {
        this.name = name;
        this.macAddress = macAddress;
        this.avatarNo = avatarNo;
    }

    public String getName() {
        return name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getAvatarNo() {
        return avatarNo;
    }
}
