package com.uet.humanactivityrecognition.writers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

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

    private String folderName;

    private FileWriter fileWriter;

    private File fileToWriteTo;

    private Context mContext;

    public SensorDataWriter(Context mContext, ArrayList<SimpleSensorData> datas, String folderName,
                            String fileName) {
        this.mContext = mContext;
        this.datas = datas;
        this.fileName = fileName;
        this.folderName = folderName;
        makeFolder(folderName);
    }

    private void makeFolder(String folderName) {

        File folder = new File(Environment.getExternalStorageDirectory().getPath()+
                                Constants.PATH.CSV_FOLDER + folderName + "/");

        if (!folder.exists()){
            folder.mkdirs() ;
        }

        makeFile(fileName);
    }

    private void makeFile(String fileName){
        fileToWriteTo = new File(Environment.getExternalStorageDirectory().getPath() +
                Constants.PATH.CSV_FOLDER + folderName , fileName);
        if (!fileToWriteTo.exists()){
            try {
                fileToWriteTo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public void write(String mode) {
        ProgressDialog progressDialog =  ProgressDialog.show(mContext, "", "Saving datas...", true);
        fileWriter = null;
        try {
            fileWriter = new FileWriter(fileToWriteTo);
            fileWriter.append(mode);
            fileWriter.append("\n");
            fileWriter.append("Size: " + datas.size());
            fileWriter.append("\n");
            writeLine("Timestamp", "X", "Y", "Z");
            for (int i = 0; i < datas.size(); i++) {
                writeLine(datas.get(i).getTimestamp() + "", datas.get(i).getX() + "",
                        datas.get(i).getY() + "", datas.get(i).getZ() + "");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        progressDialog.dismiss();
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
    }
}
