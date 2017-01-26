package com.safeteeapp.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.safeteeapp.circleoffriends.FriendsList;
import com.safeteeapp.cof2.CofLocal;
import com.safeteeapp.safetee.R;


public class ShowMessage{
    Activity _context;

    public ShowMessage(Activity context){
        this._context = context;
    }

    public void message(final String title, final String body, final String btn){
        LayoutInflater lf = LayoutInflater.from(_context);
        View messageprompt = lf.inflate(R.layout.message_main, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setView(messageprompt);
        final TextView msgbody = (TextView) messageprompt.findViewById(R.id.body);
        ImageView icon = (ImageView) messageprompt.findViewById(R.id.icon);
        if(title.equals("Success")){
            icon.setImageResource(R.drawable.ok);
        }
        String rbtn = btn;
        if(btn.equals("Dismiss")){
            rbtn = "Ok";
        }
        msgbody.setText(body);
                builder.setPositiveButton(rbtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (btn.equals("Click Here to Add Friends")) {
                            _context.startActivity(new Intent(_context, CofLocal.class));
                        }
                        if (btn.equals("Click Here to Get Started")) {
                            _context.startActivity(new Intent(_context, CofLocal.class));
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
