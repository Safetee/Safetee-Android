package com.getsafetee.safetytips;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.getsafetee.safetee.R;


public class TipView extends AppCompatActivity {

    private Intent getIntent;
    private TextView title;
    private TextView body;
    private TextView date;

    private String title_f;
    private  String body_f;
    private String date_f;

    private int HTML_FL = 3433;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tip_view);

        getIntent = getIntent();
        setTitle(getIntent.getStringExtra("sender"));

        title_f = getIntent.getStringExtra("title");
        body_f = getIntent.getStringExtra("body");
        date_f = getIntent.getStringExtra("date");

        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);
        date = (TextView) findViewById(R.id.date);

        title.setText(title_f);
        body.setText(Html.fromHtml(body_f));
        date.setText(date_f);
    }
}
