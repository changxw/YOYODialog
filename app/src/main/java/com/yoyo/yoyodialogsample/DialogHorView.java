package com.yoyo.yoyodialogsample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yoyo.yoyodialog.YOYODialogView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DialogHorView extends YOYODialogView {
    public DialogHorView(@NonNull Context context) {
        this(context, null);
    }

    public DialogHorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogHorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.dialog_hor_content, this);
        setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

}
