package com.example.medicalreminder.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.medicalreminder.HomeActivity;
import com.example.medicalreminder.LoginActivity;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

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

    public static long getCurrentTimeStamp(){
        return Calendar.getInstance().getTimeInMillis();
    }

    public static boolean checkValidSession(Context context, long loginTime){
        long diff = Math.abs(getCurrentTimeStamp() - loginTime);
        int hours = (int)diff/1000/3600;
        return hours <= 2;
    }
}
