/*
 * Copyright (C) 2015 Quinn Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ajb.merchants.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;

import com.ajb.merchants.R;
import com.lidroid.xutils.util.LogUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;


public class SlideSwitch extends CompoundButton {

    public static final int SHAPE_RECT = 1;
    public static final int SHAPE_CIRCLE = 2;
    private static final int RIM_SIZE = 6;
    private static final int DEFAULT_COLOR_THEME = Color.parseColor("#ff00ee00");
    private boolean mHasScrolled = false;//表示是否发生过滚动
    // 3 attributes
    private int color_theme;
    private int shape;
    // varials of drawing
    private Paint paint;
    private Rect backRect;
    private Rect frontRect;
    private RectF frontCircleRect;
    private RectF backCircleRect;
    private int alpha;
    private int max_left;//滑块结束位置
    private int min_left;//滑块开始位置
    private int frontRect_left;//当前位置
    private int frontRect_left_begin = RIM_SIZE;//滑块边距
    private int eventStartX;
    private int eventLastX;
    private int diffX = 0;
    private boolean slideable = true;
    private ValueAnimator toDestAnim;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private OnClickListener onClickListener;

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.slideswitch);
        color_theme = a.getColor(R.styleable.slideswitch_themeColor,
                DEFAULT_COLOR_THEME);
        shape = a.getInt(R.styleable.slideswitch_shape, SHAPE_RECT);
        a.recycle();
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitch(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(280, widthMeasureSpec);
        int height = measureDimension(140, heightMeasureSpec);
        if (shape == SHAPE_CIRCLE) {
            if (width < height)
                width = height * 2;
        }
        setMeasuredDimension(width, height);
        initDrawingVal();
    }

    public void initDrawingVal() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        backCircleRect = new RectF();
        frontCircleRect = new RectF();
        frontRect = new Rect();
        backRect = new Rect(0, 0, width, height);
        min_left = RIM_SIZE;
        if (shape == SHAPE_RECT)
            max_left = width / 2;
        else
            max_left = width - (height - 2 * RIM_SIZE) - RIM_SIZE;  //左侧滑块中心位置
        if (isChecked()) {
            frontRect_left = max_left;
            alpha = 255;
        } else {
            frontRect_left = RIM_SIZE;
            alpha = 0;
        }
        frontRect_left_begin = frontRect_left;
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize; // UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shape == SHAPE_RECT) {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.line_color));
            canvas.drawRect(backRect, paint);
            paint.setColor(color_theme);
            paint.setAlpha(alpha);
            canvas.drawRect(backRect, paint);
            frontRect.set(frontRect_left, RIM_SIZE, frontRect_left
                    + getMeasuredWidth() / 2 - RIM_SIZE, getMeasuredHeight()
                    - RIM_SIZE);
            paint.setColor(Color.WHITE);
            canvas.drawRect(frontRect, paint);
        } else {
            // draw circle
            int radius;

//            radius = backRect.height() / 2 - RIM_SIZE;
            radius = backRect.height() / 2;
            paint.setColor(ContextCompat.getColor(getContext(), R.color.line_color));
            backCircleRect.set(backRect);
            canvas.drawRoundRect(backCircleRect, radius, radius, paint);
//            paint.setColor(Color.WHITE);
//            paint.setAlpha(255 - alpha);
//            RectF frontWhiteRect = new RectF(backCircleRect);
//            frontWhiteRect.left += RIM_SIZE;
//            frontWhiteRect.top += RIM_SIZE;
//            frontWhiteRect.right -= RIM_SIZE;
//            frontWhiteRect.bottom -= RIM_SIZE;
//            canvas.drawRoundRect(frontWhiteRect, radius - RIM_SIZE, radius - RIM_SIZE, paint);
            paint.setColor(color_theme);
            paint.setAlpha(alpha);
            canvas.drawRoundRect(backCircleRect, radius, radius, paint);
            frontRect.set(frontRect_left, RIM_SIZE, frontRect_left
                    + backRect.height() - 2 * RIM_SIZE, backRect.height()
                    - RIM_SIZE);
            frontCircleRect.set(frontRect);
            paint.setColor(Color.WHITE);
//            paint.setShadowLayer(2,2,2,ContextCompat.getColor(getContext(), R.color.line_color));
            canvas.drawRoundRect(frontCircleRect, radius, radius, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        int action = MotionEventCompat.getActionMasked(event);
//        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventStartX = (int) event.getRawX();
                mHasScrolled = false;
                break;
            case MotionEvent.ACTION_MOVE:
                eventLastX = (int) event.getRawX();
                diffX = eventLastX - eventStartX;
                if (Math.abs(diffX) > 10) {
                    mHasScrolled = true;
                }
                int tempX = diffX + frontRect_left_begin;
                //控制滑动范围
                tempX = (tempX > max_left ? max_left : tempX);
                tempX = (tempX < min_left ? min_left : tempX);
                if (tempX >= min_left && tempX <= max_left) {
                    frontRect_left = tempX;
                    alpha = (int) (255 * (float) tempX / (float) max_left);
                    invalidateView();
                    LogUtils.d("frontRect_left=" + frontRect_left);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                moveToDest(isChecked());
                break;
            case MotionEvent.ACTION_UP:
                boolean toRight;
                if (mHasScrolled) {//已经滑动过
                    int wholeX = (int) (event.getRawX() - eventStartX);
                    frontRect_left_begin = frontRect_left;
                    toRight = (frontRect_left_begin > ((min_left + max_left) / 2) ? true : false);
//                    if (Math.abs(wholeX) < 3) {
//                        toRight = !toRight;
//                    }
                } else {
                    toRight = !isChecked();
                }
                moveToDest(toRight);
                if (toRight != isChecked()) {
                    if (onClickListener != null) {
                        onClickListener.onClick(this);
                    }
                }
                mHasScrolled = false;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void setChecked(boolean checked) {
        if (toDestAnim != null && toDestAnim.isRunning()) {
            toDestAnim.cancel();
        }
        if (checked) {
            frontRect_left = max_left;
            alpha = 255;
        } else {
            frontRect_left = min_left;
            alpha = 0;
        }
        super.setChecked(checked);
        invalidateView();
    }

    /**
     * draw again
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void moveToDest(final boolean toRight) {
        toDestAnim = ValueAnimator.ofInt(frontRect_left,
                toRight ? max_left : min_left);
        toDestAnim.setDuration(200);
        toDestAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        toDestAnim.start();
        toDestAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frontRect_left = (Integer) animation.getAnimatedValue();
                if (max_left > RIM_SIZE) {
                    alpha = (int) (255 * (float) (frontRect_left - RIM_SIZE) / (float) (max_left - RIM_SIZE));
                } else {
                    alpha = (int) (255 * (float) frontRect_left / (float) max_left);
                }
                invalidateView();
            }
        });
        toDestAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (toRight) {
                    setChecked(true);
                    frontRect_left_begin = max_left;
                } else {
                    setChecked(false);
                    frontRect_left_begin = min_left;
                }
            }
        });
    }

    public void setShapeType(int shapeType) {
        this.shape = shapeType;
    }

}