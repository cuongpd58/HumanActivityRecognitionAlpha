package com.uet.humanactivityrecognition.constants;

import android.os.Environment;

/**
 * Created by Cương Rùa on 10/01/2017.
 */

public class Constants {
    public interface PATH{
        String CSV_PATH = Environment.getExternalStorageDirectory()
                + "/Human Activity Recognition";
        String CSV_FORMAT = ".csv";
    }
}
