package com.example.medicalreminder.utils;

import android.content.Context;
import android.content.Intent;

import com.example.medicalreminder.HomeActivity;
import com.example.medicalreminder.LoginActivity;

public class Helper {

    public static void goToHomeView(Context context){
        Intent homeIntent = new Intent(context, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(homeIntent);
    }

    public static void goToLoginView(Context context){
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginIntent);
    }
}
