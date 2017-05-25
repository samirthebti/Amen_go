package com.samirthebti.amen_go.Model;

import io.realm.RealmObject;

/**
 * Amen_Go
 * Created by Samir Thebti on 5/17/17.
 * thebtisam@gmail.com
 */

public class MyAddress extends RealmObject {
    private double logitude;
    private double laltitude;

    public MyAddress(double logitude, double laltitude) {
        this.logitude = logitude;
        this.laltitude = laltitude;
    }

    public MyAddress() {
    }

    public double getLogitude() {
        return logitude;
    }

    public void setLogitude(double logitude) {
        this.logitude = logitude;
    }

    public double getLaltitude() {
        return laltitude;
    }

    public void setLaltitude(double laltitude) {
        this.laltitude = laltitude;
    }

    @Override
    public String toString() {
        return "MyAddress{" +
                "logitude=" + logitude +
                ", laltitude=" + laltitude +
                '}';
    }
}
