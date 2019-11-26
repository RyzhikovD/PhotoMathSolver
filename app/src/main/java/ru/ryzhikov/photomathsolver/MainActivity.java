package ru.ryzhikov.photomathsolver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.ryzhikov.photomathsolver.fragment.CropPhotoFragment;
import ru.ryzhikov.photomathsolver.provider.FileProvider;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_TO_USE_EXTERNAL_STORAGE = 0;

    private static final int CAMERA_REQUEST = 1;
//    private static final int PIC_CROP = 2;

    private Uri mImageUri;

//    private ImageView mImageView;
//    private EditText mFormula;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mImageView = findViewById(R.id.image_photo);

//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);

//        saveFullImage();

//        findViewById(R.id.button_crop).setOnClickListener(this);
//        findViewById(R.id.button_solve).setOnClickListener(this);

        if (savedInstanceState == null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                System.out.println("NOT GRANTED");

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_TO_USE_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                System.out.println("GRANTED");
                // Permission has already been granted
                try {
                    System.out.println("_____________________HELLO_____________________");
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_USE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        System.out.println("_____________________HELLO_____________________");
                        dispatchTakePictureIntent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("requestCode = " + requestCode);
        System.out.println("resultCode = " + resultCode);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем картинку
//            Bitmap thumbnailBitmap = data.getParcelableExtra("data");
//            mImageView.setImageBitmap(thumbnailBitmap);
//            mImageView.setImageURI(mImageUri);

//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root, CropPhotoFragment.newInstance(mImageUri, mCurrentPhotoPath))
                    .commit();
        }
//        else if (requestCode == PIC_CROP) {
//            if (data != null) {
////                // get the returned data
////                Bundle extras = data.getExtras();
////                // get the cropped bitmap
////                Bitmap selectedBitmap = extras.getParcelable("data");
//
////                mImageView.setImageBitmap(selectedBitmap);
//
//                Uri dataUri = data.getData();
//                Bundle extras = data.getExtras();
//                System.out.println("extras = " + extras);
//                System.out.println("dataUri = " + dataUri);
//                mImageView.setImageURI(null);
//                mImageView.setImageURI(dataUri);
//            } else {
//                System.out.println("data is null");
//            }
//        }
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Uri photoURI = Uri.fromFile(createImageFile());
                mImageUri = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    private void performCrop(Uri picUri) {
//        try {
//            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//            System.out.println(mImageUri.getPath());
//            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//
//            // indicate image type and Uri
//            cropIntent.setDataAndType(picUri, "image/*");
//            // set crop properties here
//            cropIntent.putExtra("crop", true);
//            // indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 50);
//            cropIntent.putExtra("aspectY", 50);
//            // indicate output X and Y
//            cropIntent.putExtra("outputX", 60);
//            cropIntent.putExtra("outputY", 60);
//            // retrieve data on return
//            cropIntent.putExtra("return-data", true);
//            // start the activity - we handle returning in onActivityResult
//
//
//            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
//            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
////            setResult(RESULT_OK, cropIntent);
//            startActivityForResult(cropIntent, PIC_CROP);
//        }
//        // respond to users whose devices do not support the crop action
//        catch (ActivityNotFoundException anfe) {
//            // display an error message
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
//        }
//    }

//    @Override
//    public void onClick(View v) {
//        System.out.println("_________________CLICK__________________");
//        switch (v.getId()) {
//            case R.id.button_crop:
//                performCrop(mImageUri);
//                break;
//            case R.id.button_solve:
//                solve(mImageUri);
//                break;
//        }
//    }

//    private void solve(Uri imageUri) {
//
//    }
}
