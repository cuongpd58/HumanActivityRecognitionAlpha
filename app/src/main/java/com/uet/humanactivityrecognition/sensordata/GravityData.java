package com.uet.humanactivityrecognition.sensordata;

/**
 * Created by Cương Rùa on 10/01/2017.
 */

public class GravityData {

    private double x;
    private double y;
    private double z;

    public GravityData(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}