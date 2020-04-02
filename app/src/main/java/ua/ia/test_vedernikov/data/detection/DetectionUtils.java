package ua.ia.test_vedernikov.data.detection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;

class DetectionUtils {

    static List<Classifier.Recognition> getConfidenceResults(Collection<Classifier.Recognition> all, float confidence){
        List<Classifier.Recognition> res = new ArrayList<>();
        for (Classifier.Recognition recognition : all)
            if (recognition.getConfidence() >= confidence)
                res.add(recognition);
        return res;
    }

}
