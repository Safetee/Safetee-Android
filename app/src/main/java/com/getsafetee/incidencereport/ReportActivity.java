package com.getsafetee.incidencereport;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getsafetee.MainActivity;
import com.getsafetee.RecordsActivity;
import com.getsafetee.SessionManager;
import com.getsafetee.safetee.R;
import com.getsafetee.util.Constants;
import com.getsafetee.util.GPSTracker;
import com.getsafetee.util.LocationManager;
import com.getsafetee.util.RealPathUtil;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.loader.LoadJNI;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonUpload;
    private Button buttonChoose, buttonCreate;
    private EditText editText;
    private ImageView imageView;
    private VideoView videoView;
    private int PICK_CLIP_REQUEST = 1;
    private static final int SELECT_VIDEO = 3;
    private Bitmap bitmap;
    private MediaStore.Video video;
    private String clip = "";
    String clipname;
    String clippath;
    String realPath;
    String about;
    private String selectedPath = "";
    // Location Manager
    LocationManager location;
    // Session Manager
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_report);

        session = new SessionManager(ReportActivity.this);
        location = new LocationManager(ReportActivity.this);

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonCreate = (Button) findViewById(R.id.buttonCreate);

        editText = (EditText) findViewById(R.id.about);
        imageView = (ImageView) findViewById(R.id.imageView);
        videoView = (VideoView) findViewById(R.id.videoView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);

    }

    private void CreateClip() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        startActivityForResult(cameraIntent, PICK_CLIP_REQUEST);
    }

    private void ChooseClip() {
        Intent intent = new Intent();
        intent.setType("video/*");
        //String[] mimetypes = {"image/*", "video/*", "audio/*"};
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Clip"), SELECT_VIDEO);
    }


    protected void onActivityResultkkk(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CLIP_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11) {
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                // SDK >= 11 && SDK < 19
            } else if (Build.VERSION.SDK_INT < 19) {
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                // SDK > 19 (Android 4.4)
            } else {
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
             }
            clip = data.getData().toString();
            clipname  = realPath.substring(realPath.lastIndexOf("/")+1);
            clippath  = realPath.substring(realPath.lastIndexOf("/") - 1);

            //try {
            //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //imageView.setImageBitmap(bitmap);
            //compressVideo();
                buttonChoose.setText(clipname);
                //videoView.setVideoPath(clip);
                //videoView.canPause();
                //videoView.canSeekBackward();
                //videoView.canSeekForward();
                //videoView.start();
            //} catch (IOException e) {
                //e.printStackTrace();
            //}
        }
    }

    public String compressVideo(){
        GeneralUtils.checkForPermissionsMAndAbove(ReportActivity.this, true);
        LoadJNI vk = new LoadJNI();
        try {
            Toast.makeText(ReportActivity.this, "Processing clip" , Toast.LENGTH_LONG).show();
            String workFolder = getApplicationContext().getFilesDir().getAbsolutePath();
            String[] complexCommand = {"ffmpeg","-y" ,"-i", realPath,"-strict","experimental","-s", "160x120","-r","25", "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", realPath};
            vk.run(complexCommand , workFolder , getApplicationContext());
            Log.i("test", "ffmpeg4android finished successfully");
            Toast.makeText(ReportActivity.this, "ffmpeg4android finished successfully" , Toast.LENGTH_LONG).show();
        } catch (Throwable e) {
            Log.e("test", "vk run exception.", e);
            Toast.makeText(ReportActivity.this, "ffmpeg4android error: "+e , Toast.LENGTH_LONG).show();
        }
         return realPath;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                buttonChoose.setText(selectedPath);
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    public void sendReport(File report) {
        //clip = compressVideo();
        String about = editText.getText().toString().trim();
        showMessage("Safetee", "Sending report", "Ok");
        Log.i("Report sending", "Started");
        try {
            String sendreport =
                    new MultipartUploadRequest(getApplicationContext(), Constants.UPLOAD_SERVICE_URL)
                            .addFileToUpload(report.getAbsolutePath(), "clip")
                            .addParameter("about", about)
                            .addParameter("sender", session.getUName())
                            .addParameter("uid", session.getUid())
                            .addParameter("location", String.valueOf(location.getLat()) + "," + String.valueOf(location.getLong()))
                            .setNotificationConfig(new UploadNotificationConfig().setRingToneEnabled(false))
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonChoose){
            ChooseClip();
        }
        if(v == buttonCreate){
            CreateClip();
        }
        if(v == buttonUpload){
            if (selectedPath.isEmpty()){
            Toast.makeText(ReportActivity.this, "Please select clip", Toast.LENGTH_LONG).show();
            }else {
            File reportfile = new File(selectedPath);
            sendReport(reportfile);
            }
        }
    }
    public void showMessage(String title, String msg, String btn){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(ReportActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}