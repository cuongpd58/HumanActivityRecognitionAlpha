package com.uet.humanactivityrecognition.activities;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.uet.humanactivityrecognition.R;
import com.uet.humanactivityrecognition.constants.Constants;
import com.uet.humanactivityrecognition.sensordata.AccelerometerDataCollector;
import com.uet.humanactivityrecognition.sensordata.GravityDataCollector;
import com.uet.humanactivityrecognition.sensordata.GyroscopeDataCollector;
import com.uet.humanactivityrecognition.writers.SensorDataWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    //Collectors
    private AccelerometerDataCollector mAccelCollector;
    private GravityDataCollector mGravCollector;
    private GyroscopeDataCollector mGyrosCollector;

    //Views
    private Spinner spActivities;
    private Spinner spSexual;
    private Spinner spSensors;
    private Spinner spMode;
    private EditText edtEnterName;
    private EditText edtAge;
    private EditText edtJob;
    private ToggleButton tgbtnCollectData;

    private SensorManager sensorMgr;

    private SensorDataWriter CSVWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initComps();
    }

    private void initComps() {
        sensorMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);

        mAccelCollector = new AccelerometerDataCollector();
        mGravCollector = new GravityDataCollector();
        mGyrosCollector = new GyroscopeDataCollector();
    }

    private void initViews() {

        spinnerSettings(spActivities,R.id.sp_activities, R.array.activities_array);
        spinnerSettings(spMode,R.id.sp_mode,R.array.mode_array);
        spinnerSettings(spSensors, R.id.sp_sensors, R.array.sensor_array);
        spinnerSettings(spSexual, R.id.sp_sex, R.array.sex_array);

        edtEnterName = (EditText)findViewById(R.id.edt_enter_name);
        edtAge = (EditText)findViewById(R.id.edt_age);
        edtJob = (EditText)findViewById(R.id.edt_job);

        tgbtnCollectData = (ToggleButton)findViewById(R.id.tgbtn_start_stop);
        tgbtnCollectData.setOnCheckedChangeListener(this);
    }

    private void spinnerSettings(Spinner spinner, int id, int array) {
        spinner = (Spinner)findViewById(id);
        spActivities = (Spinner)findViewById(R.id.sp_activities);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String)parent.getItemAtPosition(position);
        Toast.makeText(this,item,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            Toast.makeText(this,"Start Collect Data", Toast.LENGTH_SHORT).show();
            startCollectData();
        }
        else {
            Toast.makeText(this,"Stop Collect Data", Toast.LENGTH_SHORT).show();
            stopCollectData();
        }
    }

    private void stopCollectData() {
        sensorMgr.unregisterListener(mAccelCollector,sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorMgr.unregisterListener(mGravCollector,sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY));
        sensorMgr.unregisterListener(mGyrosCollector,sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));

        Log.e("Accel", mAccelCollector.getAccelerometerDatas().size() + "");

        //Accelerometer
        String time = sdf.format(new Date());
        String csvName = edtEnterName.getText().toString() + "_" + spActivities.getSelectedItem().toString()
                + "_Accelerometer_" +time + Constants.PATH.CSV_FORMAT;

        CSVWriter = new SensorDataWriter(mAccelCollector.getAccelerometerDatas(), csvName);
        CSVWriter.write();

        //Gravity
        CSVWriter.setDatas(mGravCollector.getGravityDatas());
        csvName = edtEnterName.getText().toString() + "_" + spActivities.getSelectedItem().toString()
                + "_Gravity_" + time + Constants.PATH.CSV_FORMAT;
        CSVWriter.setFileName(csvName);
        CSVWriter.write();

        //Gyroscope
        CSVWriter.setDatas(mGyrosCollector.getGyroscopeDatas());
        csvName = edtEnterName.getText().toString() + "_" + spActivities.getSelectedItem().toString()
                + "_Gyroscope_" + time + Constants.PATH.CSV_FORMAT;
        CSVWriter.setFileName(csvName);
        CSVWriter.write();

        mAccelCollector.resetData();
        mGravCollector.resetData();
        mGyrosCollector.resetData();
    }

    private void startCollectData() {
        sensorMgr.registerListener(mAccelCollector,sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorMgr.SENSOR_DELAY_FASTEST);
        sensorMgr.registerListener(mGravCollector,sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY),
                sensorMgr.SENSOR_DELAY_FASTEST);
        sensorMgr.registerListener(mGyrosCollector,sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                sensorMgr.SENSOR_DELAY_FASTEST);
    }
}
