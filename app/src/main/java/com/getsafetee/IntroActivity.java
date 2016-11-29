package com.getsafetee;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.getsafetee.safetee.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    public SharedPreferences settings;
    public boolean firstRun;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("prefs", 0);
        firstRun = settings.getBoolean("firstRun", true);
        if(firstRun){
            addSlide(new SlideFragmentBuilder()

                            .backgroundColor(R.color.colorPrimary)

                            .buttonsColor(R.color.colorAccent)

                            .possiblePermissions(new String[]{Manifest.permission.CALL_PHONE,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE})

                            .image(R.drawable.cf)

                            .title("Circle of Friends")

                            .description("Specify ")

                            .build(),

                    new MessageButtonBehaviour(new View.OnClickListener() {

                        @Override

                        public void onClick(View v) {

                            Toast.makeText(IntroActivity.this, "We provide solutions to make you love your work", Toast.LENGTH_SHORT).show();

                        }

                    }, "Work with love"));

            addSlide(new SlideFragmentBuilder()

                    .backgroundColor(R.color.colorPrimary)

                    .buttonsColor(R.color.colorAccent)

                    .image(R.drawable.mic)

                    .title("Record the Scene")

                    .description("Provide evidence for law suits")

                    .build());

            addSlide(new SlideFragmentBuilder()

                    .backgroundColor(R.color.colorPrimary)

                    .buttonsColor(R.color.colorAccent)

                    .image(R.drawable.tips)

                    .title("Safety Tips")

                    .description("Recieve timely tips to keep you safe")

                    .build());

            addSlide(new SlideFragmentBuilder()

                    .backgroundColor(R.color.colorPrimary)

                    .buttonsColor(R.color.colorAccent)

                    .image(R.drawable.circle_of_friends)

                    .title("NGOs Around")

                    .description("Locate a women's rights NGO around you")

                    .build());

        }else{ loadMainActivity();}

    }

    private void loadMainActivity() {

        settings = getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstRun", false);
        editor.apply();
        Intent intent = new Intent();
        setResult(2, intent);
        finish();//finishing activity
    }
}
