/*
 * Copyright © Appus Studio LLC 2009 - 2015
 */

package com.appus.splash;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.appus.splash.util.ImageUtils;
import com.appus.splash.view.ResizeCallbackImageView;

/**
 * Created by igor.malytsky on 9/10/15.
 */
public class Splash {

    //D - down
    //U - up
    //DR - duration

    private static final float D_FROM = 1.5f;
    private static final float D_TO = 1f;
    private static final float U_FROM = 1f;
    private static final float U_TO = 20.0f;

    private static final int SCALE_D_DR = 1000;
    private static final int SCALE_UP_DR = 500;

    private static final int DR_FACTOR = 1;
    private ResizeCallbackImageView mIvSplash;

    private ViewGroup mSplashContainer;
    private View mSplashImageBackground;

    private Drawable mSplashImage;
    private int mSplashBackgroundColor;
    private Drawable mSplashBackgroundImage;

    private ActionBar mActionBar;

    private int mScreenWidth;
    private int mScreenHeight;

    private boolean useColorInSplashBackground;

    private Context mContext;

    public Splash(Activity activity, ActionBar actionBar) {
        this.mActionBar = actionBar;
        this.mContext = activity.getApplicationContext();

        initScreenSize(activity);
        replaceRootContent(activity);
        initViews();
    }

