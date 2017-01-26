package com.safeteeapp.cof2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.safeteeapp.safetee.R;
import com.safeteeapp.util.CircleTransform;
import com.safeteeapp.util.ContactUtil;


public class CofView extends AppCompatActivity {

    private Intent getIntent;
    private TextView title;
    private TextView body;
    private TextView date;

    private String title_f;
    private  String body_f;
    private String date_f;

    private int HTML_FL = 3433;
    private ContactUtil mContactUtil;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cof_view);

        getIntent = getIntent();
        mContactUtil = new ContactUtil(getApplicationContext());
        setTitle(getIntent.getStringExtra("title"));

        ImageView dp = (ImageView) findViewById(R.id.dp);

        title_f = getIntent.getStringExtra("title");
        body_f = getIntent.getStringExtra("body");
        date_f = getIntent.getStringExtra("date");

        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);
        date = (TextView) findViewById(R.id.date);

        title.setText(title_f);
        body.setText(Html.fromHtml(body_f));
        date.setText(date_f);

        Uri u = mContactUtil.getPhotoUri(mContactUtil.contactIdByPhoneNumber(getApplicationContext(), body_f));
        if (u != null) {
            Glide.with(getApplicationContext()).load(u)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(CofView.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(dp);

        }else {
            Glide.with(getApplicationContext()).load(R.drawable.user2)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(CofView.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(dp);
        }
    }
}
