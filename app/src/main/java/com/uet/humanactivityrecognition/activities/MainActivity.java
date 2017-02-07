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
        spActivities = (Spinner)findViewById(R.id.sp_activities);
        spMode = (Spinner)findViewById(R.id.sp_mode);
        spSexual = (Spinner)findViewById(R.id.sp_sex);
        spSensors = (Spinner)findViewById(R.id.sp_sensors);

        spinnerSettings(spActivities, R.array.activities_array);
        spinnerSettings(spMode,R.array.mode_array);
        spinnerSettings(spSensors, R.array.sensor_array);
        spinnerSettings(spSexual, R.array.sex_array);

        edtEnterName = (EditText)findViewById(R.id.edt_enter_name);
        edtAge = (EditText)findViewById(R.id.edt_age);
        edtJob = (EditText)findViewById(R.id.edt_job);

        tgbtnCollectData = (ToggleButton)findViewById(R.id.tgbtn_start_stop);
        tgbtnCollectData.setOnCheckedChangeListener(this);
    }

    private void spinnerSettings(Spinner spinner, int array) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
        String sensor = (String)spSensors.getSelectedItem();
        String time = sdf.format(new Date());
        String csvName = null;
        switch (sensor){
            case Constants.SENSORS.ACCELEROMETER:
                sensorMgr.unregisterListener(mAccelCollector,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                //Accelerometer
                csvName = edtEnterName.getText().toString() + "_" + spActivities.getSelectedItem().toString()
                        + "_Accelerometer_" +time + Constants.PATH.CSV_FORMAT;

                CSVWriter = new SensorDataWriter(mAccelCollector.getAccelerometerDatas(), csvName);
                CSVWriter.write();
                mAccelCollector.resetData();
                break;
            case Constants.SENSORS.GRAVITY:
                sensorMgr.unregisterListener(mGravCollector,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY));
                //Gravity
                csvName = edtEnterName.getText().toString() + "_" + spActivities.getSelectedItem().toString()
                        + "_Gravity_" +time + Constants.PATH.CSV_FORMAT;

                CSVWriter = new SensorDataWriter(mGravCollector.getGravityDatas(), csvName);
                CSVWriter.write();
                mGravCollector.resetData();
                break;
            case Constants.SENSORS.GYROSCOPE:
                sensorMgr.unregisterListener(mGyrosCollector,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
                //Gyroscope
                csvName = edtEnterName.getText().toString() + "_" + spActivities.getSelectedItem().toString()
                        + "_Accelerometer_" +time + Constants.PATH.CSV_FORMAT;

                CSVWriter = new SensorDataWriter(mGyrosCollector.getGyroscopeDatas(), csvName);
                CSVWriter.write();
                mGyrosCollector.resetData();
                break;
            default:
                break;
        }
    }


    private void startCollectData() {
        String sensor = (String)spSensors.getSelectedItem();
        String mode = (String)spMode.getSelectedItem();
        Log.e("start", sensor + ";" + mode);
        int samplingPeriodUs = 0;
        switch (mode){
            case Constants.SAMPLING_PERIOD_US.DELAY_FASTEST:
                samplingPeriodUs = sensorMgr.SENSOR_DELAY_FASTEST;
                break;
            case Constants.SAMPLING_PERIOD_US.DELAY_GAME:
                samplingPeriodUs = sensorMgr.SENSOR_DELAY_GAME;
                break;
            case Constants.SAMPLING_PERIOD_US.DELAY_NORMAL:
                samplingPeriodUs = sensorMgr.SENSOR_DELAY_NORMAL;
                break;
            default:
                samplingPeriodUs = sensorMgr.SENSOR_DELAY_GAME;
                break;
        }
        switch (sensor){
            case Constants.SENSORS.ACCELEROMETER:
                sensorMgr.registerListener(mAccelCollector,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        samplingPeriodUs);
                break;
            case Constants.SENSORS.GRAVITY:
                sensorMgr.registerListener(mGravCollector,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY),
                        samplingPeriodUs);
                break;
            case Constants.SENSORS.GYROSCOPE:
                sensorMgr.registerListener(mGyrosCollector,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        samplingPeriodUs);
                break;
            default:
                break;
        }
    }
}
