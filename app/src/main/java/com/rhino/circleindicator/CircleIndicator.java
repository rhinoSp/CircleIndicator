package com.rhino.circleindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.rhino.circleindicator.R;


/**
 * <p>The custom indicator for page.</p>
 *Follow this example:
 *
 * <pre class="prettyprint">
 * &lt;?xml version="1.0" encoding="utf-8"?&gt</br>
 * &lt;RelativeLayout
 *      xmlns:android="http://schemas.android.com/apk/res/android"
 *      xmlns:app="http://schemas.android.com/apk/res-auto"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"&gt
 *
 *      &lt;com.rhino.view.CircleIndicator
 *          android:id="@+id/CircleIndicator"
 *          android:layout_width="match_parent"
 *          android:layout_height="30dp"
 *          android:layout_alignParentBottom="true"
 *          android:clickable="false"
 *          app:ci_mode="outside"
 *          app:ci_orientation="horizontal"
 *          app:ci_radius="8dp"
 *          app:ci_count="5"
 *          app:ci_margin="8dp"
 *          app:ci_normalColor="#444444"
 *          app:ci_selectedColor="#ffe6454a"/&gt
 *
 *&lt;/com.rhino.rl.pr.PullRefreshLayout&gt
 *</pre>
 * @since Created by LuoLin on 2018/1/8.
 **/
public class CircleIndicator extends View {

    private static final String TAG = "CircleIndicator";

    // default value
    private final int DEFAULT_INDICATOR_MODE = Mode.SOLO.ordinal();
    private final int DEFAULT_INDICATOR_ORIENTATION = Orientation.HORIZONTAL.ordinal();
    private final int DEFAULT_INDICATOR_COUNT = 3;
    private final int DEFAULT_INDICATOR_RADIUS = 10;
    private final int DEFAULT_INDICATOR_MARGIN = 20;
    private final int DEFAULT_INDICATOR_NORMAL_COLOR = Color.GRAY;
    private final int DEFAULT_INDICATOR_SELECTED_COLOR = Color.BLACK;
    private Orientation mOrientation = Orientation.values()[DEFAULT_INDICATOR_ORIENTATION];
    private Mode mMode = Mode.values()[DEFAULT_INDICATOR_MODE];
    private int mCount = DEFAULT_INDICATOR_COUNT;
    private float mRadius = DEFAULT_INDICATOR_RADIUS;
    private float mMargin = DEFAULT_INDICATOR_MARGIN;
    @ColorInt
    private int mNormalColor = DEFAULT_INDICATOR_NORMAL_COLOR;
    @ColorInt
    private int mSelectedColor = DEFAULT_INDICATOR_SELECTED_COLOR;

    public enum Mode {
        INSIDE,
        OUTSIDE,
        SOLO
    }
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    private int mViewWidth;
    private int mViewHeight;
    private Paint mPaint;
    private Path mPath;
    private float mIndicatorSelectX;
    private float mIndicatorSelectY;
    private int mCurrentSelectPosition;

