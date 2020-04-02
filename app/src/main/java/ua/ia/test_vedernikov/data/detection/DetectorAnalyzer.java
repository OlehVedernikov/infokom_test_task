package ua.ia.test_vedernikov.data.detection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;
import ua.ia.test_vedernikov.data.repositories.tflite.TFLiteConstants;
import ua.ia.test_vedernikov.data.repositories.tflite.TFLiteObjectDetectionAPIModel;
import ua.ia.test_vedernikov.utils.ImageUtils;

public class DetectorAnalyzer implements ImageAnalysis.Analyzer {

    @SuppressWarnings("unused")
    private static final String TAG = "infokom_Analyzer";


    public interface IObjectDetectorAnalyzerCallback{
        void onResultDetection(RecognitionResult result);
    }

    private DetectConfig mConfig;
    private IObjectDetectorAnalyzerCallback mCallback;
    private Classifier mDetector;
    private Context mContext;


    public DetectorAnalyzer(Context context, DetectConfig config, IObjectDetectorAnalyzerCallback callback){
        mContext = context;
        mConfig = config;
        mCallback = callback;

        initObjects();
    }

    private void initObjects(){
        initDetector();
    }

    private void initDetector(){
        try {
            mDetector = TFLiteObjectDetectionAPIModel.create(
                    mContext.getAssets(),
                    mConfig.getModelFile(),
                    mConfig.getLabelsFile(),
                    mConfig.getInputSize(),
                    mConfig.isQuantized());
        } catch (final IOException e) {
            mDetector = null;
        }
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (mDetector == null)
            return;

        Bitmap bitmap = imageToBitmap(image);
        if (bitmap != null) {
            List<Classifier.Recognition> allResults = mDetector.recognizeImage(bitmap);
            List<Classifier.Recognition> confidenceResults = DetectionUtils.getConfidenceResults(allResults, mConfig.getMinimumConfidence());

            //for test
//            for (Classifier.Recognition result : confidenceResults)
//                Log.d(TAG, "analyze: result title = " + result.getTitle() + ", confidence = " + result.getConfidence());
            //for test

            if (mCallback != null) {
                RecognitionResult result = new RecognitionResult(confidenceResults, mConfig.getInputSize(), mConfig.getInputSize(), 90);
                mCallback.onResultDetection(result);
            }
        }

        image.close();
    }

    @Nullable
    private Bitmap imageToBitmap(ImageProxy imageProxy){
        @SuppressLint("UnsafeExperimentalUsageError") Image image = imageProxy.getImage();
        if (image == null)
            return null;
        Bitmap bitmap = ImageUtils.imageToBitmap(image);

        int dstWidth, dstHeight, xOffset, yOffset;
        if (imageProxy.getWidth() > imageProxy.getHeight()){
            dstHeight = TFLiteConstants.IMAGE_DIMEN;
            dstWidth = (int)(imageProxy.getWidth() * ((float)TFLiteConstants.IMAGE_DIMEN/imageProxy.getHeight()));
            xOffset = 0;
            yOffset = (dstWidth - TFLiteConstants.IMAGE_DIMEN)/2;
        } else {
            dstWidth = TFLiteConstants.IMAGE_DIMEN;
            dstHeight = (int)(imageProxy.getHeight() * ((float)TFLiteConstants.IMAGE_DIMEN/imageProxy.getWidth()));
            xOffset = (dstHeight - TFLiteConstants.IMAGE_DIMEN)/2;
            yOffset = 0;
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);

        bitmap = ImageUtils.cropImage(bitmap, 90, xOffset, yOffset, TFLiteConstants.IMAGE_DIMEN, TFLiteConstants.IMAGE_DIMEN);
        return bitmap;
    }

}
