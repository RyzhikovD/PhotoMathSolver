package ru.ryzhikov.photomathsolver.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.ryzhikov.photomathsolver.BuildConfig;
import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.provider.FileProvider;
import ru.ryzhikov.photomathsolver.provider.WebDataProvider;

public class CropPhotoFragment extends Fragment implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int DEFAULT_IMAGE_COMPRESSION = 4;

    private Uri mImageUri;
    private String mCurrentPhotoPath;

    private ImageView mImageView;
    private Bitmap mBitmap;

    private WebDataProvider mWebDataProvider;

    private FloatingActionButton mCropButton;
    private ExtendedFloatingActionButton mScanButton;
    private FloatingActionButton mGalleryButton;
    private FloatingActionButton mScannedPicturesButton;
    private FloatingActionButton mCameraButton;
    private ExtendedFloatingActionButton mSelectImageButton;

    {
        setRetainInstance(true);
    }

    public static Fragment newInstance() {
        return new CropPhotoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        (mImageView = view.findViewById(R.id.image_photo)).setOnClickListener(this);

        (mCropButton = view.findViewById(R.id.button_crop)).setOnClickListener(this);
        (mScanButton = view.findViewById(R.id.button_scan)).setOnClickListener(this);
        (mSelectImageButton = view.findViewById(R.id.button_select_image)).setOnClickListener(this);
        (mScannedPicturesButton = view.findViewById(R.id.button_scanned_photos)).setOnClickListener(this);
        (mGalleryButton = view.findViewById(R.id.button_choose_from_gallery)).setOnClickListener(this);
        (mCameraButton = view.findViewById(R.id.button_take_a_photo)).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebDataProvider = new WebDataProvider(requireContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                if (resultCode == Activity.RESULT_OK) {
                    mImageUri = result.getUri();

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;

                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = DEFAULT_IMAGE_COMPRESSION;
                    bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

                    mBitmap = BitmapFactory.decodeFile(mImageUri.getPath(), bmOptions);

                    mImageView.setImageBitmap(mBitmap);
                    showImage();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = DEFAULT_IMAGE_COMPRESSION;
            bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

            mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            mImageView.setImageBitmap(mBitmap);
            showImage();
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mImageUri = data.getData();
            }
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (mImageUri != null) {
                Cursor cursor = requireActivity().getContentResolver().query(mImageUri,
                        filePathColumn, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mCurrentPhotoPath = cursor.getString(columnIndex);
                    cursor.close();
                }
            }

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = DEFAULT_IMAGE_COMPRESSION;
            bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

            mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            mImageView.setImageBitmap(mBitmap);
            showImage();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_crop:
                CropImage.activity(mImageUri).start(requireActivity(), this);
                break;
            case R.id.button_scan:
                v.getRootView().findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                v.getRootView().findViewById(R.id.progress_text).setVisibility(View.VISIBLE);
                loadImage(mBitmap);
                break;
            case R.id.button_select_image:
                final Animation galleryButtonAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.animation_for_gallery_button);
                final Animation scannedImagesButtonAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.animation_for_scanned_images_button);
                final Animation cameraButtonAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.animation_for_camera_button);

                galleryButtonAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        ((ExtendedFloatingActionButton) v).hide();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        v.getRootView().findViewById(R.id.text_choose_from_gallery).animate().alpha(1f).setDuration(400).start();
                        v.getRootView().findViewById(R.id.text_scanned_photos).animate().alpha(1f).setDuration(400).start();
                        v.getRootView().findViewById(R.id.text_take_a_photo).animate().alpha(1f).setDuration(400).start();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mGalleryButton.startAnimation(galleryButtonAnimation);
                mScannedPicturesButton.startAnimation(scannedImagesButtonAnimation);
                mCameraButton.startAnimation(cameraButtonAnimation);
                break;
            case R.id.image_photo:
                if(mGalleryButton.isEnabled()) {
                    mGalleryButton.setEnabled(false);
                    mScannedPicturesButton.setEnabled(false);
                    mCameraButton.setEnabled(false);

                    mGalleryButton.hide();
                    mScannedPicturesButton.hide();
                    mCameraButton.hide();
                } else {
                    mGalleryButton.setEnabled(true);
                    mScannedPicturesButton.setEnabled(true);
                    mCameraButton.setEnabled(true);

                    mGalleryButton.show();
                    mScannedPicturesButton.show();
                    mCameraButton.show();
                }
                break;
            case R.id.button_choose_from_gallery:
                dispatchChooseFromGalleryIntent();
                break;
            case R.id.button_take_a_photo:
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_scanned_photos:
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root, ImageListFragment.newInstance())
                        .addToBackStack(ImageListFragment.class.getSimpleName())
                        .commit();
                break;
        }
    }

    private void showImage() {
        mImageView.setVisibility(View.VISIBLE);
        mScanButton.setVisibility(View.VISIBLE);
        mCropButton.setVisibility(View.VISIBLE);

        mImageView.getRootView().findViewById(R.id.text_choose_from_gallery).setVisibility(View.GONE);
        mImageView.getRootView().findViewById(R.id.text_scanned_photos).setVisibility(View.GONE);
        mImageView.getRootView().findViewById(R.id.text_take_a_photo).setVisibility(View.GONE);

        mGalleryButton.show();
        mScannedPicturesButton.show();
        mCameraButton.show();

        mGalleryButton.hide();
        mScannedPicturesButton.hide();
        mCameraButton.hide();

        mGalleryButton.setEnabled(false);
        mScannedPicturesButton.setEnabled(false);
        mCameraButton.setEnabled(false);
    }

    private void dispatchChooseFromGalleryIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                mImageUri = FileProvider.getUriForFile(requireContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void scan(Formula formula) {
        String test = "\\int _ { 0 } ^ { a } \\frac { 1 } { 2 x ^ { 2 } + 123 } d x";

        String latexFormula = formula == null ? test : formula.getLatex();
        String wolframFormula = formula == null ? test : formula.getWolfram();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.root, EditFormulaFragment.newInstance(latexFormula, wolframFormula))
                .addToBackStack(EditFormulaFragment.class.getSimpleName())
                .commit();
    }

    private void loadImage(Bitmap bitmap) {
        DownloadFormulaTask downloadFormulaTask = new DownloadFormulaTask(this);
        downloadFormulaTask.execute(bitmap);
    }

    private static String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }

    private static class DownloadFormulaTask extends AsyncTask<Bitmap, String, Formula> {

        private final WeakReference<CropPhotoFragment> mFragmentReference;
        private final WebDataProvider mProvider;
        private final String mPath;


        private DownloadFormulaTask(@NonNull CropPhotoFragment fragment) {
            mFragmentReference = new WeakReference<>(fragment);
            mProvider = fragment.mWebDataProvider;
            mPath = fragment.mCurrentPhotoPath;
        }

        protected Formula doInBackground(Bitmap... bitmaps) {
            try {
                return mProvider.loadFormula(mPath, bitmapToBase64(bitmaps[0]));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Formula formula) {
            CropPhotoFragment fragment = mFragmentReference.get();
            if (fragment == null) {
                return;
            }

            fragment.requireView().findViewById(R.id.progress_bar).setVisibility(View.GONE);
            fragment.requireView().findViewById(R.id.progress_text).setVisibility(View.GONE);

            fragment.scan(formula);
        }
    }
}
