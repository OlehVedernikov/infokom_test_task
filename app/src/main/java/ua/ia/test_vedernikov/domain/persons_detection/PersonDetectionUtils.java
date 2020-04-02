package ua.ia.test_vedernikov.domain.persons_detection;

import java.util.ArrayList;
import java.util.List;

import ua.ia.test_vedernikov.data.detection.RecognitionResult;
import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;

class PersonDetectionUtils {

    private static final String OBJECT_TITLE = "person";

    static List<Classifier.Recognition> getPersons(RecognitionResult recognitionResult){
        List<Classifier.Recognition> res = new ArrayList<>();
        for (Classifier.Recognition detectionResult : recognitionResult.getConfidenceResult())
            if (detectionResult.getTitle().equals(OBJECT_TITLE))
                res.add(detectionResult);
        return res;
    }

}
