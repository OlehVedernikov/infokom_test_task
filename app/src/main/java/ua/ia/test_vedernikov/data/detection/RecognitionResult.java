package ua.ia.test_vedernikov.data.detection;

import java.util.ArrayList;
import java.util.List;

import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;

public class RecognitionResult {

    private List<Classifier.Recognition> mConfidenceResults;
    private int mImageWidth, mImageHeight, mImageRotationDegrees;

    public RecognitionResult(List<Classifier.Recognition> confidenceResults, int imageWidth, int imageHeight, int imageRotationDegrees) {
        mConfidenceResults = new ArrayList<>(confidenceResults);
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        mImageRotationDegrees = imageRotationDegrees;
    }


    public int getImageWidth() {
        return mImageWidth;
    }

    public void setImageWidth(int imageWidth) {
        mImageWidth = imageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public void setImageHeight(int imageHeight) {
        mImageHeight = imageHeight;
    }

    public int getImageRotationDegrees() {
        return mImageRotationDegrees;
    }

    public void setImageRotationDegrees(int imageRotationDegrees) {
        mImageRotationDegrees = imageRotationDegrees;
    }

    public List<Classifier.Recognition> getConfidenceResult() {
        return mConfidenceResults;
    }

    public void setConfidenceResults(List<Classifier.Recognition> confidenceResults) {
        mConfidenceResults = confidenceResults;
    }
}
