package com.ajb.merchants.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ajb.merchants.R;
import com.util.DensityUtil;

public class RoundedConnerImageView extends ImageView {
    public Context context;
    /**
     * 图片宽和高的比例
     */
    private float ratio;

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public RoundedConnerImageView(Context context) {
        super(context);
        this.context = context;
        init(context, null);
        // TODO Auto-generated constructor stub
    }

    public RoundedConnerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    public RoundedConnerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        Bitmap bitmap = b.copy(Config.ARGB_8888, true);
        int d = 0;
        if (w >= h) {
            d = (h % 2 == 0 ? h - 2 : (h - 3));
        } else {
            d = (w % 2 == 0 ? w - 2 : (w - 3));
        }
        Bitmap roundBitmap = getCroppedBitmap(context, bitmap, d);
        canvas.drawBitmap(roundBitmap, 0, 0, null);

//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
//
//        canvas.drawCircle(w / 2, h / 2, r, paint);


    }

    public static Bitmap getCroppedBitmap(Context context, Bitmap bmp,
                                          int d) {
        Bitmap sbmp = null;
        if (bmp.getWidth() != d || bmp.getHeight() != d) {
            if (bmp.getWidth() == bmp.getHeight()) {
                sbmp = Bitmap.createScaledBitmap(bmp, d, d, false);
            } else if (bmp.getWidth() > bmp.getHeight()) {
                Bitmap sbmp2 = Bitmap.createBitmap(bmp.getHeight(),
                        bmp.getHeight(), Config.ARGB_8888);
                Canvas c = new Canvas(sbmp2);
                int x = (bmp.getWidth() - bmp.getHeight()) / 2;
                c.drawBitmap(bmp, -x, 0, null);
                sbmp = Bitmap.createScaledBitmap(sbmp2, d, d, false);
                sbmp2.recycle();
                bmp.recycle();
            } else {
                Bitmap sbmp2 = Bitmap.createBitmap(bmp.getWidth(),
                        bmp.getWidth(), Config.ARGB_8888);
                Canvas c = new Canvas(sbmp2);
                int y = (bmp.getHeight() - bmp.getWidth()) / 2;
                c.drawBitmap(bmp, 0, -y, null);
                sbmp = Bitmap.createScaledBitmap(sbmp2, d, d, false);
                sbmp2.recycle();
                bmp.recycle();
            }
        } else {
            sbmp = bmp;
        }
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
        RectF rectF = new RectF(0, 0, sbmp.getHeight(), sbmp.getWidth());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
//        canvas.drawCircle(sbmp.getWidth() / 2,
//                sbmp.getHeight() / 2, d / 2, paint);
        canvas.drawRoundRect(rectF, DensityUtil.dp2px(context, 10), DensityUtil.dp2px(context, 10), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
//        canvas.save();
//        Paint whitePaint = new Paint();
//        whitePaint.setAntiAlias(true);
//        whitePaint.setFilterBitmap(true);
//        whitePaint.setDither(true);
//        whitePaint.setColor(Color.WHITE);
//        whitePaint.setStyle(Paint.Style.STROKE);
//        whitePaint.setStrokeWidth(CommonUtils.dip2px_(context, 4));
//        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
//                sbmp.getHeight() / 2 + 0.7f,
//                sbmp.getWidth() / 2 - CommonUtils.dip2px_(context, 2),
//                whitePaint);
//        canvas.restore();

        return output;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 父容器传过来的高度方向上的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft()
                - getPaddingRight();

        if (widthMode == MeasureSpec.EXACTLY
                && heightMode != MeasureSpec.EXACTLY && ratio != 0.0f) {
            // 判断条件为，宽度模式为Exactly，也就是填充父窗体或者是指定宽度；
            // 且高度模式不是Exaclty，代表设置的既不是fill_parent也不是具体的值，于是需要具体测量
            // 且图片的宽高比已经赋值完毕，不再是0.0f
            // 表示宽度确定，要测量高度
            height = (int) (width / ratio + 0.5f);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        } else if (widthMode != MeasureSpec.EXACTLY
                && heightMode == MeasureSpec.EXACTLY && ratio != 0.0f) {
            // 判断条件跟上面的相反，宽度方向和高度方向的条件互换
            // 表示高度确定，要测量宽度
            width = (int) (height * ratio + 0.5f);

            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                    MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) {
            setRatio(800f / 600f);
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.scaleImageViewAttr);
        float h = ta.getInteger(R.styleable.scaleImageViewAttr_expectH, 800);
        float w = ta.getInteger(R.styleable.scaleImageViewAttr_expectW, 600);
        setRatio(w / h);
        ta.recycle();
    }

}
