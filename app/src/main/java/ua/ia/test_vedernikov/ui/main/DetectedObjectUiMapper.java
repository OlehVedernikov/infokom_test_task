package ua.ia.test_vedernikov.ui.main;

import android.graphics.RectF;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import androidx.annotation.Nullable;
import ua.ia.test_vedernikov.data.repositories.tflite.Classifier;

class DetectedObjectUiMapper {

    static final int DETECTED_PEOPLE_UNDEFINED_INT = -1;
    private static final String DETECTED_PERSONS_UNDEFINED_STR = "-";

    static String getValueDetectedPersonsForDisplay(@Nullable Collection<Classifier.Recognition> persons, boolean detectionActive){
        int personsCount = getPersonsCountForDisplay(persons, detectionActive);
        return personsCount == DETECTED_PEOPLE_UNDEFINED_INT ?
                DETECTED_PERSONS_UNDEFINED_STR :
                String.valueOf(personsCount);
    }

    private static int getPersonsCountForDisplay(@Nullable Collection<Classifier.Recognition> persons, boolean detectionActive){
        return detectionActive ?
                persons == null ? 0 : persons.size():
                DETECTED_PEOPLE_UNDEFINED_INT;
    }

    static Collection<DetectedObjectUi> mapObjects(@Nullable List<Classifier.Recognition> objects,
                                                   int unscaledImageWidth, int unscaledImageHeight, int unscaledImageRotationDegrees,
                                                   int imageWidth, int imageHeight){
        if (objects == null || imageWidth == 0 || imageHeight == 0)
            return new HashSet<>();

        Collection<DetectedObjectUi> res = new LinkedHashSet<>();
        for (Classifier.Recognition object : objects){
            DetectedObjectUi mapped = mapObject(object, unscaledImageWidth, unscaledImageHeight, unscaledImageRotationDegrees, imageWidth, imageHeight);
            if (object != null)
                res.add(mapped);
        }
        return res;
    }

    @Nullable
    private static DetectedObjectUi mapObject(Classifier.Recognition object, int unscaledImageWidth, int unscaledImageHeight, int unscaledImageRotationDegrees,
                                              int imageWidth, int imageHeight){
        try {
            RectF scaledLocation = scaleLocation(object.getLocation(), unscaledImageWidth, unscaledImageHeight, unscaledImageRotationDegrees, imageWidth, imageHeight);
            return new DetectedObjectUi(scaledLocation);
        } catch (Exception e){
            return null;
        }
    }

    @Nullable
    private static RectF scaleLocation(RectF unscaled, int unscaledImageWidth, int unscaledImageHeight, @SuppressWarnings("unused") int unscaledImageRotationDegrees,
                                       int imageWidth, int imageHeight){
        try {
            float scaleFactorX = (float) imageWidth / unscaledImageWidth;
            float scaleFactorY = (float) imageHeight / unscaledImageHeight;

            float left = unscaled.left * scaleFactorX;
            float top = unscaled.top * scaleFactorY;
            float right = unscaled.right * scaleFactorX;
            float bottom = unscaled.bottom * scaleFactorY;

            return new RectF(left, top, right, bottom);
        } catch (Exception e){
            return null;
        }
    }
}
