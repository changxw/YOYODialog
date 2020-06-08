package com.yoyo.yoyodialog.touchProcessor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.yoyo.yoyodialog.YOYODialog;

import androidx.core.view.ViewCompat;

public class LeftTouchProcessor extends TouchProcessor {
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
                    float slope = deltaY * 1.0f / deltaX;

                    long deltaTime = System.currentTimeMillis() - downTime;
                    float slideSpeed = deltaY * 1.0f / deltaTime;
                    if (deltaX >= currRect.width() / 2 || slideSpeed > 1.0f) {
                        smoothDismiss(dialog, -currRect.right, slope);
                    } else {
                        smoothBack(dialog, deltaX, slope);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (hasContentTouched) {
                    float deltaX = event.getX() - lastMotionEvent.getX();
                    boolean isSliding = false;
                    if (deltaX < 0) {
                        ViewCompat.offsetLeftAndRight(contentView, (int) deltaX);
                        isSliding  = true;
                    } else if (contentView.getRight() + deltaX <= originalRect.right) {
                        ViewCompat.offsetLeftAndRight(contentView, (int) deltaX);
                        isSliding = true;
                    }

                    if (isSliding) {
                        int distance = originalRect.width();
                        float scrollDistance = Math.max(originalRect.right - (contentView.getRight() + deltaX), 0);
                        float alpha = (1 - Math.min(1.0f, scrollDistance / distance)) * dialog.getDefaultDimAmount();
                        dialog.getWindow().setDimAmount(alpha);
                    }
                }
                break;
        }

        lastMotionEvent = MotionEvent.obtain(event);
    }


    private void smoothDismiss(final YOYODialog dialog, final int offsetX, final float slope) {
        final View contentView = dialog.getContentView();
        final Rect originalRect = dialog.getContentRect();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, offsetX).setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int lastX = 0;
            private int lastY = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currX = (int) animation.getAnimatedValue();
                int currY = (int) (currX * slope);
                int deltaY = currY - lastY;
                int deltaX = currX - lastX;
                lastX = currX;
                lastY = currY;
                ViewCompat.offsetTopAndBottom(contentView, (int) deltaY);
                ViewCompat.offsetLeftAndRight(contentView, (int) deltaX);

                int distance = originalRect.width();
                float scrollDistance = Math.max(originalRect.right - (contentView.getRight() + deltaX), 0);
                float alpha = (1 - Math.min(1.0f, scrollDistance / distance)) * dialog.getDefaultDimAmount();
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

    private void smoothBack(YOYODialog dialog, int offsetX, final float slope) {
        final View contentView = dialog.getContentView();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, offsetX).setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int lastX = 0;
            private int lastY = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currX = (int) animation.getAnimatedValue();
                int currY = (int) (currX * slope);
                int deltaY = currY - lastY;
                int deltaX = currX - lastX;
                lastX = currX;
                lastY = currY;
                ViewCompat.offsetTopAndBottom(contentView, (int) deltaY);
                ViewCompat.offsetLeftAndRight(contentView, (int) deltaX);
            }
        });
        valueAnimator.start();
    }

}
