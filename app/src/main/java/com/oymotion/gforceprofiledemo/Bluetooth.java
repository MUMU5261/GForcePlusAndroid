package com.oymotion.gforceprofiledemo;

import java.util.ArrayList;
import java.util.List;

public class Bluetooth {
    private String device_name;
    private String mac_address;
    Bluetooth (String device_name,String mac_address){
        this.device_name = device_name;
        this.mac_address = mac_address;
    }
    public String getName() {
        return device_name;
    }
    public String getMacAddress() {
        return mac_address;
    }

    public boolean isSame(String mac_address) {
        if(this.mac_address.equals(mac_address)){
            return true;
        }
        return false;
    }


    // no need
    public boolean isExist(Bluetooth bluetooth, ArrayList<Bluetooth> btList){
        if (btList.contains(bluetooth)){
            return true;
        }
        return false;
    }
}
