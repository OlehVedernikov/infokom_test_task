package ua.ia.test_vedernikov.ui.main;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.Nullable;
import ua.ia.test_vedernikov.data.camera.CameraInfokom;
import ua.ia.test_vedernikov.data.detection.DetectConfig;
import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;
import ua.ia.test_vedernikov.data.repositories.tflite.TFLiteConstants;
import ua.ia.test_vedernikov.domain.persons_detection.IPersonsDetectorCallback;
import ua.ia.test_vedernikov.domain.persons_detection.PersonsDetector;
import ua.ia.test_vedernikov.utils.PermissionUtils;

public class MainPresenter implements IMainPresenter, IPersonsDetectorCallback {

    private static final int CAMERA_UNDEFINED = -1;

    private static MainPresenter mInstance;
    private IMainView mView;
    private int mCameraIndex = CAMERA_UNDEFINED;
    private ArrayList<CameraInfokom.Entry> mCameras;
    private boolean mDetectionActive = false;
    private DetectConfig mDetectionConfig;

    private MainPresenter(){
        //do nothing
    }

    static synchronized MainPresenter getInstance(){
        if (mInstance == null)
            mInstance = new MainPresenter();
        return mInstance;
    }

    @Override
    public void start() {
        initObjects();
    }

    private void initObjects(){
        initDetectionConfig();
    }

    private void initDetectionConfig(){
        mDetectionConfig = new DetectConfig(TFLiteConstants.MIN_CONFIDENCE, TFLiteConstants.NUM_DETECTION, TFLiteConstants.IMAGE_DIMEN,
                TFLiteConstants.QUANTIZED, TFLiteConstants.MODEL_FILE, TFLiteConstants.LABEL_FILE);
    }

    @Override
    public void stop() {
        resetObjects();

        PersonsDetector.getInstance().removeCallback(mInstance);
        PersonsDetector.getInstance().stopDetect();
    }

    private void resetObjects(){
        mCameras = null;
    }

    @Override
    public void bindView(IMainView view) {
        if (view == null) {
            unbindView();
            return;
        }

        mView = view;
        mView.setDetectionCallback(PersonsDetector.getInstance());
        mView.setDetectionConfig(mDetectionConfig);

        Context context = view.getContext();
        initObjects(context);
        initViews();
    }

    private void initObjects(Context context){
        initCameras(context);
    }

    private void initCameras(Context context){
        mCameras = new ArrayList<>();
        for (CameraInfokom camera : CameraInfokom.values())
            mCameras.add(camera.new Entry(context));
    }

    private void initViews(){
        initCamerasViews();

        mDetectionActive = false;
        showButtonStartDetection();
    }

    private void initCamerasViews(){
        if (mCameras == null || mCameras.size() == 0)
            hideCameras();
        else
            showCamerasList();
    }

    private void hideCameras(){
        try {
            if (mView != null)
                mView.hideCamerasSpinner();
        } catch (Exception e){
            //do nothing
        }
    }

    private void showCamerasList(){
        mCameraIndex = 0;
        ArrayList<String> camerasTitles = getCamerasTitles();
        try {
            if (mView != null)
                mView.updateCamerasSpinner(camerasTitles, mCameraIndex);
        } catch (Exception e){
            //do nothing
        }
    }

