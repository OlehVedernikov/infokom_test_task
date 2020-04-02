package ua.ia.test_vedernikov.data.camera;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import ua.ia.test_vedernikov.R;

public enum CameraInfokom {

    CameraBack(false, R.string.back_camera_label),
    CameraFront(true, R.string.front_camera_label);

    private @StringRes int mLabelIdInResources;
    private boolean mIsFront;

    CameraInfokom(boolean isFront, @StringRes int labelIdInResources){
        mIsFront = isFront;
        mLabelIdInResources = labelIdInResources;
    }

    public boolean isFront(){
        return mIsFront;
    }

    public class Entry {
        private final Context mContext;

        public Entry(final Context context) {
            mContext = context;
        }

        public String getLabel(){
            return mContext.getString(mLabelIdInResources);
        }

        public boolean isFront(){
            return mIsFront;
        }

        @NonNull
        @Override
        public String toString() {
            return mContext.getString(mLabelIdInResources);
        }

    }
}
