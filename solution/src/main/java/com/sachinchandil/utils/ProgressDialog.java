package com.sachinchandil.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;

/**
 * Created by sachin on 9/11/17.
 */

public class ProgressDialog {

    private AlertDialog dialog;

    public ProgressDialog(Context context){
        dialog = new AlertDialog.Builder(context)
                .setView(new ProgressBar(context, null, android.R.attr.progressBarStyleSmall))
                .create();
    }

    public void show(CharSequence message){
        dialog.setMessage(message);
        dialog.show();
    }

    public void dismiss(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
