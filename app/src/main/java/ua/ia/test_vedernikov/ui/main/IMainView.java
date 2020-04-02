package ua.ia.test_vedernikov.ui.main;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;

import ua.ia.test_vedernikov.data.detection.DetectConfig;
import ua.ia.test_vedernikov.data.detection.DetectorAnalyzer;

interface IMainView {

    Context getContext();
    int getCameraPreviewImageWidth();
    int getCameraPreviewImageHeight();

    void setDetectionCallback(DetectorAnalyzer.IObjectDetectorAnalyzerCallback callback);
    void setDetectionConfig(DetectConfig config);
    void requestCameraPermission();
    void hideCamerasSpinner();
    void updateCamerasSpinner(ArrayList<String> camerasTitles, int selectedIndex);
    void showButtonStartDetection();
    void showButtonStopDetection();
    void showDetectedPersons(String value, Collection<DetectedObjectUi> objects);

    void startFrontCamera();
    void startBackCamera();

    void startDetect();
    void stopDetect();

}
