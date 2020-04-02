package ua.ia.test_vedernikov.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import ua.ia.test_vedernikov.R;
import ua.ia.test_vedernikov.data.detection.DetectConfig;
import ua.ia.test_vedernikov.data.detection.DetectorAnalyzer;
import ua.ia.test_vedernikov.ui.components.BaseActivity;
import ua.ia.test_vedernikov.utils.PermissionUtils;

public class MainActivity extends BaseActivity implements IMainView {

    private static final int APP_SETTINGS_REQUEST_CODE = 100;

    private MainActivity mThisActivity;
    private IMainPresenter mPresenter;
    private PreviewView mPreviewView;
    private Spinner mCamerasSpinner;
    private TextView mTvDetectedPersons;
    private TextView mButToggleDetection;
    private String mButtonLabelStartDetection, mButtonLabelStopDetection;
    private CameraSelector cameraSelector;
    private int mCameraLensFacing = CameraSelector.LENS_FACING_FRONT;
    private ExecutorService mCameraExecutor;
    private boolean mNeedAnalyze;
    private ProcessCameraProvider mCameraProvider;
    private ImageAnalysis mImageAnalyzer;
    private DetectorAnalyzer.IObjectDetectorAnalyzerCallback mDetectionCallback;
    private DetectConfig mDetectConfig;
    private DetectionResultOverlayView mDetectionResultOverlayView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initObjects();
        initViews();
    }

    private void initObjects(){
        mThisActivity = this;
        initLabels();
        initPresenter();

        mCameraExecutor = Executors.newSingleThreadExecutor();
    }
    
    private void initLabels(){
        mButtonLabelStartDetection = getString(R.string.but_start_detection);
        mButtonLabelStopDetection = getString(R.string.but_stop_detection);
    }

    private void initPresenter(){
        mPresenter = MainPresenter.getInstance();
        mPresenter.start();
    }

    private void initViews(){
        initPreviewView();
        initDetectionResultOverlayView();
        initCamerasSpinner();
        initButToggleDetection();
        initTvDetectedPersons();
    }

    private void initPreviewView(){
        mPreviewView = findViewById(R.id.camera_preview);
    }

    private void initDetectionResultOverlayView(){
        mDetectionResultOverlayView = findViewById(R.id.detection_result);
    }

    private void initCamerasSpinner(){
        mCamerasSpinner = findViewById(R.id.sp_cameras);
        mCamerasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                mPresenter.onSelectedCamera(mThisActivity, index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
    }
    
    private void initButToggleDetection(){
        mButToggleDetection = findViewById(R.id.but_toggle_detection);
        mButToggleDetection.setOnClickListener(view -> mPresenter.onClickToggleDetection());
    }

    private void initTvDetectedPersons(){
        mTvDetectedPersons = findViewById(R.id.detected_people_value);
    }

    @Override
    public void onStart(){
        super.onStart();

        mPresenter.bindView(mThisActivity);
    }

    @Override
    public void onStop(){
        super.onStop();

        mPresenter.unbindView();
        mDetectionCallback = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mPresenter.stop();
        mCameraExecutor.shutdown();
    }

    @Override
    public Context getContext() {
        return mThisActivity;
    }

    @Override
    public int getCameraPreviewImageWidth() {
        return mPreviewView == null ? 0 : mPreviewView.getWidth();
    }

    @Override
    public int getCameraPreviewImageHeight() {
        return mPreviewView == null ? 0 : mPreviewView.getHeight();
    }

    @Override
    public void setDetectionCallback(DetectorAnalyzer.IObjectDetectorAnalyzerCallback callback) {
        mDetectionCallback = callback;
    }

    @Override
    public void setDetectionConfig(DetectConfig config) {
        mDetectConfig = config;
    }

    @Override
    public void requestCameraPermission() {
        requestPermissionWithRationaleIfNecessary(PermissionUtils.CAMERA);
    }

    @Override
    public void hideCamerasSpinner() {
        mCamerasSpinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateCamerasSpinner(ArrayList<String> camerasTitles, int selectedIndex) {
        mCamerasSpinner.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = createCamerasSpinnerAdapter(camerasTitles);
        mCamerasSpinner.setAdapter(adapter);
        mCamerasSpinner.setSelection(selectedIndex >= adapter.getCount() ? 0 : selectedIndex);
    }

    @Override
    public void showButtonStartDetection() {
        mButToggleDetection.setText(mButtonLabelStartDetection);
    }

    @Override
    public void showButtonStopDetection() {
        mButToggleDetection.setText(mButtonLabelStopDetection);
    }

    @Override
    public void showDetectedPersons(String count, Collection<DetectedObjectUi> objects) {
        runOnUiThread(() -> {
            mDetectionResultOverlayView.updateObjects(objects);
            mTvDetectedPersons.setText(count);
        });
    }

    @Override
    public void startFrontCamera() {
        mCameraLensFacing = CameraSelector.LENS_FACING_FRONT;
        bindCameraUseCases();
    }

    @Override
    public void startBackCamera() {
        mCameraLensFacing = CameraSelector.LENS_FACING_BACK;
        bindCameraUseCases();
    }

    @Override
    public void startDetect() {
        mNeedAnalyze = true;
        bindCameraUseCases();
    }

    @Override
    public void stopDetect() {
        mNeedAnalyze = false;
        try {
            mCameraProvider.unbind(mImageAnalyzer);
        } catch (Exception e){
            //do nothing
        }
    }

    private ArrayAdapter<String> createCamerasSpinnerAdapter(ArrayList<String> items){
        ArrayAdapter<String> res = new ArrayAdapter<>(mThisActivity, R.layout.spinner_simple_item, items);
        res.setDropDownViewResource(R.layout.spinner_dropdown_item);
        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //noinspection SwitchStatementWithTooFewBranches
        switch (requestCode){
            case APP_SETTINGS_REQUEST_CODE:
                mPresenter.onCloseAppSettings(mThisActivity);
                break;
        }
    }

    @Override
    protected void onGrantedPermission(String permissionLabel) {
        mPresenter.onGrantedPermission(mThisActivity, permissionLabel);
    }

    @Override
    protected void onDeniedPermission(String permissionLabel) {
        mPresenter.onDeniedPermission(permissionLabel);
    }

    @Override
    protected void showPermissionRationale(final String permissionLabel) {
        final String message = PermissionUtils.getPermissionRationale(mThisActivity, permissionLabel);
        String butGrantLabel = getString(R.string.but_grant);
        Snackbar.make(findViewById(R.id.camera_preview), message, Snackbar.LENGTH_LONG)
                .setAction(butGrantLabel, v -> requestPermission(permissionLabel))
                .show();
    }

    @Override
    protected void showPermissionRationale_NeverAskAgain(final String permissionLabel) {
        final String message = PermissionUtils.getPermissionRationale(mThisActivity, permissionLabel);
        String butGrantLabel = getString(R.string.but_grant);
        Snackbar.make(findViewById(R.id.camera_preview), message, Snackbar.LENGTH_LONG)
                .setAction(butGrantLabel, v -> openApplicationSettings())
                .show();
    }

    private void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, APP_SETTINGS_REQUEST_CODE);
    }

    private void bindCameraUseCases() {
        cameraSelector = (new CameraSelector.Builder()).requireLensFacing(mCameraLensFacing).build();
        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(mThisActivity);
        cameraProviderFuture.addListener(createCameraRunnable(mNeedAnalyze), ContextCompat.getMainExecutor(mThisActivity));

        try {
            mCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
        } catch (Exception e) {
            mCameraProvider = null;
        }
    }

    private Runnable createCameraRunnable(boolean needAnalyze) {
        return () -> {
            if (mCameraProvider == null)
                return;

            try {
                Preview preview = (new Preview.Builder()).build();
                preview.setSurfaceProvider(mPreviewView.getPreviewSurfaceProvider());

                mImageAnalyzer = null;
                if (needAnalyze) {
                    mImageAnalyzer = (new ImageAnalysis.Builder()).build();
                    DetectorAnalyzer objectDetectorAnalyzer = new DetectorAnalyzer(mThisActivity, mDetectConfig, mDetectionCallback);
                    mImageAnalyzer.setAnalyzer(mCameraExecutor, objectDetectorAnalyzer);
                }

                mCameraProvider.unbindAll();

                try {
                    if (mImageAnalyzer == null)
                        mCameraProvider.bindToLifecycle(mThisActivity, cameraSelector, preview);
                    else
                        mCameraProvider.bindToLifecycle(mThisActivity, cameraSelector, preview, mImageAnalyzer);
                } catch (Exception e) {
                    //do nothing
                }
            } catch (Exception e) {
                //do nothing
            }
        };
    }

}
