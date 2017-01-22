package com.uet.humanactivityrecognition.writers;

import android.util.Log;

import com.uet.humanactivityrecognition.constants.Constants;
import com.uet.humanactivityrecognition.sensordata.SimpleSensorData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Project name: HumanActivityRecognition
 * Created by Cuong Phan
 * Email: cuongphank58@gmail.com
 * on 21/01/2017.
 * University of Engineering and Technology,
 * Vietnam National University.
 */

public class SensorDataWriter {

    private ArrayList<SimpleSensorData> datas;

    private String fileName;

    private FileWriter fileWriter;

    private File fileToWriteTo;

    public SensorDataWriter(ArrayList<SimpleSensorData> datas, String fileName) {
        this.datas = datas;
        this.fileName = fileName;
        makeFolder();
    }

    private void makeFolder() {
        File folder = new File(Constants.PATH.CSV_PATH);

        if (!folder.exists()){
            boolean mkDir = folder.mkdirs();
            Log.e("Make folder", mkDir + "");
        }

        makeFile(fileName);
    }

    private void makeFile(String fileName){
        fileToWriteTo = new File(Constants.PATH.CSV_PATH , fileName);
    }

    public ArrayList<SimpleSensorData> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<SimpleSensorData> datas) {
        this.datas = datas;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        makeFile(fileName);
    }

    public void write(){
        fileWriter = null;
        try {
            Log.e("Write before", fileToWriteTo.getAbsolutePath());
            fileWriter = new FileWriter(fileToWriteTo);
//            FileOutputStream fileOutputStream = new FileOutputStream(fileToWriteTo);
            Log.e("Writer after", "true");
//            String dataString = null;
            writeLine("Timestamp","X", "Y", "Z");
            for (int i = 0; i < datas.size(); i++){
                 writeLine(datas.get(i).getTimestamp() + "", datas.get(i).getX() + "",
                        datas.get(i).getY() + "", datas.get(i).getZ() + "");
            }
            fileWriter.flush();
            fileWriter.close();
//            fileOutputStream.write(dataString.getBytes());
//            fileOutputStream.flush();
//            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLine(String timestamp, String x, String y, String z) throws IOException {
        fileWriter.append(timestamp);
        fileWriter.append(",");
        fileWriter.append(x);
        fileWriter.append(",");
        fileWriter.append(y);
        fileWriter.append(",");
        fileWriter.append(z);
        fileWriter.append("\n");
//        return  timestamp + "," + x + "," + y + "," + z + "\n";
    }
}
