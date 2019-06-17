package com.want.vendor.uitls;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

/**
 * SpannableString 构造器
 * @author Miao Xiongfei
 * @date 2016-03-23 18:17
 */
public class SpannableStringBuilder {

    /**
     * eg:
     SpannableStringBuilder builder = new SpannableStringBuilder();
     builder.append("【无格式start 空格】")
     .append("【指定字体颜色，大小】",Color.RED,12);
     builder.append("【指定多种格式，大小45px，字体颜色Green，背景颜色Gray,下划线，删除线，粗斜体】",
     new TextSpan().setAbsoluteSize(50)
     .setUnderline()
     .setStrikethrough()
     .setStyle(Typeface.BOLD_ITALIC)
     .setForegroundColor(Color.GREEN)
     .setBackgroundColor(Color.GRAY));
     //添加图片
     builder.append(new ImageSpan(getResources().getDrawable(R.drawable.ic_launcher)));
     builder.append("【此处可点击】",new TextSpan().setClickable(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    Toast.makeText(SpannableString.this, ((TextView)v).getText().toString(), Toast.LENGTH_LONG).show();
    }
    }));
     builder.append("【无格式end】");
     //设置点击后的颜色为透明，否则会一直出现
     textView.setHighlightColor(Color.TRANSPARENT);
     //开始响应点击事件
     textView.setMovementMethod(LinkMovementMethod.getInstance());
     textView.setText(builder.getSpannableString());
     */

    private android.text.SpannableStringBuilder spannableStringBuilder;

    public SpannableStringBuilder() {
        this.spannableStringBuilder = new android.text.SpannableStringBuilder();
    }

    /**
     * 添加文字，无格式
     * @param str
     * @return
     */
    public SpannableStringBuilder append(String str){
        spannableStringBuilder.append(str);
        return this;
    }

    /**
     * 添加字符串，并指定颜色和字体大小
     * @param str 字符串
     * @param color 字体颜色
     * @param size 字体大小，像素单位
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public SpannableStringBuilder append(String str, int color, int size){
        append(str, new TextSpan().setForegroundColor(color).setAbsoluteSize(size));
        return this;
    }

    /**
     * 添加字符串，并指定颜色
     * @param str 字符串
     * @param color 字体颜色
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public SpannableStringBuilder append(String str, int color){
        append(str, new TextSpan().setForegroundColor(color));
        return this;
    }

    /**
     * 添加格式文本
     * @param str 字符串
     * @param spanText 格式
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public SpannableStringBuilder append(String str, TextSpan spanText){
        if(TextUtils.isEmpty(str)){
            return this;
        }
        if(null == spanText){
            spannableStringBuilder.append(str);
            return this;
        }

        int start,end;
        start = spannableStringBuilder.length();
        spannableStringBuilder.append(str);
        end = spannableStringBuilder.length();
        setSpanText(spanText, start, end);

        return this;
    }

    /**
     * 添加图片
     * @param
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public SpannableStringBuilder append(ImageSpan imageSpan) {
        if (null == imageSpan || null == imageSpan.drawable) {
            return this;
        }
        spannableStringBuilder.append("*");    //先添加一个字符占位，用于被图片替换
        spannableStringBuilder.setSpan(new android.text.style.ImageSpan(imageSpan.drawable, imageSpan.verticalAlignment),
                spannableStringBuilder.length() - 1, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 获取一组字符序列，可用于显示在TextView,EditText
     * @return
     */
    public CharSequence getSpannableString() {
        return spannableStringBuilder;
    }

    @Override
    public String toString() {
        return spannableStringBuilder.toString();
    }

