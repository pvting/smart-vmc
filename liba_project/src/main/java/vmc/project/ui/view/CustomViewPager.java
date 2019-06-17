package vmc.project.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * <b>Create Date:</b> 9/06/16<br>
 * <b>Author:</b> likun<br>
 * <b>Description:</b>
 * 不可以滑动，但是可以setCurrentItem的ViewPager。
 * <br>
 */
public class CustomViewPager extends ViewPager {

//    private boolean scrollble = true;
//
//    private boolean isCanScroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (!scrollble) {
//            return true;
//        }
//        return super.onTouchEvent(ev);
//    }
//
//
//    public boolean isScrollble() {
//        return scrollble;
//    }
//
//    public void setCanScroll(boolean isCanScroll){
//        this.isCanScroll = isCanScroll;
//    }
//
//    /**
//     * 设置开启/禁用滑动
//     *
//     * @param scrollble true 开启 false 禁用
//     */
//    public void setScrollble(boolean scrollble) {
//        this.scrollble = scrollble;
//    }
//
//    @Override
//    public void scrollTo(int x, int y){
//        if (isCanScroll){
//            super.scrollTo(x, y);
//        }
//    }
}
