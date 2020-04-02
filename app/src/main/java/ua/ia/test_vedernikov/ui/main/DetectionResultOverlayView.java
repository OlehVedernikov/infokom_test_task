package ua.ia.test_vedernikov.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collection;
import java.util.HashSet;

import androidx.annotation.Nullable;

public class DetectionResultOverlayView extends View {

    private Collection<DetectedObjectUi> mObjects = new HashSet<>();
    private Paint mFrame;


    public DetectionResultOverlayView(Context context) {
        super(context);

        init();
    }

    public DetectionResultOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init(){
        initPaintFrame();
    }

    private void initPaintFrame(){
        mFrame = new Paint();
        mFrame.setStyle(Paint.Style.STROKE);
        mFrame.setColor(Color.RED);
        mFrame.setStrokeWidth(8);
        mFrame.setStrokeCap(Paint.Cap.ROUND);
    }

    public void updateObjects(@Nullable Collection<DetectedObjectUi> objects){
        mObjects = objects == null ? new HashSet<>() : new HashSet<>(objects);
        invalidate();
    }

    @Override
    public void onDraw(final Canvas canvas) {
        for (DetectedObjectUi object : mObjects)
            drawObject(canvas, object);
    }

    private void drawObject(Canvas canvas, DetectedObjectUi object){
        canvas.drawRect(object.getLocation(), mFrame);
    }

}
