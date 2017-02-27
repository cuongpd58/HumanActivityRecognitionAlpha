package com.uet.humanactivityrecognition.constants;

import java.text.SimpleDateFormat;

/**
 * Created by Cương Rùa on 10/01/2017.
 */

public class Constants {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd:HHmmss");

    public interface PATH{
        String CSV_FOLDER = "/Human Activity Recognition/Datas/";
        String CSV_FORMAT = ".csv";
        String USER_DATA_FOLDER = "Users";
        String USER_DATA_FILENAME = "UserData.txt";
        String APP_FOLDER = "/Human Activity Recognition/";
    }

    public interface SAMPLING_PERIOD_US{
        String DELAY_FASTEST = "SENSOR_DELAY_FASTEST";
        String DELAY_GAME = "SENSOR_DELAY_GAME";
        String DELAY_NORMAL = "SENSOR_DELAY_NORMAL";
        String DELAY_UI = "SENSOR_DELAY_UI";
}

    public interface SHARE_PREFERENCES_FIELD{
        String CURRENT_NAME = "Current name";
        String FILE_NAME = "UserShare";
    }
}
