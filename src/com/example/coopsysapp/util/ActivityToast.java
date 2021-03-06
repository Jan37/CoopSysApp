package com.example.coopsysapp.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

public class ActivityToast {

    public static final long LENGTH_SHORT = 2000;
    public static final long LENGTH_LONG = 3000;
    public static final int DEFAULT_ANIMATION_DURATION = 400;
    public static final String BUNDLE_IS_SHOWING = "com.ym.ActivityToast.IS_SHOWING";

    private final Activity mActivity;
    private FrameLayout.LayoutParams mLayoutParams;

    private Handler mHandler = new Handler();

    private ViewGroup mParent;
    private FrameLayout mToastHolder;
    private View mToastView;

    private Animation mShowAnimation;
    private Animation mCancelAnimation;

    private long mLength = LENGTH_SHORT;

    private Animation.AnimationListener mShowAnimationListener;
    private Animation.AnimationListener mCancelAnimationListener;

    private boolean mIsAnimationRunning;
    private boolean mIsShown;

    /**
     * @param activity Toast will be shown at top of the widow of this Activity
     */
    public ActivityToast(@NonNull Activity activity, View toastView) {
        mActivity = activity;

        mParent = (ViewGroup) activity.getWindow().getDecorView();
        mToastHolder = new FrameLayout(activity.getBaseContext());
        mLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.FILL_HORIZONTAL
        );
        mToastHolder.setLayoutParams(mLayoutParams);

        mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(DEFAULT_ANIMATION_DURATION);
        mShowAnimation.setAnimationListener(mHiddenShowListener);

        mCancelAnimation = new AlphaAnimation(1.0f, 0.0f);
        mCancelAnimation.setDuration(DEFAULT_ANIMATION_DURATION);
        mCancelAnimation.setAnimationListener(mHiddenCancelListener);

        mToastView = toastView;
        mToastHolder.addView(mToastView);

        mToastHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    cancel();
                }
                return false;
            }
        });
    }

    public void show() {
        if (!isShowing()) {
            mParent.addView(mToastHolder);
            mIsShown = true;

            if (mShowAnimation != null) {
                mToastHolder.startAnimation(mShowAnimation);
            } else {
                mHandler.postDelayed(mCancelTask, mLength);
            }
        }
    }

    public void cancel() {
        if (isShowing() && !mIsAnimationRunning) {
            if (mCancelAnimation != null) {
                mToastHolder.startAnimation(mCancelAnimation);
            } else {
                mParent.removeView(mToastHolder);
                mHandler.removeCallbacks(mCancelTask);
                mIsShown = false;
            }
        }
    }

    public boolean isShowing() {
        return mIsShown;
    }

    /**
     * Pay attention that Action bars is the part of Activity window
     *
     * @param gravity Position of view in Activity window
     */

    public void setGravity(int gravity) {
        mLayoutParams.gravity = gravity;

        if (isShowing()) {
            mToastHolder.requestLayout();
        }
    }

    public void setShowAnimation(Animation showAnimation) {
        mShowAnimation = showAnimation;
    }

    public void setCancelAnimation(Animation cancelAnimation) {
        mCancelAnimation = cancelAnimation;
    }

    /**
     * @param cancelAnimationListener cancel toast animation. Note: you should use this instead of
     *                                Animation.setOnAnimationListener();
     */
    public void setCancelAnimationListener(Animation.AnimationListener cancelAnimationListener) {
        mCancelAnimationListener = cancelAnimationListener;
    }

    /**
     * @param showAnimationListener show toast animation. Note: you should use this instead of
     *                              Animation.setOnAnimationListener();
     */
    public void setShowAnimationListener(Animation.AnimationListener showAnimationListener) {
        mShowAnimationListener = showAnimationListener;
    }

    public void setLength(long length) {
        mLength = length;
    }

    public View getView() {
        return mToastView;
    }

    public void saveInstanceState(@Nullable Bundle bundle) {
        if (bundle != null) {
            bundle.putBoolean(BUNDLE_IS_SHOWING, isShowing());
        }
    }

    public void restoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        boolean isShowing = savedInstanceState.getBoolean(BUNDLE_IS_SHOWING, false);
        if (isShowing) {
            mHandler.removeCallbacks(mCancelTask);
            mParent.addView(mToastHolder);
            mIsShown = true;
            mHandler.postDelayed(mCancelTask, mLength);
        }
    }


    private Runnable mCancelTask = new Runnable() {
        @Override
        public void run() {
            cancel();
        }
    };

    private Animation.AnimationListener mHiddenShowListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if (mShowAnimationListener != null) {
                mShowAnimationListener.onAnimationStart(animation);
            }

            mIsAnimationRunning = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mHandler.postDelayed(mCancelTask, mLength);

            if (mShowAnimationListener != null) {
                mShowAnimationListener.onAnimationEnd(animation);
            }

            mIsAnimationRunning = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            if (mShowAnimationListener != null) {
                mShowAnimationListener.onAnimationRepeat(animation);
            }
        }
    };

    private Animation.AnimationListener mHiddenCancelListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if (mCancelAnimationListener != null) {
                mCancelAnimationListener.onAnimationStart(animation);
            }

            mIsAnimationRunning = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mParent.removeView(mToastHolder);
            mHandler.removeCallbacks(mCancelTask);

            if (mCancelAnimationListener != null) {
                mCancelAnimationListener.onAnimationEnd(animation);
            }

            mIsAnimationRunning = false;
            mIsShown = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            if (mCancelAnimationListener != null) {
                mCancelAnimationListener.onAnimationRepeat(animation);
            }
        }
    };
}