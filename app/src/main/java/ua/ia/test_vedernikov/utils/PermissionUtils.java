package ua.ia.test_vedernikov.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;
import ua.ia.test_vedernikov.R;

public class PermissionUtils {


    public static final String EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String CAMERA = Manifest.permission.CAMERA;


    public static boolean isPermissionGranted(Context context, String permissionLabel){
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ContextCompat.checkSelfPermission(context, permissionLabel) == PackageManager.PERMISSION_GRANTED;
    }

    public static String getPermissionRationale(Context context, String permissionLabel){
        PackageManager pm = context.getPackageManager();
        try {
            if (permissionLabel.equals(EXTERNAL_STORAGE))
                return context.getString(R.string.permission_external_storage_rationale, pm.getPermissionGroupInfo(Manifest.permission_group.STORAGE, 0).loadDescription(pm));
            else if (permissionLabel.equals(CAMERA))
                return context.getString(R.string.permission_camera_rationale, pm.getPermissionGroupInfo(Manifest.permission_group.CAMERA, 0).loadDescription(pm));
            else return "";
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "";
        }
    }

    public static String getPermissionDeniedRestrictions(Context context, String permissionLabel){
        PackageManager pm = context.getPackageManager();
        try {
            if (permissionLabel.equals(CAMERA))
                return context.getString(R.string.permission_camera_rationale_denied_restrictions, pm.getPermissionGroupInfo(Manifest.permission_group.CAMERA, 0).loadDescription(pm));
            else if (permissionLabel.equals(EXTERNAL_STORAGE))
                return context.getString(R.string.permission_external_storage_denied_restrictions, pm.getPermissionGroupInfo(Manifest.permission_group.STORAGE, 0).loadDescription(pm));
            else return "";
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "";
        }
    }

    public static String getPermissionGroupTitle(Context context, String permissionLabel){
        PackageManager pm = context.getPackageManager();
        try {
            if (permissionLabel.equals(EXTERNAL_STORAGE))
                return pm.getPermissionGroupInfo(Manifest.permission_group.STORAGE, 0).loadLabel(pm).toString();
            else if (permissionLabel.equals(CAMERA))
                return pm.getPermissionGroupInfo(Manifest.permission_group.CAMERA, 0).loadLabel(pm).toString();
            else return "";
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "";
        }
    }

    public static String getPermissionGroupDescription(Context context, String permissionLabel){
        PackageManager pm = context.getPackageManager();
        try {
            if (permissionLabel.equals(EXTERNAL_STORAGE))
                return pm.getPermissionGroupInfo(Manifest.permission_group.STORAGE, 0).loadDescription(pm).toString();
            else if (permissionLabel.equals(CAMERA))
                return pm.getPermissionGroupInfo(Manifest.permission_group.CAMERA, 0).loadDescription(pm).toString();
            else return "";
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            return "";
        }
    }

}
