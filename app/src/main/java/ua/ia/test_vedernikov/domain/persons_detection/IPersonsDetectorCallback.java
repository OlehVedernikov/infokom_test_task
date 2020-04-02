package ua.ia.test_vedernikov.domain.persons_detection;

import java.util.List;

import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;

public interface IPersonsDetectorCallback {

    void onPersonDetectedSuccess(List<Classifier.Recognition> persons, int imageWidth, int imageHeight, int imageRotationDegrees);
    void onPersonDetectedFailed();

}