    /**
     * 根据SpanText 已有属性，设置各种样式
     * @param spanText 样式载体
     * @param start 样式文本的开始位置
     * @param end 样式文本的结束位置
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private void setSpanText(TextSpan spanText, int start, int end){
        //1.字体颜色
        if(spanText.foregroundColor != -1){
            spannableStringBuilder.setSpan(new ForegroundColorSpan(spanText.foregroundColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //2.背景色
        if(spanText.backgroundColor != -1 ){
            spannableStringBuilder.setSpan(new BackgroundColorSpan(spanText.backgroundColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //3.字体大小
        if(spanText.size > 0){
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(spanText.size,spanText.isDip), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //4.下划线
        if(spanText.isUnderline == true){
            spannableStringBuilder.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //5.删除线
        if(spanText.isStrikethrough == true){
            spannableStringBuilder.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //6.字体样式,粗体，斜体...
        if(spanText.style != -1){
            spannableStringBuilder.setSpan(new StyleSpan(spanText.style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //7.区块点击事件
        if(spanText.clickableSpan != null){
            spannableStringBuilder.setSpan(spanText.clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }



    /**
     SpannableString属性详解	http://blog.csdn.net/lan410812571/article/details/9083023

     1、BackgroundColorSpan 背景色
     2、ClickableSpan 文本可点击，有点击事件
     3、ForegroundColorSpan 文本颜色（前景色）
     4、MaskFilterSpan 修饰效果，如模糊(BlurMaskFilter)、浮雕(EmbossMaskFilter)
     5、MetricAffectingSpan 父类，一般不用
     6、RasterizerSpan 光栅效果
     7、StrikethroughSpan 删除线（中划线）
     8、SuggestionSpan 相当于占位符
     9、UnderlineSpan 下划线
     10、AbsoluteSizeSpan 绝对大小（文本字体）
     11、DynamicDrawableSpan 设置图片，基于文本基线或底部对齐。
     12、ImageSpan 图片
     13、RelativeSizeSpan 相对大小（文本字体）
     14、ReplacementSpan 父类，一般不用
     15、ScaleXSpan 基于x轴缩放
     16、StyleSpan 字体样式：粗体、斜体等
     17、SubscriptSpan 下标（数学公式会用到）
     18、SuperscriptSpan 上标（数学公式会用到）
     19、TextAppearanceSpan 文本外貌（包括字体、大小、样式和颜色）
     20、TypefaceSpan 文本字体
     21、URLSpan 文本超链接
     */

    public static class TextSpan{
        //	protected String str;
        protected int foregroundColor = -1;
        protected int backgroundColor = -1;

        protected int size = -1;
        protected boolean isDip;

        protected int style = -1;
        protected boolean isStrikethrough;
        protected boolean isUnderline;

        protected ClickableSpan clickableSpan;

        public TextSpan() {
        }

		/*public SpanText(String str) {
			this.str = str;
		}*/

        /**
         * 设置字体颜色（前景色）
         * @param color
         */
        public TextSpan setForegroundColor(int color){
            this.foregroundColor = color;
            return this;
        }

        /**
         * 设置字体背景颜色
         * @param color
         */
        public TextSpan setBackgroundColor(int color){
            this.backgroundColor = color;
            return this;
        }

        /**
         * 设置字体大小
         * @param size 字体大小
         * @param dip 字体大小单位是否为dip,true 表示 dip,false 表示 pixels
         */
		public void setAbsoluteSize(int size, boolean isDip){
			this.size = size;
			this.isDip = isDip;
		}

        /**
         * 设置字体大小，单位为像素
         * @param size
         */
        public TextSpan setAbsoluteSize(int size){
            this.size = size;
            this.isDip = false;
            return this;
        }

        /**
         * 设置样式，粗体、斜体等
         * @param style Typeface.NORMAL,Typeface.BOLD,Typeface.ITALIC,Typeface.BOLD_ITALIC
         */
        public TextSpan setStyle(int style){
            this.style = style;
            return this;
        }

        /**
         * 设置文字显示删除线（中划线）
         */
        public TextSpan setStrikethrough(){
            this.isStrikethrough = true;
            return this;
        }

        /**
         * 设置文字显示下划线
         */
        public TextSpan setUnderline(){
            this.isUnderline = true;
            return this;
        }

        /**
         * 设置文字可以点击
         * @param onClickListener 点击的响应事件
         */
        public TextSpan setClickable(final View.OnClickListener onClickListener){
            this.clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    onClickListener.onClick(widget);
                }
            };
            return this;
        }
    }


    public static class ImageSpan{
        protected Drawable drawable;
        protected int verticalAlignment;

        /**
         * 构造图片Span
         * @param drawable 图片
         * @param verticalAlignment 对齐方式:0,ALIGN_BOTTOM;1,ALIGN_BASELINE
         */
        public ImageSpan(Drawable drawable,int verticalAlignment) {
            this.drawable = drawable;
            this.drawable.setBounds(0, 0, this.drawable.getIntrinsicWidth(), this.drawable.getIntrinsicHeight());
            this.verticalAlignment = verticalAlignment;
        }

        /**
         * 构造图片Span,默认为底部对齐，ALIGN_BOTTOM
         * @param drawable 图片
         */
        public ImageSpan(Drawable drawable) {
            this.drawable = drawable;
            this.drawable.setBounds(0, 0, this.drawable.getIntrinsicWidth(), this.drawable.getIntrinsicHeight());

        }
    }

}
