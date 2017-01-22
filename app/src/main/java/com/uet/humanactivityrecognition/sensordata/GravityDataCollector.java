package com.uet.humanactivityrecognition.sensordata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

/**
 * Created by Cương Rùa on 16/01/2017.
 */

public class GravityDataCollector implements SensorEventListener {

    private ArrayList<SimpleSensorData> gravityDatas;

    public  GravityDataCollector(){
        gravityDatas = new ArrayList<>();
    }

    public ArrayList<SimpleSensorData> getGravityDatas() {
        return gravityDatas;
    }

    public void setGravityDatas(ArrayList<SimpleSensorData> gravityDatas) {
        this.gravityDatas = gravityDatas;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gravityDatas.add(new SimpleSensorData(System.currentTimeMillis(),
                event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void resetData() {
        gravityDatas.clear();
    }
}
