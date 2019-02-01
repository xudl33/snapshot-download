package com.wisea.elasticsearch.plugin;

import com.wisea.elasticsearch.plugin.entity.BackInfo;

public class BackInfoTest {
    public static void main(String[] args) {
        BackInfo in = new BackInfo();
        in.decode("eyJyIjoiV1AxaUZuTXpRd0NCNFpQSHlUZXhnUSNiYW5rIiwicyI6IndxTzlHdG1MVHdldjdfOEhWUk9jNEEjc25hcHNob3QxIn0=");
        System.out.println(in.getR() + " " + in.getS());
        
        BackInfo backInfo = new BackInfo("eyJyIjoiV1AxaUZuTXpRd0NCNFpQSHlUZXhnUSNiYW5rIiwicyI6IndxTzlHdG1MVHdldjdfOEhWUk9jNEEjc25hcHNob3QxIn0=.zip");
        System.out.println(backInfo.getR() + " " + backInfo.getS());
    }
}
