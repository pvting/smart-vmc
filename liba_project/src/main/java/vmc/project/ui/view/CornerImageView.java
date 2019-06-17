package vmc.project.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import vmc.project.R;


/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2017/3/13<br>
 * <b>Author:</b> kevin_zhuang<br>
 * <b>Description:</b> <br>
 */
public class CornerImageView extends View {

    /**
     * 没有圆角
     */
    private static final int ALL = 0;

    /**
     * 左上角圆角
     */
    public static final int TOP_LEFT = 1;

    /**
     * 右上角圆角
     */
    public static final int TOP_RIGHT = 2;

    /**
     * 左下角圆角
     */
    public static final int BOTTOM_LEFT = 3;

    /**
     * 右下角圆角
     */
    public static final int BOTTOM_RIGHT = 4;

    /**
     * 哪个位置圆角
     */
    private int CCornerWhich;

    /**
     * 圆角大小
     */
    private int CCornerRadius;

    /**
     * 背景
     */
    private Bitmap CBackground;

    /**
     * 宽
     */
    private int mWidth;

    /**
     * 高
     */
    private int mHeight;

    public CornerImageView(Context context) {
        this(context,null);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasureWidth(widthMeasureSpec);
        mHeight = getMeasureHeight(heightMeasureSpec);
        //设置宽高
        setMeasuredDimension(mWidth, mHeight);

    }

    /**
     * 测量宽度
     * @param measureSpec Spec
     * @return 宽度
     */
    private int getMeasureWidth(int measureSpec) {
        int measureSize = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            measureSize = specSize;
        } else {
            int desireByImg = getPaddingLeft() + getPaddingRight() + CBackground.getWidth();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                measureSize = Math.min(desireByImg, specSize);
            }
        }
        return measureSize;
    }

    /**
     * 测量高度
     * @param measureSpec spec
     * @return 高度
     */
    private int getMeasureHeight(int measureSpec) {
        int measureSize = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            measureSize = specSize;
        } else {
            int desireByImg = getPaddingTop() + getPaddingBottom() + CBackground.getHeight();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                measureSize = Math.min(desireByImg, specSize);
            }
        }
        return measureSize;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //画图
        canvas.drawBitmap(createBitmapByCorner(CBackground, CCornerWhich, CCornerRadius), 0, 0, null);
        super.onDraw(canvas);

    }

    /**
     * 初始化
     * @param context 上下文
     * @param attrs attrs
     * @param defStyle defStyle
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray
                ta =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.cornerImageView, defStyle, 0);

        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.cornerImageView_CBackground) {
                CBackground = BitmapFactory.decodeResource(getResources(), ta.getResourceId(attr, 0));
                if (null == CBackground) {
                    Bitmap bitmap = Bitmap.createBitmap(50, 50,
                                                        Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(ta.getColor(attr, 0));//填充颜色
//                        Log.e("CornerView","color="+ta.getColor(attr,0)+" R.color.color1="+getResources().getColor(R.color.colorMain));
                    CBackground = bitmap;
                }


            } else if (attr == R.styleable.cornerImageView_CCornerRadius) {
                CCornerRadius =
                        ta.getDimensionPixelSize(attr,
                                                 (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                                 10f,
                                                                                 getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.cornerImageView_CCornerWhich) {
                CCornerWhich = ta.getInt(attr, 0);

            }

        }
        ta.recycle();
    }


    /**
     * 创建圆角图
     * @param bitmap bitmap
     * @param cornerWhich 那个角圆角
     * @param radius 圆角大小
     * @return 圆角图
     */
    private Bitmap createBitmapByCorner(Bitmap bitmap, int cornerWhich, int radius) {
        /**
         * 原理很简单，即用两图叠加 使用SRC_OUT的mode
         */
        int width = mWidth;
        int height = mHeight;
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (cornerWhich == ALL) {
            Rect rect = new Rect(0, 0, width, height);
            canvas.drawRect(rect, paint);
        } else if (cornerWhich == TOP_LEFT) {
            Rect rect = new Rect(radius, 0, width, height);
            canvas.drawRect(rect, paint);
            Rect rect2 = new Rect(0, height - radius, width, height);
            canvas.drawRect(rect2, paint);
            RectF rectF = new RectF(0, 0, 2 * radius, height);
            canvas.drawRoundRect(rectF, radius, radius, paint);

        } else if (cornerWhich == TOP_RIGHT) {
            Rect rect = new Rect(0, 0, width - radius, height);
            canvas.drawRect(rect, paint);
            Rect rect2 = new Rect(0, radius, width, height);
            canvas.drawRect(rect2, paint);
            RectF rectF = new RectF(width - 2 * radius, 0, width, height);
            canvas.drawRoundRect(rectF, radius, radius, paint);

        } else if (cornerWhich == BOTTOM_LEFT) {
            Rect rect = new Rect(radius, 0, width, height);
            canvas.drawRect(rect, paint);
            Rect rect2 = new Rect(0, 0, width , radius);
            canvas.drawRect(rect2, paint);
            RectF rectF = new RectF(0, 0, 2*radius, height);
            canvas.drawRoundRect(rectF, radius, radius, paint);

        } else if (cornerWhich == BOTTOM_RIGHT) {

            Rect rect = new Rect(0, 0, width, height - radius);
            canvas.drawRect(rect, paint);
            Rect rect2 = new Rect(0, 0, radius, height);
            canvas.drawRect(rect2, paint);
            RectF rectF = new RectF(0, height - 2 * radius, width, height);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

//        canvas.drawBitmap(bitmap, 0, 0, paint);
//         Rect src = new Rect(0, 0, mWidth, mHeight);
//        canvas.drawBitmap(bitmap, src, src, paint);
        Matrix mMatrix = new Matrix();
        mMatrix.postScale(mWidth,mHeight);
        canvas.drawBitmap(bitmap,mMatrix,paint);
        return target;
    }

    /**
     * 代码设置背景
     * @param color 背景颜色
     */
    public void setCBackground(int color){
        Bitmap bitmap = Bitmap.createBitmap(50, 50,
                                            Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);//填充颜色
        CBackground =  bitmap;
        invalidate();//刷新
    }




}
