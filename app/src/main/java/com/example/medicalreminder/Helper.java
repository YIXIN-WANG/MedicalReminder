package com.example.medicalreminder;

import android.content.Context;
import android.content.Intent;

public class Helper {

    public static void goToHomeView(Context context){
        Intent homeIntent = new Intent(context, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(homeIntent);
    }
}
