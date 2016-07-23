package team16.filtershare;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shinjaemin on 2016. 7. 6..
 */
public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    public static int cameraId=0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    //This app doesn't use VIDEO but I left it just in case.
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 0;

    public static final int GALLERY_INTENT = 0;
    private static  final int FOCUS_AREA_SIZE= 300;

    private static int isAf =0;
    private static boolean hasAutoFocus;
    private static AutofocusRect mAutofocusRect = null;
    private static int front_camera = -1;
    private static int back_camera = -1;
    private static int current_camera = -1;
    private static TransparentRect mUpperRect = null;
    private static TransparentRect mLowerRect = null;




    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkEveryPermissionAndStart();

    }

    private void checkEveryPermissionAndStart() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("External storage");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                /*
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        */
                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        //Don't start applicaton without permissoins
        mainActivityOnCreate();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    // All Permissions Granted
                    mainActivityOnCreate();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_INTENT) {


                // Get the filepath and display on imageview.
                String filepath = getGalleryImagePath(data);
                // Check if the specified image exists.
                if (!new File(filepath).exists()) {
                    Toast.makeText(this, "Image does not exist.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {

                        Bitmap realImage = BitmapFactory.decodeFile(filepath);
                        Log.d("filepath: ", filepath);
                        //Log.d("absolute", pictureFile.getAbsolutePath());
                        //Log.d("tostring", pictureFile.toString());

                        int width = realImage.getWidth();
                        int height = realImage.getHeight();
                        Log.d("wh", "w: "+width+" h: "+  height);
                        Bitmap resizedImage;
                        if(height>=width)
                             resizedImage= Bitmap.createBitmap(realImage, 0,height/2-width/2,width, width);
                        else
                            resizedImage = Bitmap.createBitmap(realImage, width/2-height/2, 0, width/2+height/2, height);
                        Log.d("wh after", "w: "+resizedImage.getWidth()+ " h: "+ resizedImage.getHeight());
                        //Bitmap resizedImage = Bitmap.createScaledBitmap(realImage, 200, 200, true);

                        File pictureFile = getTmpOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("PictureCallback", "Error creating media file");
                            return;
                        }

                        FileOutputStream fos = new FileOutputStream(pictureFile, false);
                        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();

                        GlobalVariables mApp = ((GlobalVariables)getApplicationContext());
                        mApp.set_picture_path(pictureFile.getAbsolutePath());


                    } catch (FileNotFoundException e) {
                        Log.d("PictureCallback", "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.d("PictureCallback", "Error accessing file: " + e.getMessage());
                    }



                    Intent intent = new Intent(MainActivity.this, PhotoConfirmActivity.class);
                    startActivity(intent);

                    //finish();


                }
            }
        }
    }

    public String getGalleryImagePath(Intent data) {
        Uri imgUri = data.getData();
        String filePath = "";
        if (data.getType() == null) {
            // For getting images from default gallery app.
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imgUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if (data.getType().equals("image/jpeg") || data.getType().equals("image/png")) {
            // For getting images from dropbox or any other gallery apps.
            filePath = imgUri.getPath();
        }
        return filePath;
    }



    /**
     * Check if this device has a camera
     */
    private static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }
    @Override
    protected void onStop(){
        super.onStop();
        releaseCamera();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        releaseCamera();
        /*
        Intent intent = new Intent(MainActivity.this, PhotoConfirmActivity.class);
        startActivity(intent);
        */


    }


    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.d("Resume", "Camera resumes");

        if(checkCameraHardware(this)) {
            Log.d("Ok", "It has camera");
            if(mCamera==null) {
                mCamera = getCameraInstance();
                if (mCamera == null) {
                    Log.e("Fail", "no Camera Instance");
                    return;
                }
                mPreview = new CameraPreview(this, mCamera);
                FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                preview.addView(mPreview);
            }



        }
        else
            Log.e("NO", "checkCameraHardware failed");

        // Create our Preview view and set it as the content of our activity.


    }



    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            Log.d("CameraNum", "Num: "+ Camera.getNumberOfCameras());
            //cameraId = 0;
            Log.d("CameraId", "cameraId: "+ cameraId);

            c = Camera.open(cameraId); // attempt to get a Camera instance

        } catch (Exception e) {
            Log.e("NoCamera", "Camera " +cameraId+ " is no available");
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    private void releaseCamera(){
        if (mCamera != null){
            Log.d("Camera Release", "Camera is released");
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.removeView(mPreview);
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;


        }
    }


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private static File getTmpOutputMediaFile(int type){
        File mediaStorageFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaStorageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "latest_picture.jpg");
        }
        else{
            mediaStorageFile=null;
        }
        return mediaStorageFile;
    }


    public static Bitmap rotate(Bitmap bitmap, int degree, boolean front) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        if(front){
            Log.d("mirror", "true");

            mtx.preScale(-1.0f, 1.0f);
        }

        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static Bitmap rotate_image(String pathname, Bitmap realImage) throws IOException {
        ExifInterface exif=new ExifInterface(pathname);
        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));



        // refer http://sylvana.net/jpegcrop/exif_orientation.html to understand to code below
        if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
            realImage= rotate(realImage, 90, false);
        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
            realImage= rotate(realImage, 270, false);
        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
            realImage= rotate(realImage, 180, false);
        } /*else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
            realImage= rotate(realImage, 90);
        } */
        return realImage;

    }



    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }

    private static void assignCameraId() {

        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                front_camera = i;
            }
            else if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                back_camera = i;
            }
        }
        if(cameraId==-1) {
            if (back_camera != -1) {
                cameraId = back_camera;
            } else if (front_camera != -1) {
                cameraId = front_camera;
            }
        }
    }

    private void setViews(){
        if (hasAutoFocus) {
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
        }
        // Create our Preview view and set it as the content of our activity.
        Log.d("preview", "start camera preview");
        mPreview = new CameraPreview(this, mCamera);
        Log.d("preview", "did camera preview");
        final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


        if (hasAutoFocus) {
            mAutofocusRect = (AutofocusRect) findViewById(R.id.af_rect);
            mAutofocusRect.setParentInfo(mPreview.getWidth(), mPreview.getHeight());

            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mCamera != null) {
                        isAf = 1;
                        Camera camera = mCamera;
                        camera.cancelAutoFocus();
                        Rect focusRect = calculateFocusArea(event.getX(), event.getY());
                        Log.d("Rect", focusRect.toString());
                        Log.d("Rect_ctr", "x: " + focusRect.centerX() + "y:" + focusRect.centerY());
                        Log.d("event_ctr", "x: " + event.getX() + "y:" + event.getY());

                        mAutofocusRect.setLocation(event.getX(), event.getY());
                        mAutofocusRect.showStart();


                        Camera.Parameters parameters = camera.getParameters();
                        if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        }
                        if (parameters.getMaxNumFocusAreas() > 0) {
                            List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                            mylist.add(new Camera.Area(focusRect, 1000));
                            parameters.setFocusAreas(mylist);
                        }

                        try {
                            camera.cancelAutoFocus();
                            camera.setParameters(parameters);
                            camera.startPreview();

                            camera.autoFocus(new Camera.AutoFocusCallback() {
                                @Override
                                public void onAutoFocus(boolean success, Camera camera) {
                                    Log.d("AutoFocus", "success: " + success);
                                    if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                                        Camera.Parameters parameters = camera.getParameters();
                                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                        if (parameters.getMaxNumFocusAreas() > 0) {
                                            parameters.setFocusAreas(null);
                                        }
                                        camera.setParameters(parameters);
                                        mAutofocusRect.clear();

                                        isAf = 0;
                                        camera.startPreview();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });
        }

        else{
            preview.setOnTouchListener(new View.OnTouchListener(){
                   @Override
                   public boolean onTouch(View v, MotionEvent event) {
                       return true;
                   }

            });

        }

        if(hasAutoFocus) {
            // Add a listener to the Capture button
            ImageButton captureButton = (ImageButton) findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            //mCamera.takePicture(null, null, mPicture);

                            Log.d("isAf", "" + isAf);
                            if (isAf == 1) {
                                isAf = 0;
                                mAutofocusRect.clear();
                                mCamera.takePicture(null, null, mPicture);

                                return;
                            }

                            mAutofocusRect.setLocation(mPreview.getWidth() / 2, mPreview.getHeight() / 2);
                            mAutofocusRect.showStart();
                            mCamera.cancelAutoFocus();
                            mCamera.startPreview();

                            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                                public void onAutoFocus(boolean success, Camera camera) {
                                    Log.d("AutoFocus", "success: " + success);
                                    mAutofocusRect.clear();
                                    mCamera.takePicture(null, null, mPicture);

                                }

                            });


                        }
                    }
            );
        }
        else {
            ImageButton captureButton = (ImageButton) findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCamera.takePicture(null, null, mPicture);

                        }
                    }
            );
        }


        ImageButton galleryButton = (ImageButton) findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY_INTENT);

                    }
                }
        );
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getTmpOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d("PictureCallback", "Error creating media file");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile, false);
                //fos.write(data);
                //fos.close();

                fos = new FileOutputStream(pictureFile, false);

                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                //Log.d("absolute", pictureFile.getAbsolutePath());
                //Log.d("tostring", pictureFile.toString());
                if(cameraId==back_camera) {
                    Log.d("save", "back");
                    realImage = rotate(realImage, 90, false);
                }
                else {
                    Log.d("save", "front");
                    realImage = rotate(realImage, 90, true);
                }
                Uri.fromFile(pictureFile).getPath();
                Log.d("Uri", Uri.fromFile(pictureFile).getPath());
                realImage=rotate_image(Uri.fromFile(pictureFile).getPath(),realImage);

                int width = realImage.getWidth();
                int height = realImage.getHeight();
                Log.d("wh", "w: "+width+" h: "+  height);
                Bitmap resizedImage = Bitmap.createBitmap(realImage, 0,height/2-width/2,width, width);
                Log.d("wh after", "w: "+resizedImage.getWidth()+ " h: "+ resizedImage.getHeight());
                //Bitmap resizedImage = Bitmap.createScaledBitmap(realImage, 200, 200, true);


                resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                GlobalVariables mApp = ((GlobalVariables)getApplicationContext());
                mApp.set_picture_path(pictureFile.getAbsolutePath());



            } catch (FileNotFoundException e) {
                Log.d("PictureCallback", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PictureCallback", "Error accessing file: " + e.getMessage());
            }



            Intent intent = new Intent(MainActivity.this, PhotoConfirmActivity.class);
            startActivity(intent);

            //finish();

        }
    };




    private void mainActivityOnCreate(){
        // Create an instance of Camera
        if(checkCameraHardware(this)) {
            Log.d("Ok", "It has camera");
            assignCameraId();

            mCamera = getCameraInstance();
            List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
            hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);




            if (mCamera==null)
                Log.e("Fail", "no Camera Instance");
        }
        else
            Log.e("NO", "checkCameraHardware failed");

        Display display = getWindowManager().getDefaultDisplay();
        final Point screen_size = new Point();
        display.getSize(screen_size);

        final ImageButton changeButton = (ImageButton) findViewById(R.id.change_camera);

        changeButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                changeButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                changeButton.getHeight(); //height is ready
                mUpperRect = (TransparentRect) findViewById(R.id.upper_rect);
                mUpperRect.setScreenSize(screen_size.x, screen_size.y -changeButton.getHeight());
                Log.d("icon height", "icon height: "+ changeButton.getHeight());



                mUpperRect.setUpper();

                mLowerRect = (TransparentRect) findViewById(R.id.lower_rect);
                mLowerRect.setScreenSize(screen_size.x, screen_size.y -changeButton.getHeight());
                mLowerRect.setLower();
            }
        });




        //set camera to continually auto-focus
        setViews();


        changeButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        mCamera.release();
                        if(cameraId == front_camera && back_camera!=-1){
                            cameraId = back_camera;
                        }
                        else if(cameraId == back_camera && front_camera!=-1){
                            cameraId = front_camera;
                        }
                        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                        preview.removeView(mPreview);


                        mCamera=getCameraInstance();
                        List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
                        hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
                        Log.d("change_autofocus", "change_auto?: "+hasAutoFocus);
                        setViews();


                        // Create our Preview view and set it as the content of our activity.
                        //mPreview = new CameraPreview(MainActivity.this, mCamera);
                        //preview.addView(mPreview);



                    }
                }
        );


        ImageButton mCapture = (ImageButton) findViewById(R.id.button_capture);

        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        boolean didTutorial = sharedPref.getBoolean("didTutorial", false);



        if(!didTutorial) {
            ShowcaseView mShowcaseView1 = new ShowcaseView.Builder(this)

                    .setTarget(new ViewTarget(mCapture))
                    .setContentTitle("Select a picture by taking a photo")
                    .setContentText("Take your own photo to apply FilterShare filters. ")
                    //.setStyle(R.style.CustomShowcaseTheme2)
                    .blockAllTouches()
                    .replaceEndButton(R.layout.scv_button)

                    .build();

            final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.setMargins(0, screen_size.y * 3 / 10, 0, 0);

            mShowcaseView1.setButtonPosition(layoutParams);
            //mShowcaseView1.forceTextPosition(ShowcaseView.LEFT_OF_SHOWCASE);

            mShowcaseView1.setOnShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    ImageButton mButton_gallery = (ImageButton) findViewById(R.id.button_gallery);

                    ShowcaseView mShowcaseView2 = new ShowcaseView.Builder(MainActivity.this)

                            .setTarget(new ViewTarget(mButton_gallery))
                            .setContentTitle("Select a picture by picking up from gallery")
                            .setContentText("Pick up the existing picture from gallery to apply FilterShare filters.")
                            //.setStyle(R.style.CustomShowcaseTheme2)
                            .blockAllTouches()

                            .replaceEndButton(R.layout.scv_button)

                            .build();
                    mShowcaseView2.setButtonPosition(layoutParams);

                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("didTutorial", true);
                    editor.commit();


                }

                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {


                }

                @Override
                public void onShowcaseViewShow(ShowcaseView showcaseView) {

                }

                @Override
                public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                }

                ;


                OnShowcaseEventListener NONE = new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                };

            });
        }
    }
}