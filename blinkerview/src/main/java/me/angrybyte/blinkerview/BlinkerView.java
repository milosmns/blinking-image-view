package me.angrybyte.blinkerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The core class of the Blinker view's implementation. This view allows the user to set a drawable to it,
 * and would blink the drawable (show/hide) periodically using multiple configuration options.
 */
public class BlinkerView extends View {

    public static final int SCALE_STRETCH = 0;
    public static final int SCALE_CONSTRAIN = 1;
    public static final int SCALE_CENTER = 2;

    private static final String TAG = BlinkerView.class.getSimpleName();
    private static final int DEF_DURATION = 500; // ms

    @Retention(SOURCE)
    @IntDef({SCALE_STRETCH, SCALE_CONSTRAIN, SCALE_CENTER})
    public @interface ScaleType {}

    @Nullable
    private Drawable mDrawable;
    @ScaleType
    private int mScaleType = SCALE_STRETCH;
    private int mDuration = DEF_DURATION;
    private boolean mAutostart;
    private boolean mFade = true;
    private Rect mDrawableBounds = new Rect();

    // <editor-fold desc="Constructors">
    public BlinkerView(final @NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public BlinkerView(final @NonNull Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public BlinkerView(final @NonNull Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BlinkerView(final @NonNull Context context, @Nullable final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }
    // </editor-fold>

    private void init(final @NonNull Context context, @Nullable final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BlinkerView, defStyleAttr, defStyleRes);

        mDrawable = attributes.getDrawable(R.styleable.BlinkerView_blink_drawable);
        mScaleType = attributes.getInteger(R.styleable.BlinkerView_blink_scale_type, mScaleType);
        mDuration = attributes.getInteger(R.styleable.BlinkerView_blink_duration, mDuration);
        mAutostart = attributes.getBoolean(R.styleable.BlinkerView_blink_autostart, mAutostart);
        mFade = attributes.getBoolean(R.styleable.BlinkerView_blink_use_fading, mFade);

        attributes.recycle();
    }

    @ScaleType
    @SuppressWarnings("unused")
    public int getScaleType() {
        return mScaleType;
    }

    @SuppressWarnings("unused")
    public void setScaleType(@ScaleType final int scaleType) {
        mScaleType = scaleType;
        updateDrawableBounds();
        invalidate();
    }

    @Nullable
    @SuppressWarnings("unused")
    public Drawable getDrawable() {
        return mDrawable;
    }

    @SuppressWarnings("unused")
    public void setDrawable(@Nullable final Drawable drawable) {
        mDrawable = drawable;
        updateDrawableBounds();
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // TODO start blinking if blinking was enabled
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldW, final int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateDrawableBounds();
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateDrawableBounds();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable != null) {
            if (isInEditMode()) {
                mDrawable.draw(canvas);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // TODO stop blinking
    }

    /* Private helpers */

    @NonNull
    private Rect getPadding() {
        return new Rect(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? getPaddingStart() : getPaddingLeft(),
                getPaddingTop(),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? getPaddingEnd() : getPaddingRight(),
                getPaddingBottom()
        );
    }

    private void updateDrawableBounds() {
        if (mDrawable == null) return;

        final int intrinsicW, intrinsicH;
        switch (mScaleType) {
            case SCALE_STRETCH:
                applyStretchBounds();
                break;
            case SCALE_CONSTRAIN:
                intrinsicW = mDrawable.getIntrinsicWidth();
                intrinsicH = mDrawable.getIntrinsicHeight();
                if (intrinsicW == -1 || intrinsicH == -1) {
                    Log.w(TAG, "Drawable has no intrinsic width/height, stretching...");
                    applyStretchBounds();
                } else {
                    applyConstrainBounds(intrinsicW, intrinsicH);
                }
                break;
            case SCALE_CENTER:
                intrinsicW = mDrawable.getIntrinsicWidth();
                intrinsicH = mDrawable.getIntrinsicHeight();
                if (intrinsicW == -1 || intrinsicH == -1) {
                    Log.w(TAG, "Drawable has no intrinsic width/height, stretching...");
                    applyStretchBounds();
                } else {
                    applyCenterBounds(intrinsicW, intrinsicH);
                }
                break;
            default:
                Log.e(TAG, "Drawable bounds not updated, unknown scale type: " + mScaleType);
                break;
        }
        mDrawable.setBounds(mDrawableBounds);
    }

    private void applyStretchBounds() {
        final Rect padding = getPadding();
        mDrawableBounds.left = padding.left;
        mDrawableBounds.top = padding.top;
        mDrawableBounds.right = getWidth() - padding.right;
        mDrawableBounds.bottom = getHeight() - padding.bottom;
    }

    private void applyConstrainBounds(final int intrinsicW, final int intrinsicH) {
        final double whRatio = (double) intrinsicW / (double) intrinsicH;
        final Rect padding = getPadding();
        final int width, height;
        if (whRatio >= 1d) {
            // landscape ratio (maximize width)
            width = getWidth() - padding.left - padding.right;
            height = (int) Math.round((double) width / whRatio);
            mDrawableBounds.left = padding.left;
            mDrawableBounds.top = getHeight() / 2 - height / 2;
            mDrawableBounds.right = getWidth() - padding.right;
            mDrawableBounds.bottom = getHeight() - mDrawableBounds.top;
        } else {
            // portrait ratio (maximize height)
            height = getHeight() - padding.top - padding.bottom;
            width = (int) Math.round((double) height * whRatio);
            mDrawableBounds.left = getWidth() / 2 - width / 2;
            mDrawableBounds.top = padding.top;
            mDrawableBounds.right = getWidth() - mDrawableBounds.left;
            mDrawableBounds.bottom = getHeight() - padding.bottom;
        }
    }

    private void applyCenterBounds(final int intrinsicW, final int intrinsicH) {
        mDrawableBounds.left = getWidth() / 2 - intrinsicW / 2;
        mDrawableBounds.top = getHeight() / 2 - intrinsicH / 2;
        mDrawableBounds.right = getWidth() / 2 + intrinsicW / 2;
        mDrawableBounds.bottom = getHeight() / 2 + intrinsicH / 2;
    }

}
