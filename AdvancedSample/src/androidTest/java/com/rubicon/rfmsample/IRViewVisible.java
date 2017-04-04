package com.rubicon.rfmsample;


import android.support.test.espresso.IdlingResource;
import android.view.View;


public class IRViewVisible implements IdlingResource {

    private View view;
    private ResourceCallback callback;

    public IRViewVisible(View view) {
        this.view = view;
    }

    @Override
    public String getName() {
        return IRViewVisible.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if(view.getVisibility() == View.VISIBLE && callback != null) {
            callback.onTransitionToIdle();
            System.out.println("IR-Idle");
            return true;
        }
        System.out.println("IR-NotIdle");
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.callback = resourceCallback;

    }
}