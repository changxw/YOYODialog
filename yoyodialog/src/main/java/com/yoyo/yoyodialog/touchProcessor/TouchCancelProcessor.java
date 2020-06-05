package com.yoyo.yoyodialog.touchProcessor;

import android.view.MotionEvent;

import com.yoyo.yoyodialog.YOYODialog;

public class TouchCancelProcessor extends TouchProcessor {
    private MotionEvent lastMotionEvent;
    private boolean hasContentTouched = false;
    private float moveDistance = 0;

    @Override
    public void doProcess(YOYODialog dialog, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hasContentTouched = dialog.getContentRect().contains((int) event.getX(), (int) event.getY());
                lastMotionEvent = MotionEvent.obtain(event);
                moveDistance = 0;
                break;
            case MotionEvent.ACTION_UP:
                if (moveDistance < 10) {
                    if (!hasContentTouched) {
                        dialog.dismiss();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                moveDistance += Math.abs(event.getX() - lastMotionEvent.getX()) + Math.abs(event.getY() - lastMotionEvent.getY());
                break;
        }
        lastMotionEvent = MotionEvent.obtain(event);
    }
}
