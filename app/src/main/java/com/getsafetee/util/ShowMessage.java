package com.getsafetee.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class ShowMessage{
    Context _context;

    public ShowMessage(Context context){
        this._context = context;
    }

    public void message(final String title, final String body, final String btn){
        final AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage(body)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
