package com.uilib.inputlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Mikiller on 2016/7/13.
 */
public class InputLinearLayout extends LinearLayout{
    private KeyboardStateListener listener;
    private int keyboardHeight = 100;

    public InputLinearLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public InputLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public InputLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public void setKeyboardStateListener(KeyboardStateListener listener){
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isKeyboardShown(this.getRootView())) {
            if(listener != null){
                listener.onKeyboardShown(keyboardHeight);
            }
        } else {
            if(listener != null)
                listener.onKeyboardHiden();
        }
    }

    private boolean isKeyboardShown(View rootView) {
        final int height = 100;
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int hx = rootView.getBottom() - rect.bottom;
        return (keyboardHeight = hx) > height * dm.density;
    }
}
