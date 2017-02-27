package com.uet.humanactivityrecognition.writers;

import android.os.Environment;

import com.uet.humanactivityrecognition.constants.Constants;
import com.uet.humanactivityrecognition.userdata.UserData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Project name: HumanActivityRecognition
 * Created by Cuong Phan
 * Email: cuongphank58@gmail.com
 * on 08/02/2017.
 * University of Engineering and Technology,
 * Vietnam National University.
 */

public class UserWriter {
    public static final String USER_FOLDER = Environment.getExternalStorageDirectory().getPath()
            + "/" + Constants.PATH.APP_FOLDER + "/" + Constants.PATH.USER_DATA_FOLDER +"/";
    private static final int NUMBER_OF_FIELDS_USER_DATA = 5;

    private ArrayList<UserData> userDatas;

    private File mFile;

    public UserWriter(){
        File folder = new File(USER_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        mFile = new File(folder.getPath(), Constants.PATH.USER_DATA_FILENAME);
        if (!mFile.exists()){
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userDatas = new ArrayList<>();
        loadUserData();
    }

    public void loadUserData(){
        if (!userDatas.isEmpty()){
            userDatas.clear();
        }
        try {
            FileReader fileReader = new FileReader(mFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null){
                dissectUserData(line);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dissectUserData(String line) {

        int index = 0;
        ArrayList<String> infor = new ArrayList<>();

        //5 fields of user data
        //line: id,name,sex,yob,job
        do {
            infor.add(line.substring(index,line.indexOf("_",index + 1)));
            index = line.indexOf("_",index + 1) + 1;
        } while (line.indexOf("_", index) >= 0);
        infor.add(line.substring(line.lastIndexOf("_") + 1));

        if (infor.size() == NUMBER_OF_FIELDS_USER_DATA){
            //User: name,sex,age,job
            userDatas.add(new UserData(infor.get(1), infor.get(2), Integer.parseInt(infor.get(3)),
                    infor.get(4)));
        }
    }

    public ArrayList<UserData> getUserDatas() {
        return userDatas;
    }

    public void setUserDatas(ArrayList<UserData> userDatas) {
        this.userDatas = userDatas;
    }

    public void addUserData(String name, String sex, int age, String job){
        userDatas.add(new UserData(name,sex,age,job));
        updateUserDataFile();
    }

    private void updateUserDataFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mFile,false);
            String data = "";
            for (int i = 0; i < userDatas.size(); i++){
                data += userDatas.get(i).getId() + "_" + userDatas.get(i).getName()
                        + "_" + userDatas.get(i).getSex() + "_" + userDatas.get(i).getYearOfBirth()
                        + "_" + userDatas.get(i).getJob() + "\n";
            }
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getUserNameDatas() {
        int size = userDatas.size();
        String[] allUserName = new String[size];
        for (int i = 0; i < size; i++){
            allUserName[i] = userDatas.get(i).getName();
        }
        return allUserName;
    }

    public UserData getUserDataByName(String name) {
        UserData userData = null;
        int size = userDatas.size();
        for (int i = 0; i < size; i++){
            if (userDatas.get(i).getName().equals(name)){
                userData = userDatas.get(i);
            }
        }
        return userData;
    }
}
