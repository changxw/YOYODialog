package com.yoyo.yoyodialog.touchProcessor;

import android.view.MotionEvent;

import com.yoyo.yoyodialog.YOYODialog;

public abstract class TouchProcessor {
    public abstract void doProcess(YOYODialog dialog, MotionEvent event);
}
