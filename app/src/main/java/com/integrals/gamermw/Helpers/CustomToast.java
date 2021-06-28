package com.integrals.gamermw.Helpers;

import android.content.Context;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class CustomToast {
    private Context context;

    public CustomToast(Context context) {
        this.context = context;
    }
    public void showErrorToast(String message){
        DynamicToast.makeError(context, message, 2000).show();
    }
    public void showSuccessToast(String message){
        DynamicToast.makeSuccess(context, message, 2000).show();
    }
    public void showWarningToast(String message){
        DynamicToast.makeWarning(context, message, 2000).show();
    }
}
