package com.yoyo.yoyodialogsample;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.yoyo.yoyodialog.YOYODialogView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogContentView extends YOYODialogView {
    public DialogContentView(@NonNull Context context) {
        this(context, null);
    }

    public DialogContentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogContentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.dialog_content, this);
//        setBackgroundColor(getResources().getColor(R.color.colorAccent));

    }

}
