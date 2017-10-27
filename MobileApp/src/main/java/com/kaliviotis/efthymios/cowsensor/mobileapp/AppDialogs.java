package com.kaliviotis.efthymios.cowsensor.mobileapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * Created by Efthymios on 10/26/2017.
 */

public class AppDialogs {
    private static EditText input;

    public static void DisplayMessage(Context context, String title, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton(R.string.ok_text, null);
/*        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });*/
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public static void DisplayGetInput(Context context, int title, int message, int inputType, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);

        input = new EditText(context);
        input.setInputType(inputType);
        //input.setInputType(InputType.TYPE_CLASS_NUMBER);
        dlgAlert.setView(input);

        if (okListener != null)
            dlgAlert.setPositiveButton(R.string.ok_text, okListener);
        if (cancelListener != null)
            dlgAlert.setNegativeButton(R.string.cancel_text, cancelListener);
/*        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });*/
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public static String GetValue() {
        return input.getText().toString();
    }

}
