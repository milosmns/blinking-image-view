package me.angrybyte.blinkerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * The core class of the Blinker view's implementation. This view allows the user to set a drawable to it,
 * and would blink the drawable (show/hide) periodically using multiple configuration options.
 */
public class BlinkerView extends View {

    private static final int DEF_DURATION = 500; // ms

    @Nullable
    private Drawable mDrawable;
    private int mDuration = DEF_DURATION;
    private boolean mAutostart;
    private boolean mFade = true;
    private Rect mBounds = new Rect();

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
        mDuration = attributes.getInteger(R.styleable.BlinkerView_blink_duration, mDuration);
        mAutostart = attributes.getBoolean(R.styleable.BlinkerView_blink_autostart, mAutostart);
        mFade = attributes.getBoolean(R.styleable.BlinkerView_blink_use_fading, mFade);

        // TODO ADD PADDING

        attributes.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
