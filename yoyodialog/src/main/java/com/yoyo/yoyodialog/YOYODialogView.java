package com.yoyo.yoyodialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class YOYODialogView extends CustomFrameLayout implements OnDismissListener {
    private List<OnDismissListener> onDismissListeners = new ArrayList<>();
    private YOYODialog yoyoDialog;

    public YOYODialogView(@NonNull Context context) {
        super(context);
    }

    public YOYODialogView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YOYODialogView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addOnDismissListener(OnDismissListener listener){
        if (listener != null && onDismissListeners.contains(listener))
            return;

        onDismissListeners.add(listener);
    }

    void setYoyoDialog(YOYODialog dialog) {
        this.yoyoDialog = dialog;
    }

    public void dismiss(){
        yoyoDialog.dismiss();
    }

    public void dismissDelay(int delay){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                yoyoDialog.dismiss();
            }
        }, delay);
    }

    public void dismiss(OnDismissListener listener){
        addOnDismissListener(listener);
        yoyoDialog.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        for(OnDismissListener listener : onDismissListeners)
            listener.onDismiss(dialog);
    }
}

