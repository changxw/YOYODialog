package com.yoyo.yoyodialogsample;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.yoyo.yoyodialog.YOYODialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bottomDialog).setOnClickListener(this);
        findViewById(R.id.centerDialog).setOnClickListener(this);
        findViewById(R.id.topDialog).setOnClickListener(this);
        findViewById(R.id.leftDialog).setOnClickListener(this);
        findViewById(R.id.rightDialog).setOnClickListener(this);
        findViewById(R.id.fullscreenDialog).setOnClickListener(this);
        findViewById(R.id.outTouchableDialog).setOnClickListener(this);
        findViewById(R.id.slidableDialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottomDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogContentView(this))
                        .setCancelable(true)
                        .setNavigationBarColor(getColor(R.color.colorAccent))
                        .setDialogStyle(YOYODialog.DialogStyle.BOTTOM)
                        .setOutsideTouchable(false)
                        .setSlidingDismiss(true)
                        .build().show();
                break;
            case R.id.centerDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogContentView(this))
                        .setCancelable(true)
                        .setDialogStyle(YOYODialog.DialogStyle.CENTER)
                        .setOutsideTouchable(false)
                        .setSlidingDismiss(false)
                        .build().show();
                break;
            case R.id.topDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogContentView(this))
                        .setCancelable(true)
                        .setDialogStyle(YOYODialog.DialogStyle.TOP)
                        .setOutsideTouchable(false)
                        .setSlidingDismiss(true)
                        .build().show();
                break;
            case R.id.leftDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogHorView(this))
                        .setCancelable(true)
                        .setDialogStyle(YOYODialog.DialogStyle.LEFT)
                        .setOutsideTouchable(false)
                        .setSlidingDismiss(true)
                        .build().show();
                break;
            case R.id.rightDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogHorView(this))
                        .setCancelable(true)
                        .setDialogStyle(YOYODialog.DialogStyle.RIGHT)
                        .setOutsideTouchable(false)
                        .setSlidingDismiss(true)
                        .build().show();
                break;
            case R.id.fullscreenDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogFullScreenView(this))
                        .setCancelable(true)
                        .setDialogStyle(YOYODialog.DialogStyle.FULLSCREEN)
                        .setOutsideTouchable(false)
                        .setSlidingDismiss(false)
                        .build().show();
                break;
            case R.id.outTouchableDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogContentView(this))
                        .setCancelable(true)
                        .setDialogStyle(YOYODialog.DialogStyle.BOTTOM)
                        .setOutsideTouchable(true)
                        .setSlidingDismiss(true)
                        .build().show();
                break;
            case R.id.slidableDialog:
                new YOYODialog.Builder(this)
                        .setContentView(new DialogContentView(this))
                        .setCancelable(true)
                        .setDialogStyle(YOYODialog.DialogStyle.CENTER)
                        .setOutsideTouchable(false)
                        .setSlidingDismiss(true)
                        .build().show();
                break;
        }
    }
}
