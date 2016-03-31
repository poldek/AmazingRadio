package com.szycha.amazingradio.AppService;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pld on 29.03.16.
 */
public class Utils {

    private static SharedPreferences sp;
    private static final String PREF_NAME = "stream_sp";
    public static final String IS_STREAM = "stream";

    public static final String POSITION = "position";
    public static final String POSITION_DATA = "position_data";

    public static final String NAZWA = "nazwa";
    public static final String NAZWA_RADIA = "nazwa_data";

    public static final String ADRESS = "adress";
    public static final String ADRESS_RADIA = "adress_radia";

    public static void setDataBooleanToSP(Context context, String to, boolean
            data) {
        sp = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(to, data);
        editor.commit();
    }

    public static boolean getDataBooleanFromSP(Context context, String from) {
        sp = context.getSharedPreferences(PREF_NAME, 0);
        return sp.getBoolean(from, false);
    }


    public static void setPosition(Context context, String to, int position) {
        sp = context.getSharedPreferences(POSITION, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(to, position);
        editor.commit();
    }

    public static int getPosition(Context context, String from) {
        sp = context.getSharedPreferences(POSITION,0);
        return sp.getInt(from, -1);
    }

    public static void setNazwa(Context context, String to, String nazwa) {
        sp = context.getSharedPreferences(NAZWA, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(to, nazwa);
        editor.commit();
    }

    public static String getRadioNazwa(Context context, String from) {
        sp = context.getSharedPreferences(NAZWA,0);
        return sp.getString(from, null);
    }



    public static void setAdressRadia(Context context, String to, String nazwa) {
        sp = context.getSharedPreferences(ADRESS, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(to, nazwa);
        editor.commit();
    }

    public static String getAdressRadia(Context context, String from) {
        sp = context.getSharedPreferences(ADRESS,0);
        return sp.getString(from,null);
    }
}
