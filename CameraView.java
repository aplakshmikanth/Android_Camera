package com.example.lakshmikanthgowda.camera;

/**
 * Created by lakshmikanthgowda on 2017-06-15.
 *
 * Surface functionality to display automatically captured image
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private static Camera mCamera;
    private boolean safeToTakePicture = false;

    public CameraView(Context context, Camera camera){
        super(context);

        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        //get the holder and set this class as the callback, so we can get camera data here
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            //when the surface is created, we can set the camera to draw images in this surfaceholder
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            Log.d("M2-1", "Camera2-1" );
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if(mHolder.getSurface() == null)//check if the surface is ready to receive camera data
        {
            Log.d("M2", "Camera2" );

            return;
        }

        try{
            mCamera.stopPreview();
            Log.d("M3", "Camera3" );

        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
        }

        //now, recreate the camera preview
        try{
            Log.d("M5", "Camera5" );
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

            safeToTakePicture = true;
            if (safeToTakePicture) {
                Log.d("M99", "click pic" );

                mCamera.takePicture(null, null, mPicture);
                safeToTakePicture = false;
            }
            Log.d("M6", "Camera6" );

        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
        mCamera.stopPreview();
        Log.d("M7", "Camera7" );
        mCamera.release();
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("M21", "picture function" );

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d("M22", "pic" );

                return;

            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                Log.d("M23", "pic" );

                fos.close();
                safeToTakePicture = true;

            } catch (FileNotFoundException e) {
                Log.d("M31", "file exception" );


            } catch (IOException e) {
                Log.d("M32", "file open exception" );

            }
        }
    };

    private static File getOutputMediaFile() {
        Log.d("M24", "getOutputMediaFile" );


        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp/ubicomp_pic");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        Log.d("M25", "filename"+mediaFile );

        mCamera.stopPreview();
        Log.d("M77", "Camera7" );
        mCamera.release();

        return mediaFile;
    }
}

