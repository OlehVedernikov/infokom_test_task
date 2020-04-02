package ua.ia.test_vedernikov.domain.persons_detection;

import java.util.LinkedHashSet;
import java.util.List;

import ua.ia.test_vedernikov.data.detection.RecognitionResult;
import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;
import ua.ia.test_vedernikov.data.detection.DetectorAnalyzer;

public class PersonsDetector implements DetectorAnalyzer.IObjectDetectorAnalyzerCallback {

    private final LinkedHashSet<IPersonsDetectorCallback> CALLBACKS = new LinkedHashSet<>();
    private static PersonsDetector mInstance;
    private boolean mNeedWork;

    private PersonsDetector(){
        //do nothing
    }

    public static synchronized PersonsDetector getInstance(){
        if (mInstance == null)
            mInstance = new PersonsDetector();
        return mInstance;
    }

    public void startDetect(){
        mNeedWork = true;
    }

    public void stopDetect(){
        mNeedWork = false;
    }

    public void removeCallback(IPersonsDetectorCallback callback){
        synchronized (CALLBACKS){
            CALLBACKS.remove(callback);
        }
    }

    public void addCallback(IPersonsDetectorCallback callback){
        synchronized (CALLBACKS) {
            CALLBACKS.add(callback);
        }
    }

    @Override
    public void onResultDetection(RecognitionResult result) {
        if (!mNeedWork)
            return;

        List<Classifier.Recognition> persons = PersonDetectionUtils.getPersons(result);
        if (persons.size() > 0)
            onPersonDetectedSuccess(persons, result.getImageWidth(), result.getImageHeight(), result.getImageRotationDegrees());
        else
            onPersonDetectedFailed();
    }

    private void onPersonDetectedSuccess(List<Classifier.Recognition> results, int imageWidth, int imageHeight, int imageRotationDegrees){
        LinkedHashSet<IPersonsDetectorCallback> callbacks = new LinkedHashSet<>();
        synchronized (CALLBACKS){
            //noinspection CollectionAddAllCanBeReplacedWithConstructor
            callbacks.addAll(CALLBACKS);
        }

        for (IPersonsDetectorCallback callback : callbacks){
            try {
                callback.onPersonDetectedSuccess(results, imageWidth, imageHeight, imageRotationDegrees);
            } catch (Exception e){
                //do nothing
            }
        }
    }

    private void onPersonDetectedFailed(){
        LinkedHashSet<IPersonsDetectorCallback> callbacks = new LinkedHashSet<>();
        synchronized (CALLBACKS){
            //noinspection CollectionAddAllCanBeReplacedWithConstructor
            callbacks.addAll(CALLBACKS);
        }

        for (IPersonsDetectorCallback callback : callbacks){
            try {
                callback.onPersonDetectedFailed();
            } catch (Exception e){
                //do nothing
            }
        }
    }

}