    public CircleIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = getWidth();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = getHeight();
        }
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        drawIndicators(canvas);
        drawSelectIndicator(canvas);
        canvas.restore();
    }

    /**
     * Do something init.
     * @param context Context
     * @param attrs AttributeSet
     */
    private void init(Context context, AttributeSet attrs){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        if(attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator);
        int mode = typedArray.getInt(R.styleable.CircleIndicator_ci_mode, DEFAULT_INDICATOR_MODE);
        mMode = Mode.values()[mode];
        int orientation = typedArray.getInt(R.styleable.CircleIndicator_ci_orientation, DEFAULT_INDICATOR_ORIENTATION);
        mOrientation = Orientation.values()[orientation];
        mCount = typedArray.getInt(R.styleable.CircleIndicator_ci_count, DEFAULT_INDICATOR_COUNT);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_radius, DEFAULT_INDICATOR_RADIUS);
        mMargin = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, DEFAULT_INDICATOR_MARGIN);
        mNormalColor = typedArray.getColor(R.styleable.CircleIndicator_ci_normalColor,
                DEFAULT_INDICATOR_NORMAL_COLOR);
        mSelectedColor = typedArray.getColor(R.styleable.CircleIndicator_ci_selectedColor,
                DEFAULT_INDICATOR_SELECTED_COLOR);
        typedArray.recycle();
    }

    /**
     * Draw the all indicator.
     * @param canvas canvas
     */
    private void drawIndicators(Canvas canvas) {
        canvas.save();
        mPaint.setColor(mNormalColor);
        float cx = mViewWidth / 2;
        float cy = mViewHeight / 2;
        for (int i = 0; i < mCount; i++) {
            if (Orientation.HORIZONTAL == mOrientation) {
                canvas.drawCircle(calculateIndicatorX(i), cy, mRadius, mPaint);
            } else {
                canvas.drawCircle(cx, calculateIndicatorY(i), mRadius, mPaint);
            }
        }
        canvas.restore();
    }

    /**
     * Draw the select indicator.
     * @param canvas canvas
     */
    private void drawSelectIndicator(Canvas canvas) {
        canvas.save();
        mPaint.setColor(mSelectedColor);
        float cx = mViewWidth / 2;
        float cy = mViewHeight / 2;
        if (Mode.INSIDE == mMode) {
            mPath.reset();
            for (int i = 0; i < mCount; i++) {
                if (Orientation.HORIZONTAL == mOrientation) {
                    mPath.addCircle(calculateIndicatorX(i), cy, mRadius, Path.Direction.CW);
                } else {
                    mPath.addCircle(cx, calculateIndicatorY(i), mRadius, Path.Direction.CW);
                }
            }
            canvas.clipPath(mPath);
        }
        if (0 == mIndicatorSelectX || 0 == mIndicatorSelectY) {
            if (Orientation.HORIZONTAL == mOrientation) {
                mIndicatorSelectX = calculateIndicatorX(mCurrentSelectPosition);
                mIndicatorSelectY = cy;
            } else {
                mIndicatorSelectX = cx;
                mIndicatorSelectY = calculateIndicatorY(mCurrentSelectPosition);
            }
        }
        canvas.drawCircle(mIndicatorSelectX, mIndicatorSelectY, mRadius, mPaint);
        canvas.restore();
    }

    /**
     * Calculate the position indicator x.
     * @param position The position of page.
     * @return x
     */
    private float calculateIndicatorX(int position) {
        float indicatorRealWidth = mCount * mRadius * 2
                + (mCount-1) * mMargin;
        float margin = position * mMargin + position * mRadius * 2;
        return mViewWidth / 2 - indicatorRealWidth / 2 + mRadius + margin;
    }

    /**
     * Calculate the position indicator y.
     * @param position The position of page.
     * @return x
     */
    private float calculateIndicatorY(int position) {
        float indicatorRealHeight = mCount * mRadius * 2
                + (mCount-1) * mMargin;
        float margin = position * mMargin + position * mRadius * 2;
        return mViewHeight / 2 - indicatorRealHeight / 2 + mRadius + margin;
    }

    /**
     * This method will be invoked when the dest page is selected.
     *
     * @param position The position of current page selected.
     */
    public void setPosition(int position) {
        Log.d(TAG, "setPosition position = " + position);
        this.mCurrentSelectPosition = position;
        if (Orientation.HORIZONTAL == mOrientation) {
            float currentIndicatorX = calculateIndicatorX(position);
            if (currentIndicatorX != mIndicatorSelectX) {
                mIndicatorSelectX = currentIndicatorX;
                invalidate();
            }
        } else {
            float currentIndicatorY = calculateIndicatorY(position);
            if (currentIndicatorY != mIndicatorSelectY) {
                mIndicatorSelectY = currentIndicatorY;
                invalidate();
            }
        }
    }

    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position The position of next page that will be show.
     * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
     * @see Mode#INSIDE
     * @see Mode#OUTSIDE
     */
    public void setPosition(int position, float positionOffset) {
        Log.d(TAG, "setPosition position = " + position + ", positionOffset = " + positionOffset);
        if (0 >= positionOffset) {
            // scroll end
            mCurrentSelectPosition = position;
        }
        if (mMode == Mode.SOLO) {
            setPosition(mCurrentSelectPosition);
        } else {
            if (Orientation.HORIZONTAL == mOrientation) {
                float destIndicatorX = calculateIndicatorX(position);
                float towIndicatorDistance = mRadius * 2 + mMargin;
                mIndicatorSelectX = destIndicatorX + positionOffset * towIndicatorDistance;
                invalidate();
            } else {
                float destIndicatorY = calculateIndicatorY(position);
                float towIndicatorDistance = mRadius * 2 + mMargin;
                mIndicatorSelectY = destIndicatorY + positionOffset * towIndicatorDistance;
                invalidate();
            }
        }
    }

    /**
     * Returns the current orientation of the layout.
     *
     * @return Current orientation,  either {@link Orientation#HORIZONTAL} or {@link Orientation#VERTICAL}
     * @see #setOrientation(Orientation)
     */
    public Orientation getOrientation() {
        return mOrientation;
    }

    /**
     * Sets the orientation of the indicator.
     *
     * @param orientation {@link Orientation#HORIZONTAL} or {@link Orientation#VERTICAL}
     */
    public void setOrientation(Orientation orientation) {
        if (orientation != Orientation.HORIZONTAL && orientation != Orientation.VERTICAL) {
            throw new IllegalArgumentException("invalid orientation:" + orientation);
        }
        if (orientation == mOrientation) {
            return;
        }
        mOrientation = orientation;
        invalidate();
    }

    /**
     * Set the mode of indicator.
     * @param mode The new mode of indicator.
     * @see Mode#INSIDE
     * @see Mode#OUTSIDE
     * @see Mode#SOLO
     */
    public void setMode(Mode mode) {
        if (mode != mMode) {
            this.mMode = mode;
            invalidate();
        }
    }

    /**
     * Set the count of indicator.
     * @param count The count of indicator.
     */
    public void setCount(int count) {
        if (0 >= count) {
            throw new RuntimeException("ERROR: invalid count.");
        }
        if (count != mCount) {
            this.mCount = count;
            invalidate();
        }
    }

    /**
     * Set the radius of indicator.
     * @param radius The radius of indicator.
     */
    public void setRadius(float radius) {
        if (radius != mRadius) {
            this.mRadius = radius;
            invalidate();
        }
    }

    /**
     * Set the margin between tow indicator.
     * @param margin The margin between tow indicator.
     */
    public void setMargin(float margin) {
        if (margin != mMargin) {
            this.mMargin = margin;
            invalidate();
        }
    }

    /**
     * Set the color of normal indicator.
     * @param normalColor The color of normal indicator.
     */
    public void setNormalColor(@ColorInt int normalColor) {
        if (normalColor != mNormalColor) {
            this.mNormalColor = normalColor;
            invalidate();
        }
    }

    /**
     * Set the color of selected indicator.
     * @param selectedColor The color of selected indicator.
     */
    public void setSelectedColor(@ColorInt int selectedColor) {
        if (selectedColor != mSelectedColor) {
            this.mSelectedColor = selectedColor;
            invalidate();
        }
    }
}
