package ua.ia.test_vedernikov.ui.components;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import ua.ia.test_vedernikov.data.repositories.PreferencesUtils;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;

    protected abstract void onGrantedPermission(String permissionLabel);
    protected abstract void onDeniedPermission(String permissionLabel);
    protected abstract void showPermissionRationale(String permissionLabel);
    protected abstract void showPermissionRationale_NeverAskAgain(String permissionLabel);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void requestPermissionWithRationaleIfNecessary(String permissionLabel) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionLabel)) {
            showPermissionRationale(permissionLabel);
        } else if (PreferencesUtils.wasRequestPermission(this, permissionLabel, false)) {
            showPermissionRationale_NeverAskAgain(permissionLabel);
        } else {
            requestPermission(permissionLabel);
        }
    }

    protected void requestPermission(String permissionLabel){
        PreferencesUtils.setWasRequestPermission(this, permissionLabel, true);

        ActivityCompat.requestPermissions(this,
                new String[] {permissionLabel},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            int count = Math.min(permissions.length, grantResults.length);
            for (int i = 0; i < count; i++){
                int grantResult = grantResults[i];
                switch (grantResult){
                    case PackageManager.PERMISSION_GRANTED:
                        onGrantedPermission(permissions[i]);
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        onDeniedPermission(permissions[i]);
                        break;
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
