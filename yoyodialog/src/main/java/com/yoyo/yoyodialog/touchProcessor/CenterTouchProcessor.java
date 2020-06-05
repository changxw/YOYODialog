package com.yoyo.yoyodialog.touchProcessor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.yoyo.yoyodialog.YOYODialog;

import androidx.core.view.ViewCompat;

public class CenterTouchProcessor extends TouchProcessor {
    private MotionEvent lastMotionEvent;
    private boolean hasContentTouched = false;
    private long downTime;

    @Override
    public void doProcess(YOYODialog dialog, MotionEvent event) {
        Rect originalRect = dialog.getContentRect();
        View contentView = dialog.getContentView();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hasContentTouched = originalRect.contains((int) event.getX(), (int) event.getY());
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                if (hasContentTouched) {
                    Rect currRect = new Rect();
                    contentView.getHitRect(currRect);
                    int deltaX = originalRect.left - currRect.left;
                    int deltaY = originalRect.top - currRect.top;
                    float slope = deltaX * 1.0f / deltaY;

                    long deltaTime = System.currentTimeMillis() - downTime;
                    float slideSpeed = -deltaY * 1.0f / deltaTime;
                    if (deltaY < -currRect.height() / 2 || slideSpeed > 1.0f) {
                        smoothDismiss(dialog, dialog.getWindow().getDecorView().getHeight() - currRect.top + 50, slope);
                    } else {
                        smoothBack(dialog, deltaY, slope);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (hasContentTouched) {
                    float deltaX = event.getX() - lastMotionEvent.getX();
                    float deltaY = event.getY() - lastMotionEvent.getY();
                    ViewCompat.offsetTopAndBottom(contentView, (int) deltaY);
                    ViewCompat.offsetLeftAndRight(contentView, (int) deltaX);

                    int distance = originalRect.height();
                    float scrollDistance = Math.max(contentView.getTop() + deltaY - originalRect.top, 0);
                    float alpha = (1-Math.min(1.0f, scrollDistance/distance))*dialog.getDefaultDimAmount();
                    dialog.getWindow().setDimAmount(alpha);
                }
                break;
        }

        lastMotionEvent = MotionEvent.obtain(event);
    }


    private void smoothDismiss(final YOYODialog dialog, final float offsetY, final float slope) {
        final View contentView = dialog.getContentView();
        final Rect originalRect = dialog.getContentRect();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, offsetY).setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float lastX = 0;
            private float lastY = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currY = (float) animation.getAnimatedValue();
                float currX = currY * slope;
                float deltaY = currY - lastY;
                float deltaX = currX - lastX;
                lastX = currX;
                lastY = currY;
                ViewCompat.offsetTopAndBottom(contentView, (int) deltaY);
                ViewCompat.offsetLeftAndRight(contentView, (int) deltaX);

                int distance = originalRect.height();
                float scrollDistance = Math.max(contentView.getTop() + deltaY - originalRect.top, 0);
                float alpha = (1-Math.min(1.0f, scrollDistance/distance))*dialog.getDefaultDimAmount();
                dialog.getWindow().setDimAmount(alpha);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dialog.dismiss();
            }
        });
        valueAnimator.start();
    }

    private void smoothBack(YOYODialog dialog, float offsetY, final float slope) {
        final View contentView = dialog.getContentView();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, offsetY).setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float lastX = 0;
            private float lastY = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currY = (float) animation.getAnimatedValue();
                float currX = currY * slope;
                float deltaY = currY - lastY;
                float deltaX = currX - lastX;
                lastX = currX;
                lastY = currY;
                ViewCompat.offsetTopAndBottom(contentView, (int) deltaY);
                ViewCompat.offsetLeftAndRight(contentView, (int) deltaX);
            }
        });
        valueAnimator.start();
    }

}
