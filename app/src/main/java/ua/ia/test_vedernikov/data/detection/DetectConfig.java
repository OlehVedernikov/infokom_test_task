package ua.ia.test_vedernikov.data.detection;

public class DetectConfig {

    private float mMinimumConfidence;
    private int mNumDetection;
    private int mInputSize;
    private boolean mIsQuantized;
    private String mModelFile;
    private String mLabelsFile;

    public DetectConfig(float minimumConfidence, int numDetection, int inputSize, boolean isQuantized, String modelFile, String labelsFile){
        mMinimumConfidence = minimumConfidence;
        mNumDetection = numDetection;
        mInputSize = inputSize;
        mIsQuantized = isQuantized;
        mModelFile = modelFile;
        mLabelsFile = labelsFile;
    }

    public float getMinimumConfidence() {
        return mMinimumConfidence;
    }

    public int getNumDetection() {
        return mNumDetection;
    }

    public int getInputSize() {
        return mInputSize;
    }

    public boolean isQuantized() {
        return mIsQuantized;
    }

    public String getModelFile() {
        return mModelFile;
    }

    public String getLabelsFile() {
        return mLabelsFile;
    }

}
