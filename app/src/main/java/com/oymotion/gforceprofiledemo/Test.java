package com.oymotion.gforceprofiledemo;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        Bluetooth b1 = new Bluetooth("name1", "address1");
        Bluetooth b2 = new Bluetooth("name1", "address1");

        ArrayList<Bluetooth> blist = new ArrayList<Bluetooth>();
        blist.add(b1);

    }
}
