package com.getsafetee.safetee.circle_of_friends;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.getsafetee.safetee.R;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.android.inapp.Pay;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseResponse;


public class DonateDialog extends DialogFragment {
    private String ngo = "";

    private Activity activity;

    private Context context;

    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();
        context = getContext();

        return new AlertDialog.Builder(getActivity())

                .setTitle(R.string.dialog_title)

                .setSingleChoiceItems(R.array.gnos, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Resources res = getResources();
                        ngo = res.getStringArray(R.array.gnos)[which];

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        donateWithISW();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create();


    }

    private void donateWithISW() {

        RequestOptions options = RequestOptions.builder()
                .setClientId("IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218")
                .setClientSecret("XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=")
                .build();

        Pay pay = new Pay(activity, "Safetee", "Donate With Interswitch to " + ngo, "1000", "NGN", options,
                new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        Util.notify(context, "error", error.getMessage(), "Close", false);
                    }

                    @Override
                    public void onSuccess(PurchaseResponse response) {

                        String f = "Your donation of =N= 1000.00 to " + ngo + " was successful.";

                        Util.notify(context, "Success", f, "Close", false);

                    }
                });
        pay.start();
    }


}
