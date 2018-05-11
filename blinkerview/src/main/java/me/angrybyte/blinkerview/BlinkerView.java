package me.angrybyte.blinkerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.lang.annotation.Retention;

import static android.animation.ValueAnimator.AnimatorListener;
import static android.animation.ValueAnimator.AnimatorUpdateListener;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The core class of the Blinker view's implementation. This view allows the user to set a drawable to it,
 * and would blink the drawable (show/hide) periodically using multiple configuration options.
 */
public class BlinkerView extends View {

    private static final String TAG = BlinkerView.class.getSimpleName();
    private static final String KEY_SCALE_TYPE = "blinker_scale_type";
    private static final String KEY_DURATION = "blinker_duration";
    private static final String KEY_FADE = "blinker_fade";
    private static final String KEY_BLINK = "blinker_should_blink";
    private static final String KEY_SUPER_STATE = "blinker_super_state";
    private static final int DEF_INTERVAL = 500; // ms

    // <editor-fold desc="Scale types">
    public static final int SCALE_STRETCH = 0;
    public static final int SCALE_CONSTRAIN = 1;
    public static final int SCALE_CENTER = 2;

    @Retention(SOURCE)
    @IntDef({SCALE_STRETCH, SCALE_CONSTRAIN, SCALE_CENTER})
    public @interface ScaleType {}
    // </editor-fold>

    @Nullable
    private Drawable mDrawable;
    @NonNull
    private Rect mDrawableBounds = new Rect();
    @NonNull
    private ValueAnimator mAnimator = new ValueAnimator();
    @ScaleType
    private int mScaleType = SCALE_STRETCH;
    private int mInterval = DEF_INTERVAL;
    private boolean mShouldBlink = false;
    private boolean mFade = true;

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

    @SuppressWarnings("unused")
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
        mInterval = attributes.getInteger(R.styleable.BlinkerView_blink_interval, mInterval);
        mShouldBlink = attributes.getBoolean(R.styleable.BlinkerView_blink_autostart, false);
        mFade = attributes.getBoolean(R.styleable.BlinkerView_blink_use_fading, mFade);
        attributes.recycle();
        createAnimator();
    }

    /* Android integration */

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mShouldBlink) {
            startBlinking();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopBlinking(false);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable originalState = super.onSaveInstanceState();
        final Bundle state = new Bundle();
        state.putInt(KEY_SCALE_TYPE, mScaleType);
        state.putInt(KEY_DURATION, mInterval);
        state.putBoolean(KEY_FADE, mFade);
        state.putBoolean(KEY_BLINK, mShouldBlink);
        state.putParcelable(KEY_SUPER_STATE, originalState);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(@Nullable final Parcelable outerState) {
        if (outerState instanceof Bundle) {
            final Bundle state = (Bundle) outerState;
            super.onRestoreInstanceState(state.getParcelable(KEY_SUPER_STATE));
            mScaleType = state.getInt(KEY_SCALE_TYPE);
            mInterval = state.getInt(KEY_DURATION);
            mFade = state.getBoolean(KEY_FADE);
            mShouldBlink = state.getBoolean(KEY_BLINK);
        }
        if (mShouldBlink) {
            startBlinking();
        }
    }

    @Override
    public void setPadding(final int left, final int top, final int right, final int bottom) {
        super.setPadding(left, top, right, bottom);
        updateDrawableBounds();
        invalidate();
    }

    @Override
    public void setPaddingRelative(final int start, final int top, final int end, final int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        updateDrawableBounds();
        invalidate();
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
                mDrawable.setAlpha(255);
            } else if (mShouldBlink) {
                int alpha = Math.round(mAnimator.getAnimatedFraction() * 255);
                if (!mFade) {
                    alpha = alpha > 255 / 2 ? 255 : 0;
                }

                mDrawable.setAlpha(alpha);
            } else {
                mDrawable.setAlpha(255);
            }
            mDrawable.draw(canvas);
        }
    }

    /* Public API */

    /**
     * Gets the current drawable's scale type. Always is one of {@link ScaleType}.
     *
     * @return The current scale type
     */
    @ScaleType
    @SuppressWarnings("unused")
    public int getScaleType() {
        return mScaleType;
    }

    /**
     * Sets the drawable's scale type. Must be one of {@link ScaleType}.
     *
     * @param scaleType The new scale type
     */
    @SuppressWarnings("unused")
    public void setScaleType(@ScaleType final int scaleType) {
        mScaleType = scaleType;
        updateDrawableBounds();
        invalidate();
    }

    /**
     * Gets the blinker drawable, if any.
     *
     * @return A drawable that is currently set to blink, or {@code null} if no drawable is set
     */
    @Nullable
    @SuppressWarnings("unused")
    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Changes the blinker drawable.
     *
     * @param drawable Which drawable to blink now, set to {@code null} to make it invisible
     */
    @SuppressWarnings("unused")
    public void setDrawable(@Nullable final Drawable drawable) {
        mDrawable = drawable;
        updateDrawableBounds();
        invalidate();
    }

    /**
     * Starts blinking the source drawable (infinitely).
     */
    @SuppressWarnings("unused")
    public void startBlinking() {
        mShouldBlink = true;
        stopAnimator();
        createAnimator().start();
    }

    /**
     * Stops blinking the source drawable and returns it's alpha to 100%.
     */
    @SuppressWarnings("unused")
    public void stopBlinking() {
        stopBlinking(true);
    }

    /**
     * Checks if the BlinkerView is set to blink. This doesn't mean that it's actually
     * blinking, as blinking is disabled when detached from window.
     *
     * @return {@code True} if blinker is set to blink when attached to window, {@code false} if not
     */
    @SuppressWarnings("unused")
    public boolean isBlinkingOn() {
        return mShouldBlink;
    }

    /* Private helpers */

    private void stopBlinking(boolean setFlag) {
        if (setFlag) {
            mShouldBlink = false;
        }
        stopAnimator();
    }

    private final AnimatorUpdateListener mUpdateListener = (animation) -> invalidate();

    // @formatter:off
    private final AnimatorListener mAnimationListener = new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) { invalidate(); }
        @Override public void onAnimationCancel(Animator animation) { invalidate(); }
    };
    // @formatter:on

    @NonNull
    private ValueAnimator createAnimator() {
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(mInterval);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(mUpdateListener);
        animator.addListener(mAnimationListener);
        mAnimator = animator;
        return animator;
    }

    private void stopAnimator() {
        mAnimator.cancel();
        mAnimator.removeUpdateListener(mUpdateListener);
        mAnimator.removeListener(mAnimationListener);
    }

    @NonNull
    private Rect getPadding() {
        // @formatter:off
        return new Rect(
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? getPaddingStart() : getPaddingLeft(),
            getPaddingTop(),
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? getPaddingEnd() : getPaddingRight(),
            getPaddingBottom()
        );
        // @formatter:on
    }

    private void updateDrawableBounds() {
        if (mDrawable == null) { return; }

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

    // <editor-fold desc="Applying drawable bounds">
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
    // </editor-fold>

}
