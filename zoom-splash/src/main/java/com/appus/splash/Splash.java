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
import android.view.animation.AlphaAnimation;
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

    private static final float D_FROM = 1.2f;
    private static final float D_TO = 1f;
    private static final float U_FROM = 1f;
    private static final float U_TO = 20.0f;

    private static final int START_OFFSET = 1000;

    private static final int SCALE_D_DR_TYPE_1 = 1000;
    private static final int SCALE_UP_DR_TYPE_1 = 500;
    private static final int ALPHA_DR_TYPE_1 = 500;

    private static final int SCALE_D_DR_TYPE_2 = 300;
    private static final int SCALE_UP_DR_TYPE_2 = 500;
    private static final int ALPHA_DR_TYPE_2 = 200;

    private static final int DR_FACTOR = 1;
    private ResizeCallbackImageView mIvSplash;

    private ViewGroup mSplashContainer;
    private View mSplashImageBackground;

    private Drawable mSplashImage;
    private int mSplashBackgroundColor;
    private Drawable mSplashBackgroundPicture;

    private ActionBar mActionBar;

    private int mScreenWidth;
    private int mScreenHeight;

    private boolean useColorInSplashBackground;

    private Context mContext;

    private static boolean isOneShot = true;
    private static boolean hasBeenPerformed = false;

    private int mPivotXOffset = 0;
    private int mPivotYOffset = 0;

    public enum AnimationType {
        TYPE_1,
        TYPE_2,
    }

    private AnimationType mAnimationType = AnimationType.TYPE_1;

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

        mSplashContainer.setVisibility(View.GONE);
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

        if (useColorInSplashBackground || mSplashBackgroundPicture == null) {
            canvas.drawColor(mSplashBackgroundColor);
        } else {
            Bitmap tmpSplashBitmap = ((BitmapDrawable) mSplashBackgroundPicture).getBitmap();
            Bitmap background = ImageUtils.scaleCenterCrop(tmpSplashBitmap, mScreenHeight, mScreenWidth);
            canvas.drawBitmap(background, 0, 0, new Paint());
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawBitmap(mask, left, top, paint);

        mIvSplash.setImageBitmap(result);

        mIvSplash.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    /**
     * Starts animations
     */
    private void startAnimationType1() {
        float midW = mIvSplash.getWidth() >> 1;
        float midH = mIvSplash.getHeight() >> 1;

        final ScaleAnimation scaleUpAnim = new ScaleAnimation(U_FROM, U_TO, U_FROM, U_TO, midW + mPivotXOffset, midH + mPivotYOffset);
        final ScaleAnimation scaleDownAnim = new ScaleAnimation(D_FROM, D_TO, D_FROM, D_TO, midW, midH);
        final AlphaAnimation alphaSplashImageAnimation = new AlphaAnimation(1f, 0f);

        scaleDownAnim.setDuration(SCALE_D_DR_TYPE_1 * DR_FACTOR);
        scaleUpAnim.setDuration(SCALE_UP_DR_TYPE_1 * DR_FACTOR);
        alphaSplashImageAnimation.setDuration(ALPHA_DR_TYPE_1);

        scaleDownAnim.setAnimationListener(new CAnimatorListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mSplashImageBackground.startAnimation(alphaSplashImageAnimation);
                mIvSplash.startAnimation(scaleUpAnim);
            }
        });

        scaleUpAnim.setAnimationListener(new CAnimatorListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                mSplashContainer.setVisibility(View.GONE);
                toggleActionbar(true);
                isOneShot = true;
            }
        });

        initSplashSizeBeforeScaleAnim();

        mIvSplash.startAnimation(scaleDownAnim);
    }

    private void startAnimationType2() {
        float midW = mIvSplash.getWidth() >> 1;
        float midH = mIvSplash.getHeight() >> 1;

        final ScaleAnimation scaleUpAnim = new ScaleAnimation(U_FROM, U_TO, U_FROM, U_TO, midW + mPivotXOffset, midH + mPivotYOffset);
        final ScaleAnimation scaleDownAnim = new ScaleAnimation(D_FROM, D_TO, D_FROM, D_TO, midW, midH);
        final AlphaAnimation alphaSplashImageAnimation = new AlphaAnimation(1f, 0f);

        scaleDownAnim.setDuration(SCALE_D_DR_TYPE_2 * DR_FACTOR);
        scaleUpAnim.setDuration(SCALE_UP_DR_TYPE_2 * DR_FACTOR);
        alphaSplashImageAnimation.setDuration(ALPHA_DR_TYPE_2 * DR_FACTOR);

        alphaSplashImageAnimation.setAnimationListener(new CAnimatorListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mSplashImageBackground.setVisibility(View.GONE);

                initSplashSizeBeforeScaleAnim();

                mIvSplash.startAnimation(scaleDownAnim);
            }
        });

        scaleDownAnim.setAnimationListener(new CAnimatorListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mIvSplash.startAnimation(scaleUpAnim);
            }
        });

        scaleUpAnim.setAnimationListener(new CAnimatorListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mSplashContainer.setVisibility(View.GONE);
                toggleActionbar(true);
                isOneShot = true;
            }
        });

        mSplashImageBackground.startAnimation(alphaSplashImageAnimation);
    }

    /**
     * Should be used before first scale animation for restoring splash scale to normal. Is used for avoiding flashing.
     * */
    private void initSplashSizeBeforeScaleAnim() {
        mIvSplash.setScaleX(D_TO);
        mIvSplash.setScaleY(D_TO);
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
        this.mSplashBackgroundPicture = image;

        processMask();
    }

    private void setPivotXOffset(int offset) {
        this.mPivotXOffset = offset;
    }

    private void setPivotYOffset(int offset) {
        this.mPivotYOffset = offset;
    }

    private void setUseColorInSplashBackground(boolean useColorInSplashBackground) {
        this.useColorInSplashBackground = useColorInSplashBackground;
    }

    private void setAnimationType(AnimationType type) {
        this.mAnimationType = type;
    }

    /**
     * Method is responsible for performing splash screen
     */
    public void perform() {
        if (hasBeenPerformed) {
            return;
        }

        hasBeenPerformed = isOneShot;

        toggleActionbar(false);
        if (mSplashContainer != null) {
            mSplashContainer.setVisibility(View.VISIBLE);
            mIvSplash.setOnSizeChangedListener(new ResizeCallbackImageView.OnSizeChangedListener() {
                @Override
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startAnimation();
                        }
                    }, START_OFFSET);
                }
            });
        }
    }

    /**
     * Starts specified animation type. If type was not set, then starts default animation (TYPE_1)
     * */
    private void startAnimation() {
        switch (mAnimationType) {
            case TYPE_1:
                startAnimationType1();
                break;
            case TYPE_2:
                startAnimationType2();
                break;
        }
    }

    public static class Builder {
        private Splash mSplashInstance;

        public Builder(Activity activity, ActionBar actionBar) {
            mSplashInstance = new Splash(activity, actionBar);
        }

        public Builder setSplashImage(Drawable splashImage) {
            mSplashInstance.setSplashImage(splashImage);
            return this;
        }

        public Builder setSplashImageColor(int color) {
            mSplashInstance.setSplashImageColor(color);
            return this;
        }

        public Builder setBackgroundColor(int color) {
            mSplashInstance.setUseColorInSplashBackground(true);
            mSplashInstance.setBackgroundColor(color);
            return this;
        }

        public Builder setBackgroundImage(Drawable image) {
            mSplashInstance.setUseColorInSplashBackground(false);
            mSplashInstance.setBackgroundImage(image);
            return this;
        }

        public Builder setPivotXOffset(int offset) {
            mSplashInstance.setPivotXOffset(offset);
            return this;
        }

        public Builder setPivotYOffset(int offset) {
            mSplashInstance.setPivotYOffset(offset);
            return this;
        }

        public Builder setOneShotStart(boolean isOneShot) {
            Splash.isOneShot = isOneShot;
            return this;
        }

        public Builder setAnimationType(AnimationType type) {
            mSplashInstance.setAnimationType(type);
            return this;
        }

        public Splash create() {
            return mSplashInstance;
        }

        public void perform() {
            mSplashInstance.perform();
        }
    }
}