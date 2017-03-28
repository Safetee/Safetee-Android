package com.safeteeapp.adapter;


import com.safeteeapp.app.AppController;
import com.safeteeapp.model.GetRecords;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.safeteeapp.safetee.R;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<GetRecords> recordsItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<GetRecords> recordsItems) {
        this.activity = activity;
        this.recordsItems = recordsItems;
    }

    @Override
    public int getCount() {
        return recordsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return recordsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.records_list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView remark = (TextView) convertView.findViewById(R.id.remark);
        TextView category = (TextView) convertView.findViewById(R.id.category);
        TextView created = (TextView) convertView.findViewById(R.id.created);
        TextView audio = (TextView) convertView.findViewById(R.id.audio);
        TextView rid = (TextView) convertView.findViewById(R.id.rid);

        // getting record data for the row
        GetRecords m = recordsItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // remark
        remark.setText(String.valueOf(m.getRemark()));


        // created
        created.setText(String.valueOf(m.getCreated()));

        // audio
        audio.setText(String.valueOf(m.getAudio()));

        // record id
        rid.setText(String.valueOf(m.getRid()));

        return convertView;
    }

}