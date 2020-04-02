package ua.ia.test_vedernikov.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {


    private static final String PREFERENCES_APP = "pref_app";
    private static final String WAS_REQUEST_PERMISSION = "was_request_permission_";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREFERENCES_APP, Context.MODE_PRIVATE);
    }

    public static boolean wasRequestPermission(Context context, String permissionLabel, boolean defaultValue){
        return getSharedPreferences(context).getBoolean(WAS_REQUEST_PERMISSION + permissionLabel, defaultValue);
    }

    public static void setWasRequestPermission(Context context, String permissionLabel, boolean value){
        getSharedPreferences(context).edit().putBoolean(WAS_REQUEST_PERMISSION + permissionLabel, value).apply();
    }
    
}
