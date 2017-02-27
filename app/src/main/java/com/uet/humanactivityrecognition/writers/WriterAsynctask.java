package com.uet.humanactivityrecognition.writers;

import android.os.AsyncTask;

import com.uet.humanactivityrecognition.sensordata.SimpleSensorData;

import java.util.ArrayList;

/**
 * Project name: HumanActivityRecognition
 * Created by Cuong Phan
 * Email: cuongphank58@gmail.com
 * on 27/02/2017.
 * University of Engineering and Technology,
 * Vietnam National University.
 */

public class WriterAsynctask extends AsyncTask<String, Void, Void> {
    private ArrayList<SimpleSensorData> datas;

    public WriterAsynctask(ArrayList<SimpleSensorData> datas){
        this.datas = datas;
    }
    @Override
    protected Void doInBackground(String... params) {
        return null;
    }
}
