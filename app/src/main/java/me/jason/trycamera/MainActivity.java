package me.jason.trycamera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    SurfaceView view;
    Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void start() {
        view = (SurfaceView) findViewById(R.id.surfaceView);
        holder = view.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceCreated(holder);
    }

    public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            start();
        }
        else {
            Toast toast = Toast.makeText(MainActivity.this, "Cannot access  the camera", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = camera.open();
        } catch(Exception e) {Log.d("DEBUGGING BRO", e.getMessage());}
        Camera.Parameters p = camera.getParameters();
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(p);
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (Exception e) {Log.d("DEBUGGING BRO", e.getMessage());}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
