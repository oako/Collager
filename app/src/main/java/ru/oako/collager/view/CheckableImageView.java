package ru.oako.collager.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * ImageView with ability to check/uncheck view
 * Created by Alexei on 25.07.2014.
 */
public class CheckableImageView extends ImageView implements Checkable {
    private boolean mChecked = false;
    private static final int CHECKED_COLOR = 0x804b9ecd;

    public CheckableImageView(Context context) {
        super(context);
    }

    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void refreshDrawableState() {
        if (mChecked) {
            setColorFilter(CHECKED_COLOR, PorterDuff.Mode.SRC_ATOP);
        } else {
            clearColorFilter();
        }
    }

    @Override
    public void setChecked(boolean b) {
        mChecked = b;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
        refreshDrawableState();
    }
}
