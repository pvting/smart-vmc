package vmc.project.ui.view;

import android.content.Context;
import android.widget.Scroller;


public class IndicatorScroller extends Scroller {
    private int mDuration = 1000;

    public IndicatorScroller(Context context, int duration) {
        super(context);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}