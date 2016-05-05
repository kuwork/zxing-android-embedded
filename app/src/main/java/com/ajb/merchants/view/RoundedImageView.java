package com.ajb.merchants.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lidroid.xutils.bitmap.core.AsyncDrawable;

public class RoundedImageView extends ImageView {
    public Context context;

    public RoundedImageView(Context context) {
        super(context);
        this.context = context;
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
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
        Bitmap b = null;
        if (drawable instanceof BitmapDrawable) {
            b = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof AsyncDrawable) {
            //xUtils 解决同步缓存奔溃问题
            b = Bitmap
                    .createBitmap(
                            getWidth(),
                            getHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas1 = new Canvas(b);
            // canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, getWidth(),
                    getHeight());
            drawable.draw(canvas1);
        }
        Bitmap bitmap = b.copy(Config.ARGB_8888, true);
        int d = 0;
        if (w >= h) {
            d = (h % 2 == 0 ? h : (h - 1));
        } else {
            d = (w % 2 == 0 ? w : (w - 1));
        }
        d = d - Math.max(getPaddingLeft(), getPaddingRight());
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
                int x = (bmp.getWidth() - bmp.getHeight()) >> 1;
                c.drawBitmap(bmp, -x, 0, null);
                sbmp = Bitmap.createScaledBitmap(sbmp2, d, d, false);
                sbmp2.recycle();
                bmp.recycle();
            } else {
                Bitmap sbmp2 = Bitmap.createBitmap(bmp.getWidth(),
                        bmp.getWidth(), Config.ARGB_8888);
                Canvas c = new Canvas(sbmp2);
                int y = (bmp.getHeight() - bmp.getWidth()) >> 1;
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

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() >> 1,
                sbmp.getHeight() >> 1, (d >> 1) - 5, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
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

}