    /**
     * Initializing variables, which are responsible for screen width and height.
     *
     * @param activity Parent activity before which we should show splash screen
     */
    private void initScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        mScreenWidth = screenSize.x;
        mScreenHeight = screenSize.y;
    }

    /**
     * Removing content from root ViewGroup, copying it to another ViewGroup, which contains splash screen,
     * that higher in the hierarchy.
     *
     * @param activity Parent activity before which we should show splash screen
     */
    private void replaceRootContent(Activity activity) {
        ViewGroup content = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);

        mSplashContainer = (ViewGroup) LayoutInflater.from(content.getContext())
                .inflate(R.layout.splash_layout, null, false);

        ViewGroup root = (ViewGroup) content.getParent();
        root.removeView(content);

        FrameLayout newContainer = new FrameLayout(root.getContext());
        newContainer.addView(content);
        newContainer.addView(mSplashContainer);

        root.addView(newContainer);
    }

    /**
     * Method is responsible for initializing all views, which are used is splash screen
     */
    private void initViews() {
        mIvSplash = (ResizeCallbackImageView) mSplashContainer.findViewById(R.id.iv_splash_image);
        mSplashImageBackground = mSplashContainer.findViewById(R.id.splash_image_background);

        mSplashImageBackground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.default_splash_image_color));
        mSplashBackgroundColor = ContextCompat.getColor(mContext, R.color.default_splash_color);
        mSplashImage = ContextCompat.getDrawable(mContext, R.drawable.default_splash_image);

        mIvSplash.setScaleX(D_FROM);
        mIvSplash.setScaleY(D_FROM);

        processMask();
    }

    /**
     * If activity contains action bar this method toggles it (shows or hides).
     *
     * @param show Boolean which is responsible for toggling. (true - to show)
     */
    private void toggleActionbar(boolean show) {
        if (mActionBar != null) {
            if (show) {
                mActionBar.show();
            } else {
                mActionBar.hide();
            }

        }
    }

    /**
     * This method is responsible for cutting splash image from background to make transparent hole
     */
    private void processMask() {
        BitmapDrawable splashBitmapDrawable = (BitmapDrawable) mSplashImage;

        Bitmap mask = splashBitmapDrawable.getBitmap();
        Bitmap result = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);

        /**
         * Finding left and top coordinates for drawing mask in the center of ImageView
         * */
        float left = mScreenWidth / 2 - mask.getWidth() / 2;
        float top = mScreenHeight / 2 - mask.getHeight() / 2;

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (useColorInSplashBackground || mSplashBackgroundImage == null) {
            canvas.drawColor(mSplashBackgroundColor);
        } else {
            Bitmap tmpSplashBitmap = ((BitmapDrawable) mSplashBackgroundImage).getBitmap();
            Bitmap background = ImageUtils.scaleCenterCrop(tmpSplashBitmap, mScreenHeight, mScreenWidth);
            canvas.drawBitmap(background, 0, 0, new Paint());
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawBitmap(mask, left, top, paint);

        mIvSplash.setImageBitmap(result);

        mIvSplash.setScaleType(ImageView.ScaleType.CENTER);
    }


    /**
     * Starts animations
     */
    private void startAnimations() {
        float midW = mIvSplash.getWidth() >> 1;
        float midH = mIvSplash.getHeight() >> 1;

        final ScaleAnimation scaleUpAnim = new ScaleAnimation(U_FROM, U_TO, U_FROM, U_TO, midW, midH);
        ScaleAnimation scaleDownAnim = new ScaleAnimation(D_FROM, D_TO, D_FROM, D_TO, midW, midH);
        final ScaleAnimation frameScaleDownAnim = new ScaleAnimation(D_FROM, U_TO, D_FROM, U_TO, midW, midH);
        scaleDownAnim.setDuration(SCALE_D_DR * DR_FACTOR);
        scaleUpAnim.setDuration(SCALE_UP_DR * DR_FACTOR);
        frameScaleDownAnim.setDuration(SCALE_UP_DR * DR_FACTOR);
        scaleDownAnim.setAnimationListener(new CAnimatorListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mSplashImageBackground.setVisibility(View.GONE);
                mIvSplash.startAnimation(scaleUpAnim);
            }
        });

        scaleUpAnim.setAnimationListener(new CAnimatorListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                mSplashContainer.setVisibility(View.GONE);
                toggleActionbar(true);
            }
        });

        mIvSplash.setScaleX(D_TO);
        mIvSplash.setScaleY(D_TO);

        mIvSplash.startAnimation(scaleDownAnim);
    }

    private void setSplashImage(Drawable splashImage) {
        this.mSplashImage = splashImage;

        processMask();
    }

    private void setSplashImageColor(int color) {
        mSplashImageBackground.setBackgroundColor(color);
    }

    private void setBackgroundColor(int color) {
        this.mSplashBackgroundColor = color;

        processMask();
    }

    private void setBackgroundImage(Drawable image) {
        this.mSplashBackgroundImage = image;

        processMask();
    }

    private void setUseColorInSplashBackground(boolean useColorInSplashBackground) {
        this.useColorInSplashBackground = useColorInSplashBackground;
    }

    /**
     * Method is responsible for performing splash screen
     */
    public void perform() {
        toggleActionbar(false);
        if (mSplashContainer != null) {
            mIvSplash.setOnSizeChangedListener(new ResizeCallbackImageView.OnSizeChangedListener() {
                @Override
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startAnimations();
                        }
                    }, SCALE_D_DR);
                }
            });
        }

    }

    public static class Builder {
        private Splash mSplashInstance;

        /**
         * Create builder for custom splash.
         * @param activity - Activity context
         * @param actionBar - Used actionbar. Need to properly hide it during splash.
         */
        public Builder(Activity activity, ActionBar actionBar) {
            mSplashInstance = new Splash(activity, actionBar);
        }

        /**
         * Set image, that will be displayed at center of splash.
         * @param splashImage drawable with picture
         * @return Builder instance
         */
        public Builder setSplashImage(Drawable splashImage) {
            mSplashInstance.setSplashImage(splashImage);
            return this;
        }

        /**
         * Set color of center splash image
         * @param color Color that will colorify image
         * @return Builder instance
         */
        public Builder setSplashImageColor(int color) {
            mSplashInstance.setSplashImageColor(color);
            return this;
        }

        /**
         * Set color of background behind splash image
         * @param color splash background color
         * @return Builder instance
         */
        public Builder setBackgroundColor(int color) {
            mSplashInstance.setUseColorInSplashBackground(true);
            mSplashInstance.setBackgroundColor(color);
            return this;
        }

        /**
         *
         * @param image
         * @return Builder instance
         */
        public Builder setBackgroundImage(Drawable image) {
            mSplashInstance.setUseColorInSplashBackground(false);
            mSplashInstance.setBackgroundImage(image);
            return this;
        }

        /**
         * Run splash animation
         */
        public void perform() {
            mSplashInstance.perform();
        }
    }
}