    private ArrayList<String> getCamerasTitles(){
        ArrayList<String> res = new ArrayList<>();

        for (CameraInfokom.Entry camera : mCameras)
            res.add(camera.getLabel());

        return res;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void onSelectedCamera(Context context, int cameraIndex) {
        mCameraIndex = cameraIndex;
        startCamera(context);
    }

    @Override
    public void onClickToggleDetection() {
        mDetectionActive = !mDetectionActive;

        if (mDetectionActive)
            startDetection();
        else
            stopDetection();
    }

    private void startDetectAtView(){
        try {
            if (mView != null)
                mView.startDetect();
        } catch (Exception e){
            //do nothing
        }
    }

    private void startDetection(){
        showButtonStopDetection();
        startDetectAtView();

        PersonsDetector.getInstance().addCallback(mInstance);
        PersonsDetector.getInstance().startDetect();
    }

    private void stopDetection(){
        showButtonStartDetection();
        stopDetectAtView();
        onPersonDetectedFailed();

        PersonsDetector.getInstance().removeCallback(mInstance);
        PersonsDetector.getInstance().stopDetect();
    }

    private void stopDetectAtView(){
        try {
            if (mView != null)
                mView.stopDetect();
        } catch (Exception e){
            //do nothing
        }
    }

    private void showButtonStartDetection(){
        try {
            if (mView != null)
                mView.showButtonStartDetection();
        } catch (Exception e){
            //do nothing
        }
    }

    private void showButtonStopDetection(){
        try {
            if (mView != null)
                mView.showButtonStopDetection();
        } catch (Exception e){
            //do nothing
        }
    }

    private void startCamera(Context context){
        if (mCameraIndex == CAMERA_UNDEFINED)
            return;

        if (!PermissionUtils.isPermissionGranted(context, PermissionUtils.CAMERA)){
            requestCameraPermission();
            return;
        }

        showDetectedPersons(null, 0, 0, 0);

        if (mCameraIndex >= mCameras.size())
            return;

        if (isSelectedFrontCamera())
            startFrontCamera();
        else
            startBackCamera();
    }

    private boolean isSelectedFrontCamera(){
        return mCameraIndex >= 0 && mCameraIndex < mCameras.size()
                && mCameras.get(mCameraIndex).isFront();
    }

    private void startBackCamera(){
        try {
            mView.startBackCamera();
        } catch (Exception e){
            //do nothing
        }
    }

    private void startFrontCamera(){
        try {
            mView.startFrontCamera();
        } catch (Exception e){
            //do nothing
        }
    }

    private void showDetectedPersons(@Nullable List<Classifier.Recognition> persons,
            int unscaledImageWidth, int unscaledImageHeight, int unscaledImageRotationDegrees){
        try {
            if (mView != null) {
                String countStr = DetectedObjectUiMapper.getValueDetectedPersonsForDisplay(persons, mDetectionActive);

                int imageWidth = mView.getCameraPreviewImageWidth();
                int imageHeight = mView.getCameraPreviewImageHeight();
                Collection<DetectedObjectUi> objects = DetectedObjectUiMapper.mapObjects(persons, unscaledImageWidth, unscaledImageHeight, unscaledImageRotationDegrees,
                        imageWidth, imageHeight);

                mView.showDetectedPersons(countStr, objects);
            }
        } catch (Exception e){
            //do nothing
        }
    }

    @Override
    public void onGrantedPermission(Context context, String permissionLabel) {
        if (permissionLabel.equals(PermissionUtils.CAMERA))
            onGrantedPermissionCamera(context);
    }

    @Override
    public void onDeniedPermission(String permissionLabel) {
        if (permissionLabel.equals(PermissionUtils.CAMERA))
            onDeniedPermissionCamera();
    }

    @Override
    public void onCloseAppSettings(Context context) {
        startCamera(context);
    }

    private void requestCameraPermission(){
        try {
            if (mView != null)
                mView.requestCameraPermission();
        } catch (Exception e){
            //do nothing
        }
    }

    private void onGrantedPermissionCamera(Context context){
        startCamera(context);
    }

    private void onDeniedPermissionCamera(){
        //do nothing
    }

    @Override
    public void onPersonDetectedSuccess(List<Classifier.Recognition> persons, int imageWidth, int imageHeight, int imageRotationDegrees) {
        showDetectedPersons(persons, imageWidth, imageHeight, imageRotationDegrees);
    }

    @Override
    public void onPersonDetectedFailed() {
        showDetectedPersons(null, 0, 0, 0);
    }

}
