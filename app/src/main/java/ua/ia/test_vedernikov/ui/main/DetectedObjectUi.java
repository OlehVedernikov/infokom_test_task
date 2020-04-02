package ua.ia.test_vedernikov.ui.main;

import android.graphics.RectF;

public class DetectedObjectUi {

    private RectF mLocation;

    public DetectedObjectUi(RectF location){
        mLocation = location;
    }

    public RectF getLocation() {
        return mLocation;
    }
}
