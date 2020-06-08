package com.yoyo.yoyodialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yoyo.yoyodialog.touchProcessor.BottomTouchProcessor;
import com.yoyo.yoyodialog.touchProcessor.CenterTouchProcessor;
import com.yoyo.yoyodialog.touchProcessor.DefaultTouchProcessor;
import com.yoyo.yoyodialog.touchProcessor.LeftTouchProcessor;
import com.yoyo.yoyodialog.touchProcessor.RightTouchProcessor;
import com.yoyo.yoyodialog.touchProcessor.TopTouchProcessor;
import com.yoyo.yoyodialog.touchProcessor.TouchCancelProcessor;
import com.yoyo.yoyodialog.touchProcessor.TouchProcessor;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class YOYODialog extends Dialog {
    public enum DialogStyle {
        BOTTOM(Gravity.BOTTOM, R.style.BottomDialog, new BottomTouchProcessor()),
        CENTER(Gravity.CENTER, R.style.CenterDialog, new CenterTouchProcessor()),
        TOP(Gravity.TOP, R.style.TopDialog, new TopTouchProcessor()),
        LEFT(Gravity.START, R.style.LeftDialog, new LeftTouchProcessor()),
        RIGHT(Gravity.END, R.style.RightDialog, new RightTouchProcessor()),
        FULLSCREEN(Gravity.CENTER, R.style.CenterDialog, new CenterTouchProcessor()),
        SPECIFIC(-1, R.style.CenterDialog, new DefaultTouchProcessor());

        private int gravity;
        private int animStyle;
        private TouchProcessor touchProcessor;

        DialogStyle(int gravity, int animStyle, TouchProcessor touchProcessor) {
            this.gravity = gravity;
            this.animStyle = animStyle;
            this.touchProcessor = touchProcessor;
        }

        public DialogStyle setPosition(int x, int y) {
            this.gravity = (x << 16) | y;
            return this;
        }
    }

    public static class Builder {
        private Context context;
        private boolean cancelable = true;
        private DialogStyle dialogStyle = DialogStyle.BOTTOM;
        private boolean outsideTouchable = false;
        private int windowAnimations;
        private int layoutResId;
        private YOYODialogView contentView;
        private OnDismissListener onDismissListener;
        private boolean slidingDismiss = false;
        private int navigationBarColor = Color.TRANSPARENT;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setNavigationBarColor(int navigationBarColor) {
            this.navigationBarColor = navigationBarColor;
            return this;
        }

        public Builder setSlidingDismiss(boolean slidingDismiss) {
            this.slidingDismiss = slidingDismiss;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public Builder setContentView(YOYODialogView view) {
            contentView = view;
            return this;
        }

        public Builder setContentView(@LayoutRes int layoutResId) {
            this.layoutResId = layoutResId;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setDialogStyle(DialogStyle dialogStyle) {
            this.dialogStyle = dialogStyle;
            return this;
        }

        public Builder setOutsideTouchable(boolean outsideTouchable) {
            this.outsideTouchable = outsideTouchable;
            return this;
        }

        public Builder setWindowAnimations(int windowAnimations) {
            this.windowAnimations = windowAnimations;
            return this;
        }

        public YOYODialog build() {
            YOYODialog dialog = new YOYODialog(context, this);
            return dialog;
        }

    }

    private Builder builder;
    private List<OnDismissListener> onDismissListeners = new ArrayList<>();
    private List<TouchProcessor> touchProcessors = new ArrayList<>();
    private final float defaultDimAmount = 0.0f;
    private View navigationBarView;

    private YOYODialog(@NonNull Context context, Builder builder) {
        super(context);
        this.builder = builder;
        initView();
    }

    public View getContentView() {
        return contentView;
    }

    public Rect getContentRect() {
        return contentRect;
    }

    private View contentView;
    private Rect contentRect = new Rect();

    public void initView() {
        //set content
        if (builder.layoutResId > 0) {
            contentView = View.inflate(builder.context, builder.layoutResId, null);
            getWindow().setContentView(contentView, createLayoutParams());
        } else {
            getWindow().setContentView(builder.contentView, createLayoutParams());
            addDismissListener(builder.contentView);
            builder.contentView.setYoyoDialog(this);
            contentView = builder.contentView;
        }
        contentView.post(new Runnable() {
            @Override
            public void run() {
                contentView.getHitRect(contentRect);
            }
        });

        setOnDismissListener(onDismissListener);
        addDismissListener(builder.onDismissListener);
        //set position
        if (builder.dialogStyle != DialogStyle.SPECIFIC) {
            getWindow().setGravity(builder.dialogStyle.gravity);
        } else {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.x = (builder.dialogStyle.gravity & 0xFF00) >> 16;
            layoutParams.y = builder.dialogStyle.gravity & 0x00FF;
            getWindow().setAttributes(layoutParams);
        }

        getWindow().setWindowAnimations(builder.windowAnimations > 0 ? builder.windowAnimations : builder.dialogStyle.animStyle);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(builder.outsideTouchable);
        if (builder.outsideTouchable) {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            int width = (builder.dialogStyle == DialogStyle.TOP || builder.dialogStyle == DialogStyle.BOTTOM || builder.dialogStyle == DialogStyle.CENTER || builder.dialogStyle == DialogStyle.FULLSCREEN) ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = (builder.dialogStyle == DialogStyle.LEFT || builder.dialogStyle == DialogStyle.RIGHT || builder.dialogStyle == DialogStyle.FULLSCREEN || (builder.dialogStyle == DialogStyle.CENTER && builder.slidingDismiss)) ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            getWindow().setLayout(width, height);
        }

        getWindow().setDimAmount(defaultDimAmount);
        if (builder.outsideTouchable) {
            getWindow().setDimAmount(0);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        }

        if (builder.cancelable) {
            touchProcessors.add(new TouchCancelProcessor());
        }
        if (builder.slidingDismiss) {
            touchProcessors.add(builder.dialogStyle.touchProcessor);
        }

        applyWindowInsets(contentView);
        Log.e("sf", "applyWindowInsets=");
    }

    public float getDefaultDimAmount(){
        return builder.outsideTouchable ? 0 : defaultDimAmount;
    }

    private FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams layoutParams;
        if (builder.dialogStyle == DialogStyle.FULLSCREEN) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.TOP;
        } else {
            int width = (builder.dialogStyle == DialogStyle.TOP || builder.dialogStyle == DialogStyle.BOTTOM || builder.dialogStyle == DialogStyle.CENTER) ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = (builder.dialogStyle == DialogStyle.LEFT || builder.dialogStyle == DialogStyle.RIGHT) ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams = new FrameLayout.LayoutParams(width, height);
            layoutParams.gravity = builder.dialogStyle != DialogStyle.SPECIFIC ? builder.dialogStyle.gravity : Gravity.TOP;
        }
        return layoutParams;
    }

    private void applyWindowInsets(final View contentView) {
        final Activity activity = (Activity) builder.context;
        ViewUtils.doOnApplyWindowInsets(activity.getWindow().getDecorView(), new ViewUtils.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, final WindowInsetsCompat insets, ViewUtils.RelativePadding initialPadding) {
                switch (builder.dialogStyle) {
                    case LEFT:
                    case RIGHT:
                    case BOTTOM:
                        initialPadding.bottom += insets.getSystemWindowInsetBottom();
                        initialPadding.applyToView(contentView);
                        showNavigationBar(insets.getSystemWindowInsetBottom());
                        break;
                    case CENTER:
                        contentView.post(new Runnable() {
                            @Override
                            public void run() {
                                ViewCompat.offsetTopAndBottom(contentView, -insets.getSystemWindowInsetBottom());
                            }
                        });
                        break;
                    case FULLSCREEN: {
                        initialPadding.bottom += insets.getSystemWindowInsetBottom();
                        initialPadding.applyToView(contentView);
                        showNavigationBar(insets.getSystemWindowInsetBottom());
                        break;
                    }case TOP:
                        break;
                    case SPECIFIC:
                        break;
                    default:
                        break;

                }
                return insets;
            }
        });
    }

    private void showNavigationBar(int height){
        if (height <= 0) return;

        navigationBarView = new View(getContext());
        navigationBarView.setBackgroundColor(builder.navigationBarColor);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        layoutParams.gravity = Gravity.BOTTOM;
        getWindow().addContentView(navigationBarView, layoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (builder.outsideTouchable) {
            if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
                return true;
            }
        }

        for (TouchProcessor processor : touchProcessors)
            processor.doProcess(this, event);

        return false;
    }

    private void addDismissListener(OnDismissListener listener) {
        if (listener != null && !onDismissListeners.contains(listener))
            onDismissListeners.add(listener);
    }

    private OnDismissListener onDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            for (OnDismissListener onDismissListener : onDismissListeners) {
                onDismissListener.onDismiss(dialog);
            }
        }
    };
}
