package com.uet.humanactivityrecognition.sensordata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

/**
 * Created by Cương Rùa on 16/01/2017.
 */

public class AccelerometerDataCollector implements SensorEventListener {
    private ArrayList<SimpleSensorData> accelerometerDatas;
    private boolean hasGravitySensor;
    private boolean hasMadSensor;

    public AccelerometerDataCollector(){
        accelerometerDatas = new ArrayList<>();

    }

    public AccelerometerDataCollector(ArrayList<SimpleSensorData> accelerometerDatas) {
        this.accelerometerDatas = accelerometerDatas;
    }

    public ArrayList<SimpleSensorData> getAccelerometerDatas() {
        return accelerometerDatas;
    }

    public void setAccelerometerDatas(ArrayList<SimpleSensorData> accelerometerDatas) {
        this.accelerometerDatas = accelerometerDatas;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accelerometerDatas.add(new SimpleSensorData(System.currentTimeMillis(),
                event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void resetData() {
        accelerometerDatas.clear();
    }
}
