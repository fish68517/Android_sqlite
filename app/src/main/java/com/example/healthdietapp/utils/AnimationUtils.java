package com.example.healthdietapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.healthdietapp.R;

/**
 * Utility class for handling animations throughout the application
 * Provides methods for button feedback, page transitions, and list item animations
 */
public class AnimationUtils {

    /**
     * Apply button click animation (scale down)
     */
    public static void applyButtonClickAnimation(View button) {
        if (button == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(button.getContext(), R.anim.button_click);
        button.startAnimation(animation);
    }

    /**
     * Apply button release animation (scale back to normal)
     */
    public static void applyButtonReleaseAnimation(View button) {
        if (button == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(button.getContext(), R.anim.button_release);
        button.startAnimation(animation);
    }

    /**
     * Apply fade in animation to a view
     */
    public static void applyFadeInAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        view.startAnimation(animation);
    }

    /**
     * Apply fade out animation to a view
     */
    public static void applyFadeOutAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
        view.startAnimation(animation);
    }

    /**
     * Apply list item enter animation
     */
    public static void applyListItemEnterAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.list_item_enter);
        view.startAnimation(animation);
    }

    /**
     * Apply list item enter animation with delay
     */
    public static void applyListItemEnterAnimationWithDelay(View view, int delayMillis) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.list_item_enter);
        animation.setStartTime(Animation.START_ON_FIRST_FRAME);
        view.postDelayed(() -> view.startAnimation(animation), delayMillis);
    }

    /**
     * Apply slide in from right animation
     */
    public static void applySlideInRightAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_right);
        view.startAnimation(animation);
    }

    /**
     * Apply slide in from left animation
     */
    public static void applySlideInLeftAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_left);
        view.startAnimation(animation);
    }

    /**
     * Apply slide out to left animation
     */
    public static void applySlideOutLeftAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_out_left);
        view.startAnimation(animation);
    }

    /**
     * Apply slide out to right animation
     */
    public static void applySlideOutRightAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_out_right);
        view.startAnimation(animation);
    }

    /**
     * Apply scale in animation
     */
    public static void applyScaleInAnimation(View view) {
        if (view == null) return;
        
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_in);
        view.startAnimation(animation);
    }

    /**
     * Apply ripple effect animation using ObjectAnimator
     * Creates a visual feedback for button clicks
     */
    public static void applyRippleEffect(View view) {
        if (view == null) return;
        
        // Create a scale animation using ObjectAnimator
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f);
        
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        
        scaleX.start();
        scaleY.start();
    }

    /**
     * Apply page transition animation with fade effect
     */
    public static void applyPageTransitionAnimation(View enterView, View exitView) {
        if (enterView != null) {
            applyFadeInAnimation(enterView);
        }
        if (exitView != null) {
            applyFadeOutAnimation(exitView);
        }
    }

    /**
     * Apply activity transition animations
     */
    public static void applyActivityTransition(Context context, int enterAnimResId, int exitAnimResId) {
        if (context instanceof android.app.Activity) {
            android.app.Activity activity = (android.app.Activity) context;
            activity.overridePendingTransition(enterAnimResId, exitAnimResId);
        }
    }

    /**
     * Apply slide in animation to activity
     */
    public static void applySlideInActivityTransition(Context context) {
        applyActivityTransition(context, R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Apply slide out animation to activity
     */
    public static void applySlideOutActivityTransition(Context context) {
        applyActivityTransition(context, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Apply fade animation to activity
     */
    public static void applyFadeActivityTransition(Context context) {
        applyActivityTransition(context, R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Apply button press animation with callback
     */
    public static void applyButtonPressAnimation(View button, Runnable onAnimationEnd) {
        if (button == null) return;
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f, 1f);
        
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        
        scaleX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }
        });
        
        scaleX.start();
        scaleY.start();
    }

    /**
     * Apply staggered list animation to multiple views
     */
    public static void applyStaggeredListAnimation(View[] views, int delayBetweenItems) {
        for (int i = 0; i < views.length; i++) {
            applyListItemEnterAnimationWithDelay(views[i], i * delayBetweenItems);
        }
    }
}
