package com.ajb.merchants.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ajb.merchants.util.FastBlur;
import com.lidroid.xutils.bitmap.core.AsyncDrawable;

public class BlurImageView extends ImageView {
    public Context context;

    public BlurImageView(Context context) {
        super(context);
        this.context = context;
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        canvas.drawBitmap(bitmap, 0, 0, null);

    }

    public static Bitmap getCroppedBitmap(Context context, Bitmap bmp,
                                          int d) {
        Bitmap sbmp = null;
        if (bmp.getWidth() != d || bmp.getHeight() != d) {
            if (bmp.getWidth() == bmp.getHeight()) {
                sbmp = Bitmap.createScaledBitmap(bmp, d, d, false);
            } else if (bmp.getWidth() > bmp.getHeight()) {
                Bitmap sbmp2 = Bitmap.createBitmap(bmp.getHeight(),
                        bmp.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(sbmp2);
                int x = (bmp.getWidth() - bmp.getHeight()) >> 1;
                c.drawBitmap(bmp, -x, 0, null);
                sbmp = Bitmap.createScaledBitmap(sbmp2, d, d, false);
                sbmp2.recycle();
                bmp.recycle();
            } else {
                Bitmap sbmp2 = Bitmap.createBitmap(bmp.getWidth(),
                        bmp.getWidth(), Bitmap.Config.ARGB_8888);
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
        return FastBlur.doBlur(sbmp, 15, true);
    }

}
