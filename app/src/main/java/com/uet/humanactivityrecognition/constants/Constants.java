package com.uet.humanactivityrecognition.constants;

/**
 * Created by Cương Rùa on 10/01/2017.
 */

public class Constants {
    public interface PATH{
        String CSV_FOLDER = "Human Activity Recognition";
        String CSV_FORMAT = ".csv";
    }

    public interface SAMPLING_PERIOD_US{
        String DELAY_FASTEST = "FASTEST_MODE";
        String DELAY_GAME = "GAME_DELAY_MODE";
        String DELAY_NORMAL = "NORMAL_MODE";
    }

    public interface SENSORS{
        String ACCELEROMETER = "Accelerometer";
        String GYROSCOPE = "Gyroscope";
        String GRAVITY = "Gravity";
    }
}
