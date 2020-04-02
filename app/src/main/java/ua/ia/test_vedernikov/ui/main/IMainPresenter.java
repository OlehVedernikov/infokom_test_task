package ua.ia.test_vedernikov.ui.main;

import android.content.Context;

interface IMainPresenter {

    void start();
    void stop();

    void bindView(IMainView view);
    void unbindView();

    void onSelectedCamera(Context context, int cameraIndex);
    void onClickToggleDetection();

    void onGrantedPermission(Context context, String permissionLabel);
    void onDeniedPermission(String permissionLabel);
    void onCloseAppSettings(Context context);

}
