package vmc.vendor.utils;

import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.want.base.sdk.framework.app.view.ViewCompatEx;
import com.want.imageloader.ImageLoader;
import com.want.imageloader.OnLoadCallback;

import vmc.core.log;
import vmc.project.R;

/**
 * <b>Create Date:</b> 26/10/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class DataBindingAdapter {
    private static final boolean DEBUG = true;

    private DataBindingAdapter() {
        //no instance
    }

    /**
     * 通过DataBinding为图片配置Bitmap对象
     *
     * @param view
     * @param bitmap
     */
    @BindingAdapter(value = {"imageBitmap"}, requireAll = true)
    public static void imageBitmap(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter(value = {"drawableLeftSrc", "drawableTopSrc", "drawableRightSrc", "drawableBottomSrc"},
                    requireAll = false)
    public static void compoundDrawable(TextView textView, int left, int top, int right, int bottom) {
        final Resources res = textView.getResources();
        final Drawable dLeft = getDrawable(res, left);
        final Drawable dTop = getDrawable(res, top);
        final Drawable dRight = getDrawable(res, right);
        final Drawable dBottom = getDrawable(res, bottom);
        textView.setCompoundDrawables(dLeft, dTop, dRight, dBottom);
    }

    private static Drawable getDrawable(Resources res, int src) {
        if (0 == src) {
            return null;
        }
        final Drawable drawable = res.getDrawable(src);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

    @BindingAdapter(value = {"image_url", "image_scale", "image_callback", "image_loading", "image_error"}, requireAll = false)
    public static void image(View view, String url, boolean scale, boolean callback, int loading, int error) {
        image(view, url, error, loading, scale, callback);
    }


    private static void image(final View view,
                              String url,
                              int error,
                              int loading,
                              final boolean scale,
                              boolean callback) {
        ImageLoader.Builder builder = new ImageLoader.Builder();
        builder.url(url)
               .with(view.getContext().getApplicationContext())
               .view(view);

        if (callback) {
            builder.callback(new OnLoadCallback() {
                @Override
                public void onLoadCallback(final Drawable drawable) {
                    if (scale) {
                        final Bitmap bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
                        final int width = bitmap.getWidth();
                        final int height = bitmap.getHeight();
                        final int pWidth = view.getWidth();

                        if (0 != pWidth) {
                            final float scale = pWidth / (float) width;
                            final ViewGroup.LayoutParams lp = view.getLayoutParams();
                            lp.height = (int) (scale * height + 0.5f);
                            view.setLayoutParams(lp);

                            if (DEBUG) {
                                log.v("image", "image loaded, Image width: " + width + ", height: " + height);
                                log.v("image",
                                      "image loaded, View width: " + pWidth + ", height: " + lp.height);
                            }
                        } else {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    onLoadCallback(drawable);
                                }
                            });
                        }
                    }

                    if (view instanceof ImageView) {
                        ((ImageView) view).setImageDrawable(drawable);
                    } else {
                        ViewCompatEx.setBackground(view, drawable);
                    }
                }
            });
        }

        if (0 != error) {
            builder.error(error);
        } else {
            builder.error(R.drawable.default_image);
        }

        if (0 != loading) {
            builder.loading(loading);
        } else {
            builder.error(R.drawable.default_image);
        }

        builder.build().load();
    }
}
