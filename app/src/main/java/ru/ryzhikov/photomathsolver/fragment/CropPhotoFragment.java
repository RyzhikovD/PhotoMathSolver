package ru.ryzhikov.photomathsolver.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.provider.WebDataProvider;

public class CropPhotoFragment extends Fragment implements View.OnClickListener {

    private static final int PIC_CROP = 0;

    private ImageView mImageView;
    private Uri mImageUri;

    private int targetW;
    private int targetH;
    private String mPath;

    private final WebDataProvider mWebDataProvider = new WebDataProvider();

    public static Fragment newInstance(Uri imageUri, String path) {
        return new CropPhotoFragment(imageUri, path);
    }

    private CropPhotoFragment(Uri imageUri, String path) {
        mImageUri = imageUri;
        mPath = path;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = view.findViewById(R.id.image_photo);
        view.findViewById(R.id.button_crop).setOnClickListener(this);
        view.findViewById(R.id.button_scan).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImageView.setImageURI(mImageUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PIC_CROP) {
            if (data != null) {
                Uri dataUri = data.getData();
                mImageView.setImageURI(null);
                mImageView.setImageURI(dataUri);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_crop:
                performCrop(mImageUri);
                break;
            case R.id.button_scan:
                targetW = mImageView.getWidth();
                targetH = mImageView.getHeight();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                bmOptions.inSampleSize = 4;

                Bitmap bitmap = BitmapFactory.decodeFile(mPath, bmOptions);

                loadImage(bitmap);
                break;
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", targetW);
            cropIntent.putExtra("outputY", targetH);
            cropIntent.putExtra("return-data", true);

            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException ex) {
            String errorMessage = "Your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void scan(Formula formula) {
        String test = "\\int _ { 0 } ^ { a } \\frac { 1 } { 2 x ^ { 2 } + 123 } d x";

        String latexFormula = formula == null ? test : formula.getLatex();
        String wolframFormula = formula == null ? test : formula.getWolfram();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.root, EditFormulaFragment.newInstance(latexFormula, wolframFormula))
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

    private static class DownloadFormulaTask extends AsyncTask<Bitmap, Void, Formula> {

        private final WeakReference<CropPhotoFragment> mFragmentReference;

        private final WebDataProvider mProvider;


        private DownloadFormulaTask(@NonNull CropPhotoFragment fragment) {
            mFragmentReference = new WeakReference<>(fragment);
            mProvider = fragment.mWebDataProvider;
        }

        protected Formula doInBackground(Bitmap... bitmaps) {
            try {
                return mProvider.loadFormula(bitmapToBase64(bitmaps[0]));
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
            fragment.scan(formula);
        }

    }
}
