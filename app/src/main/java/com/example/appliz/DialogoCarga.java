package com.example.appliz;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class DialogoCarga {

    Activity activity;
    AlertDialog dialog;

    DialogoCarga(Activity myActivity){
        activity = myActivity;

    }

    void starLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_proggresbar,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

    }

    void dismissDialog(){
        dialog.dismiss();
    }

}
