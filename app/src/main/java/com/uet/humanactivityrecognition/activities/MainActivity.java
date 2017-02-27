package com.uet.humanactivityrecognition.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.uet.humanactivityrecognition.R;
import com.uet.humanactivityrecognition.adapters.UsersAdapter;
import com.uet.humanactivityrecognition.constants.Constants;
import com.uet.humanactivityrecognition.dialogs.NewUserDialog;
import com.uet.humanactivityrecognition.sensordata.SimpleSensorData;
import com.uet.humanactivityrecognition.userdata.UserData;
import com.uet.humanactivityrecognition.writers.SensorDataWriter;
import com.uet.humanactivityrecognition.writers.UserWriter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener{

    private static final int UPDATE_TIMER = 1;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    //Collectors
    private ArrayList<SimpleSensorData> accelDatas;
    private ArrayList<SimpleSensorData> earthAccelDatas;
    private ArrayList<SimpleSensorData> gravDatas;
    private ArrayList<SimpleSensorData> gyrosDatas;
    private ArrayList<SimpleSensorData> magDatas;
    //Current data
    private float[] gravCurrent = null;
    private float[] magCurrent = null;

    //Views
    private TextView tvName;
    //Vehicle
    private RadioGroup rgVehicle;
    //Status
    private RadioGroup rgStatus;

    //Menu
    private FloatingActionMenu famMenu;
    private FloatingActionButton fabUsers;
    private FloatingActionButton fabConfig;

    //Collect Layout
    private Button btnStart;
    private TextView tvActivityRunning;
    private TextView tvTimer;
    private ImageButton imbControlCollection;
    private ImageButton imbStopToSaveData;
    private BottomSheetBehavior bsbDataCollection;

    //Dialog
    private DialogPlus usersDialog;
    private UsersAdapter usersAdapter;

    private Spinner spModes;

    private UserWriter mUserWriter;

    private SensorManager sensorMgr;

    private SensorDataWriter writerSensorDatas;
    private int samplingPeriodUs;

    private UserData currentUser;
    private boolean isStop = true;
    private boolean isPause = true;

    private SharedPreferences sharedPreferences;

    private Thread timerThread;

    private long timestamp;

    private PowerManager powerManager;

    private PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStoragePermissionGranted();
        checkWakeLockPermissionGranted();
        initViews();
        initComps();
    }

    public  boolean checkWakeLockPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WAKE_LOCK)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission","Permission is granted");
                return true;
            } else {
                Log.v("Permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission","Permission is granted");
            return true;
        }
    }

    public  boolean checkStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission","Permission is granted");
                return true;
            } else {
                Log.v("Permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission","Permission is granted");
            return true;
        }
    }

    private void initComps() {
        powerManager = (PowerManager)this.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        sensorMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);

        accelDatas = new ArrayList<>();
        earthAccelDatas = new ArrayList<>();
        gravDatas = new ArrayList<>();
        gyrosDatas = new ArrayList<>();
        magDatas = new ArrayList<>();

        isCollecting = false;

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
    }

    private void initViews() {
        mUserWriter = new UserWriter();

        tvName = (TextView)findViewById(R.id.tv_enter_name);
        sharedPreferences = getSharedPreferences(Constants.SHARE_PREFERENCES_FIELD.FILE_NAME,
                Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(Constants.SHARE_PREFERENCES_FIELD.CURRENT_NAME, "");
        if (name != ""){
            tvName.setText(name);
            currentUser = mUserWriter.getUserDataByName(name);
        }
        rgVehicle = (RadioGroup)findViewById(R.id.rg_vehicle);
        rgStatus = (RadioGroup)findViewById(R.id.rg_status);

        famMenu = (FloatingActionMenu)findViewById(R.id.material_design_android_floating_action_menu);
        fabUsers = (FloatingActionButton)findViewById(R.id.menu_item_users);
        fabUsers.setOnClickListener(this);
        fabConfig = (FloatingActionButton)findViewById(R.id.menu_item_config);
        fabConfig.setOnClickListener(this);

        initBottomSheet();

        samplingPeriodUs = sensorMgr.SENSOR_DELAY_GAME;
    }

    private void initBottomSheet() {
        RelativeLayout bottomLayout = (RelativeLayout)findViewById(R.id.layout_bottom_sheet);

        bsbDataCollection = BottomSheetBehavior.from(bottomLayout);
        bsbDataCollection.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bsbDataCollection.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bsbDataCollection.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnStart.animate().alpha(0.0f).setDuration(700l);
                }
                else {
                    bsbDataCollection.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btnStart.animate().alpha(1.0f).setDuration(700l);
                }
            }
        });

        bsbDataCollection.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bsbDataCollection.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    btnStart.animate().alpha(0.0f).setDuration(700l);
                } else {
                    btnStart.animate().alpha(1.0f).setDuration(700l);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btnStart = (Button)bottomLayout.findViewById(R.id.btn_start_collecting);
        btnStart.setOnClickListener(this);
        tvActivityRunning = (TextView)bottomLayout.findViewById(R.id.tv_vehicle_collecting);
        tvTimer = (TextView)bottomLayout.findViewById(R.id.tv_time_collect);
        imbControlCollection = (ImageButton)bottomLayout.findViewById(R.id.imb_control_collect_data);
        imbControlCollection.setOnClickListener(this);
        imbStopToSaveData = (ImageButton)bottomLayout.findViewById(R.id.imb_stop_to_save_collect_data);
        imbStopToSaveData.setOnClickListener(this);

    }

    private Handler dataCollectionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_TIMER:
                    tvTimer.setText(currentTimerString);
                    break;
                default:
                    break;
            }
        }

    };

    private boolean isCollecting = false;
    private String currentTimerString = "";
    private Runnable timerRunnable = new Runnable() {
        int count = 0;
        String minute;
        String second;
        @Override
        public void run() {
            while (isCollecting){
                try {
                    if (!isPause)
                        count++;
                    minute = (count/60) + "";
                    if (count%60 < 10){
                        second = "0" + count%60;
                    } else
                        second = count%60 + "";
                    currentTimerString = minute + ":" + second;
                    dataCollectionHandler.sendEmptyMessage(UPDATE_TIMER);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            count = 0;
        }
    };

//    private void stopCollectData() {
//    }



//    private void startCollectData() {
//        String mode = (String)spMode.getSelectedItem();
//        int samplingPeriodUs = 0;
//        switch (mode){
//            case Constants.SAMPLING_PERIOD_US.DELAY_FASTEST:
//                samplingPeriodUs = sensorMgr.SENSOR_DELAY_FASTEST;
//                break;
//            case Constants.SAMPLING_PERIOD_US.DELAY_GAME:
//                samplingPeriodUs = sensorMgr.SENSOR_DELAY_GAME;
//                break;
//            case Constants.SAMPLING_PERIOD_US.DELAY_NORMAL:
//                samplingPeriodUs = sensorMgr.SENSOR_DELAY_NORMAL;
//                break;
//            default:
//                samplingPeriodUs = sensorMgr.SENSOR_DELAY_GAME;
//                break;
//        }
//
//        sensorMgr.registerListener(mAccelCollector,
//                sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                samplingPeriodUs);
//        sensorMgr.registerListener(mGravCollector,
//                sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY),
//                samplingPeriodUs);
//        sensorMgr.registerListener(mGyrosCollector,
//                sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
//                samplingPeriodUs);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_collecting:
                bsbDataCollection.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.imb_control_collect_data:
                controlDataCollection();
                break;
            case R.id.imb_stop_to_save_collect_data:
                saveData();
                break;
            case R.id.menu_item_users:
                chooseUser();
                famMenu.close(true);
                break;
            case R.id.menu_item_config:
                showConfigDialog();
                famMenu.close(true);
                break;
            default:
                break;
        }
    }

    private void saveData() {
        isStop = true;
        isCollecting = false;
        isPause = false;

        if (currentUser == null){
            Toast.makeText(this,getResources().getString(R.string.please_add_user),Toast.LENGTH_LONG).show();
            tvName.setText("");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Constants.SHARE_PREFERENCES_FIELD.CURRENT_NAME);
            editor.commit();
            bsbDataCollection.setState(BottomSheetBehavior.STATE_COLLAPSED);
            btnStart.setVisibility(View.VISIBLE);
            imbStopToSaveData.setVisibility(View.GONE);
            imbControlCollection.setImageResource(R.drawable.ic_play_arrow_white);
            tvTimer.setText("00:00");
            tvActivityRunning.setText("");
            return;
        }

        String time = sdf.format(new Date());
        String folder = currentUser.getName();

        String csvName = null;

        String vehicle = ((RadioButton) rgVehicle.findViewById(rgVehicle.getCheckedRadioButtonId())).
                getText().toString();

        String status = ((RadioButton) rgStatus.findViewById(rgStatus.getCheckedRadioButtonId())).
                getText().toString();
        String mode = null;

        switch (samplingPeriodUs) {
            case SensorManager.SENSOR_DELAY_FASTEST:
                mode = Constants.SAMPLING_PERIOD_US.DELAY_FASTEST;
                break;
            case SensorManager.SENSOR_DELAY_GAME:
                mode = Constants.SAMPLING_PERIOD_US.DELAY_GAME;
                break;
            case SensorManager.SENSOR_DELAY_NORMAL:
                mode = Constants.SAMPLING_PERIOD_US.DELAY_NORMAL;
                break;
            case SensorManager.SENSOR_DELAY_UI:
                mode = Constants.SAMPLING_PERIOD_US.DELAY_UI;
                break;
            default:
                mode = Constants.SAMPLING_PERIOD_US.DELAY_GAME;
                break;
        }


        if (!accelDatas.isEmpty()) {
            //Accelerometer
            sensorMgr.unregisterListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            //csvName: Sensor_vihicle_status_timestamp
            csvName = "Accelerometer_" + vehicle + "_" + status + "_" + time + Constants.PATH.CSV_FORMAT;

            writerSensorDatas = new SensorDataWriter(MainActivity.this, accelDatas, folder, csvName);
            writerSensorDatas.write(mode);
            accelDatas.clear();

            //Earth Accelertometer
            csvName = "EarthAccel_" + vehicle + "_" + status + "_" + time + Constants.PATH.CSV_FORMAT;

            writerSensorDatas = new SensorDataWriter(MainActivity.this, earthAccelDatas, folder, csvName);
            writerSensorDatas.write(mode);
            earthAccelDatas  .clear();
        }

        if (!gravDatas.isEmpty()) {
            //Gravity
            sensorMgr.unregisterListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY));
            csvName = "Gravity_" + vehicle + "_" + status + "_" + time + Constants.PATH.CSV_FORMAT;

            writerSensorDatas = new SensorDataWriter(MainActivity.this, gravDatas, folder, csvName);
            writerSensorDatas.write(mode);
            gravDatas.clear();
        }

        if (!gyrosDatas.isEmpty()) {
            //Gyroscope
            sensorMgr.unregisterListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
            csvName = "Gyrocope_" + vehicle + "_" + status + "_" + time + Constants.PATH.CSV_FORMAT;

            writerSensorDatas = new SensorDataWriter(MainActivity.this, gyrosDatas, folder, csvName);
            writerSensorDatas.write(mode);
            gyrosDatas.clear();
        }

        if (!magDatas.isEmpty()) {
            //Magnetic
            sensorMgr.unregisterListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
            csvName = "Magnetic_" + vehicle + "_" + status + "_" + time + Constants.PATH.CSV_FORMAT;

            writerSensorDatas = new SensorDataWriter(MainActivity.this, magDatas, folder, csvName);
            writerSensorDatas.write(mode);
            magDatas.clear();
        }


        bsbDataCollection.setState(BottomSheetBehavior.STATE_COLLAPSED);
        btnStart.setVisibility(View.VISIBLE);
        imbStopToSaveData.setVisibility(View.GONE);
        imbControlCollection.setImageResource(R.drawable.ic_play_arrow_white);
        tvTimer.setText("00:00");
        tvActivityRunning.setText("");
    }

    private void showConfigDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.config_layout);

        spModes = (Spinner)dialog.findViewById(R.id.sp_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModes.setAdapter(adapter);
        //set default to GAME_DELAY_MODE
        spModes.setSelection(1);
        Button btnOk = (Button)dialog.findViewById(R.id.btn_ok_config);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((String)spModes.getSelectedItem()){
                    case Constants.SAMPLING_PERIOD_US.DELAY_FASTEST:
                        samplingPeriodUs = sensorMgr.SENSOR_DELAY_FASTEST;
                        break;
                    case Constants.SAMPLING_PERIOD_US.DELAY_GAME:
                        samplingPeriodUs = sensorMgr.SENSOR_DELAY_GAME;
                        break;
                    case Constants.SAMPLING_PERIOD_US.DELAY_NORMAL:
                        samplingPeriodUs = sensorMgr.SENSOR_DELAY_NORMAL;
                        break;
                    case Constants.SAMPLING_PERIOD_US.DELAY_UI:
                        samplingPeriodUs = sensorMgr.SENSOR_DELAY_UI;
                        break;
                    default:
                        Toast.makeText(MainActivity.this,"Not selected",Toast.LENGTH_SHORT).show();
                        break;
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void chooseUser() {
        usersAdapter = new UsersAdapter(this,mUserWriter.getUserDatas());
        usersDialog = DialogPlus.newDialog(this)
                .setAdapter(usersAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.select_user_header_layout)
                .setFooter(R.layout.dialog_footer_layout)
                .setExpanded(false)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        String name = ((TextView)view.findViewById(R.id.tv_name_in_user_item))
                                .getText().toString();
                        tvName.setText(name);
                        currentUser = mUserWriter.getUserDataByName(name);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.SHARE_PREFERENCES_FIELD.CURRENT_NAME,name);
                        editor.commit();
                    }
                }).create();
        usersDialog.show();

        Button btnAddUser = (Button)usersDialog.findViewById(R.id.btn_add_user_dialog);
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersDialog.dismiss();
                final NewUserDialog newUserDialog = new NewUserDialog(MainActivity.this, mUserWriter);
                newUserDialog.show();
                ImageButton imbSave = (ImageButton)newUserDialog.findViewById(R.id.imb_save_add_user);
                imbSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newUserDialog.saveUser();
                        currentUser = mUserWriter.getUserDataByName(newUserDialog.getName());
                        tvName.setText(newUserDialog.getName());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.SHARE_PREFERENCES_FIELD.CURRENT_NAME,
                                newUserDialog.getName());
                        editor.commit();
                    }
                });
            }
        });
        Button btnClose = (Button)usersDialog.findViewById(R.id.btn_close_dialog);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersDialog.dismiss();
            }
        });
    }

    private void controlDataCollection() {
        //check infor
        String name = tvName.getText().toString();
        if (name.isEmpty()){
            Toast.makeText(this,getResources().getString(R.string.please_add_user),
                    Toast.LENGTH_LONG).show();
            return;
        }
        int vehicleId = rgVehicle.getCheckedRadioButtonId();
        if (vehicleId == -1){
            Toast.makeText(this,getResources().getString(R.string.please_choose_vehicle),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int statusId = rgStatus.getCheckedRadioButtonId();
        if (statusId == -1){
            Toast.makeText(this,getResources().getString(R.string.please_choose_status),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        isCollecting = true;
        if (isStop){
            isStop = false;
            isPause = false;
            timerThread = new Thread(timerRunnable);
            timerThread.start();

            wakeLock.acquire();

            imbControlCollection.setImageResource(R.drawable.ic_pause_white);
            imbStopToSaveData.setVisibility(View.VISIBLE);
            sensorMgr.registerListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    samplingPeriodUs);
            sensorMgr.registerListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    samplingPeriodUs);
            sensorMgr.registerListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY),
                    samplingPeriodUs);
            sensorMgr.registerListener(this,
                    sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    samplingPeriodUs);
            String vehicle = ((RadioButton)rgVehicle.
                    findViewById(rgVehicle.getCheckedRadioButtonId())).getText().toString();
            tvActivityRunning.setText(vehicle);
        } else {
            if (!isPause) {
                isPause = true;
                //Change button state to pause
                imbControlCollection.setImageResource(R.drawable.ic_play_arrow_white);
                imbStopToSaveData.setVisibility(View.VISIBLE);

                wakeLock.release();

                //pause collect data
                sensorMgr.unregisterListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                sensorMgr.unregisterListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
                sensorMgr.unregisterListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY));
                sensorMgr.unregisterListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));

            } else {
                isPause = false;
                imbControlCollection.setImageResource(R.drawable.ic_pause_white);
                imbStopToSaveData.setVisibility(View.VISIBLE);
                sensorMgr.registerListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        samplingPeriodUs);
                sensorMgr.registerListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        samplingPeriodUs);
                sensorMgr.registerListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY),
                        samplingPeriodUs);
                sensorMgr.registerListener(this,
                        sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        samplingPeriodUs);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        timestamp = System.currentTimeMillis();
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                float[] deviceRelativeAcceleration = new float[4];
                deviceRelativeAcceleration[0] = event.values[0];
                deviceRelativeAcceleration[1] = event.values[1];
                deviceRelativeAcceleration[2] = event.values[2];
                deviceRelativeAcceleration[3] = 0;

                //Add Accelerometer data
                accelDatas.add(new SimpleSensorData(timestamp,deviceRelativeAcceleration[0],
                        deviceRelativeAcceleration[1], deviceRelativeAcceleration[2]));

                //Invert R
                float[] invert = new float[16];
                float[] R = new float[16];
                float[] earthAccel = new float[16];

                //Change the device relative acceleration values to earth relative values
                //Device has all sensors
                if (!gravDatas.isEmpty() && !magDatas.isEmpty()){
                    float[] I = new float[16];
                    SensorManager.getRotationMatrix(R,I,gravCurrent,magCurrent);
                    android.opengl.Matrix.invertM(invert,0,R,0);
                    android.opengl.Matrix.multiplyMV(earthAccel,0,invert,0,deviceRelativeAcceleration,0);
                }
                //Device has only Accelerometer
                else {
                    android.opengl.Matrix.invertM(invert,0,R,0);
                    android.opengl.Matrix.multiplyMV(earthAccel,0,invert,0,deviceRelativeAcceleration,0);
                }

                //Add data after transform
                earthAccelDatas.add(new SimpleSensorData(timestamp,earthAccel[0],
                        earthAccel[1],earthAccel[2]));

                break;
            case Sensor.TYPE_GYROSCOPE:
                gyrosDatas.add(new SimpleSensorData(timestamp,event.values[0], event.values[1],
                        event.values[2]));
                break;
            case Sensor.TYPE_GRAVITY:
                gravCurrent = event.values;
                gravDatas.add(new SimpleSensorData(timestamp,gravCurrent[0], gravCurrent[1],
                        gravCurrent[2]));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magCurrent = event.values;
                magDatas.add(new SimpleSensorData(timestamp, magCurrent[0], magCurrent[1],
                        magCurrent[2]));
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
