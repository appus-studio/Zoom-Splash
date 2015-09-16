/*
 * Copyright © Appus Studio LLC 2009 - 2015
 */

package com.appus.splash.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by igor.malytsky on 9/10/15.
 */
public class ResizeCallbackImageView extends ImageView {

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener mSizeChangedListener) {
        this.mSizeChangedListener = mSizeChangedListener;
    }

    private OnSizeChangedListener mSizeChangedListener;

    public ResizeCallbackImageView(Context context) {
        super(context);
    }

    public ResizeCallbackImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeCallbackImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mSizeChangedListener != null) {
            mSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }
}