package com.safeteeapp.auth;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.safeteeapp.safetee.R;

public class SettingsAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] itemabout;
    private final Integer[] imgid;

    public SettingsAdapter(Activity context, String[] itemname, String[] itemabout, Integer[] imgid) {
        super(context, R.layout.activity_main_list, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.itemabout=itemabout;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.settings_main_list, null,true);

        //LinearLayout line = (LinearLayout) rowView.findViewById(R.id.listline);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);


        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        extratxt.setText(itemabout[position]);
        return rowView;

    };
}