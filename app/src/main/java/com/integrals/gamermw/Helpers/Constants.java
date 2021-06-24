package com.integrals.gamermw.Helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Constants {
    public static final String TAG ="ADHIN";
    public static final String TIME_FORMAT="dd-MM-yyyy HH:mm:ss";
    public static final String PROFILE_PIC = "https://firebasestorage.googleapis.com/v0/b/gamer-mw-adhin.appspot.com/o/Profiles%2Fic_launcher_round.png?alt=media&token=e2b1b817-02cc-45b7-8acb-a33682604335";
    public static final int MAX_ANGLE = 50;
    public static final int WHEEL_DURATION = 4000;
    public static final int INTERVELS = 50;

    public static void showLog(String message){
        Log.d(TAG,message);
    }
    public static void showToast(String message, Context context){ Toast.makeText(context,message,Toast.LENGTH_LONG).show(); }
}
