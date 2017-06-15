package me.jason.trycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static me.jason.trycamera.R.id.bottom;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    SurfaceView view;
    Camera camera;
    ImageButton change;
    ImageButton toggle;
    ImageButton capture;
    File outputPictureFile;
    boolean front = false;
    boolean flash = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        change = (ImageButton) (findViewById(R.id.change));
        toggle = (ImageButton) (findViewById(R.id.flashToggle));
        capture = (ImageButton) (findViewById(R.id.capture));
        File outputPictureFile = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else {
            start();
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    camera.stopPreview();
                    camera.release();
                    if (front) {
                        camera = camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    }
                    else {
                        camera = camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    }
                } catch(Exception e) {Log.d("debugging mainActivity", e.getMessage());}
                try {
                    camera.setDisplayOrientation(90);
                    camera.setPreviewDisplay(holder);
                } catch(Exception e) {Log.d("dubugging mainActivity", e.getMessage());}
                front = !front;
                camera.startPreview();
            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.Parameters p = camera.getParameters();
                if (flash) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    toggle.setImageResource(R.drawable.flash_off);
                }
                else {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    toggle.setImageResource(R.drawable.flash);
                }
                flash = !flash;
                camera.setParameters(p);
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "button clicked!", Toast.LENGTH_SHORT).show();
                captureImage();
            }
        });
    }

    public void refreshCamera () {
        if (holder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {}
        try {
            camera.setPreviewDisplay(holder);
             camera.startPreview();
        } catch (Exception e) {}
    }

    private File getDir() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file,  "Camera_Demo");
    }
    public void captureImage() {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            File directory = getDir();
            // @Override
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    FileOutputStream outStream = new FileOutputStream(
                            directory.getAbsoluteFile().toString() + "/"
                                    + System.currentTimeMillis() + ".jpg");
                    outStream.write(data);
                    outStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    refreshCamera();
                }
                Log.d("debugging mainActivity", "onPictureTaken - jpeg");
            }
        });
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
        try {
            camera.stopPreview();
            camera.release();
        } catch (Exception e) {}
    }
}
