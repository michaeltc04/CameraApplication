package com.example.mike.pictureapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

/**
 * Created by Mike Carr on 7/9/15.
 *
 * The main thing that one can forget to do is in AndroidManifest.xml
 * to add the permissions in order to use the camera application and/or
 * save to the phone's hard drive.
 *
 * Just remember the permissions are needed:
 * <uses-feature android:name="android.hardware.camera"
 *               android:required="true" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */
public class MainActivity extends Activity {
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runCameraProgram();
    }

    private void runCameraProgram() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
    }

    //This is where most the magic happens, after returning from taking the picture
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Check that request code matches ours:
        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
        {
            //Get our saved file into a bitmap object:
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
            Bitmap bm = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1920, 1080); //These values specify the desired width and height

            if (bm != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //
                //
                //THIS IS YOUR IMAGE DATA
                //
                //
                //ByteArray seems to have better data than the String that we could send and decode on the other side
                byte[] b = baos.toByteArray();
                String s = baos.toString();
                //
                //
                // From the API: Base64-encode the given data and return a newly allocated String with the result
                //               Passing DEFAULT results in output that adheres to RFC 2045.
                // This may be easier to send and decode
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                Log.d("Endcoded Image", encodedImage);
                //
                //
                byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);
                //
                //
                // We always go into true here (after some of my testing).
                // So sending this small, encoded, string for image data seems to be the way to go.
                // I still need to implement something to show the image and see if I have about the
                // same thing on both ends
                if (Arrays.equals(b, decodedImage)) {
                    Log.d("Byte Array Comparison", "  TRUE");
                } else {
                    Log.d("Byte Array Comparison", "  FALSE");
                }
            }
        }
        runCameraProgram();
    }

    /**
     * This does some decoding of a sample bitmap.  I am wondering if we even need to go in here.
     * Need to do some more research later.
     *
     * @param path The filepath where the bitmap is saved
     * @param reqWidth The desired width
     * @param reqHeight The desired height
     * @return A bitmap with the desired width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
}
