package com.uet.humanactivityrecognition.sensordata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

/**
 * Project name: HumanActivityRecognition
 * Created by Cuong Phan
 * Email: cuongphank58@gmail.com
 * on 16/01/2017.
 * University of Engineering and Technology,
 * Vietnam National University.
 */

public class GyroscopeDataCollector implements SensorEventListener {

    private ArrayList<SimpleSensorData> gyroscopeDatas;

    public GyroscopeDataCollector(){
        gyroscopeDatas = new ArrayList<>();
    }

    public ArrayList<SimpleSensorData> getGyroscopeDatas() {
        return gyroscopeDatas;
    }

    public void setGyroscopeDatas(ArrayList<SimpleSensorData> gyroscopeDatas) {
        this.gyroscopeDatas = gyroscopeDatas;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gyroscopeDatas.add(new SimpleSensorData(System.currentTimeMillis(),
                event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void resetData() {
        gyroscopeDatas.clear();
    }
}
