package com.example.exercisefloatingwidgetapp.Class;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.exercisefloatingwidgetapp.R;

public class FloatWidgetService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingWidget;
    public FloatWidgetService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        final WindowManager.LayoutParams params;
        //Below changed due to issues with targetSDKVersion != 22, was originally just the 'else' lines
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        //Set the starting position of the created bubble
        params.gravity = Gravity.TOP | Gravity.START;
        //Get the width of the phone for the starting X position
        params.x = Resources.getSystem().getDisplayMetrics().widthPixels;
        params.y = 200;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);
        //Get the collapsed and expanded view from out layouts
        final View collapsedView = mFloatingWidget.findViewById(R.id.collapse_layout);
        final View expandedView = mFloatingWidget.findViewById(R.id.expanded_layout);
        //Get the two buttons for closing the views/ floating widget
        ImageView closeButtonCollapsed = mFloatingWidget.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        ImageView closeButton = mFloatingWidget.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });
        mFloatingWidget.findViewById(R.id.container_layout).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //When the widget is touched by a finger (down)
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        Log.d("test", "initialX: " + initialX);
                        Log.d("test", "initialY: " + initialY);
                        Log.d("DEBUG MSG", ": " + collapsedView.getWidth());
                        return true;
                    //When a finger is released from the widget (up)
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        //If the finger release is within a certain bounds, then it will expand the widget
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    //When a finger moves while on the widget, to move it around
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if (isViewCollapsed()) {
                            if (params.x > Resources.getSystem().getDisplayMetrics().widthPixels - collapsedView.getWidth()) {
                                params.x = Resources.getSystem().getDisplayMetrics().widthPixels - collapsedView.getWidth();
                            } else if (params.x < collapsedView.getWidth()) {
                                params.x = collapsedView.getWidth();
                            }
                        }
                        mWindowManager.updateViewLayout(mFloatingWidget, params);
                        return true;
                }
                return false;
            }
        });
    }

    //Boolean function for if the view is collapsed or expanded
    private boolean isViewCollapsed() {
        return mFloatingWidget == null || mFloatingWidget.findViewById(R.id.collapse_layout).getVisibility() == View.VISIBLE;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidget != null) mWindowManager.removeView(mFloatingWidget);
    }
}