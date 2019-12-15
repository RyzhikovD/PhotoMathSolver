package ru.ryzhikov.photomathsolver.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.ryzhikov.photomathsolver.BuildConfig;
import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.provider.FileProvider;

public class ChooseImageFragment extends Fragment implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri mImageUri;
    private String mCurrentPhotoPath;

    {
        setRetainInstance(true);
    }

    public static ChooseImageFragment newInstance() {
        return new ChooseImageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_choose_from_gallery).setOnClickListener(this);
        view.findViewById(R.id.button_take_a_photo).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, CropPhotoFragment.newInstance())
//                    .replace(R.id.root, CropPhotoFragment.newInstance(mImageUri, mCurrentPhotoPath))
                    .addToBackStack(CropPhotoFragment.class.getSimpleName())
                    .commit();
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            mImageUri = data.getData();
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

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root, CropPhotoFragment.newInstance())
//                    .replace(R.id.root, CropPhotoFragment.newInstance(mImageUri, mCurrentPhotoPath))
                    .addToBackStack(CropPhotoFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        }
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
}
