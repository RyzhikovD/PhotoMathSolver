package ru.ryzhikov.photomathsolver.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.data.model.Formula;
import ru.ryzhikov.photomathsolver.provider.WebDataProvider;

// хорошо бы разнести все классы/пакеты на domain/data/presentation
public class CropPhotoFragment extends Fragment implements View.OnClickListener {

    private static final int DEFAULT_IMAGE_COMPRESSION = 4;

    private ImageView mImageView;
    private View mProgressRelativeLayout;

    private Uri mImageUri;
    private String mPath;

    private WebDataProvider mWebDataProvider;

    private Bitmap mBitmap;

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
        mProgressRelativeLayout = view.findViewById(R.id.relative_layout_progress);
        view.findViewById(R.id.button_crop).setOnClickListener(this);
        view.findViewById(R.id.button_scan).setOnClickListener(this);
        view.findViewById(R.id.button_scanned_photos).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = DEFAULT_IMAGE_COMPRESSION;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        mBitmap = BitmapFactory.decodeFile(mPath, bmOptions);
        mImageView.setImageBitmap(mBitmap);

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
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_crop:
                CropImage.activity(mImageUri).start(requireActivity(), this);
                break;
            case R.id.button_scan:
                mProgressRelativeLayout.setVisibility(View.VISIBLE);

                loadImage(mBitmap);
                break;
            case R.id.button_scanned_photos:
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root, ImageListFragment.newInstance())
                        .commit();
                break;
        }
    }

    private void scan(Formula formula) {
        String test = "\\int _ { 0 } ^ { a } \\frac { 1 } { 2 x ^ { 2 } + 123 } d x";

        String latexFormula = formula == null ? test : formula.getLatex();
        String wolframFormula = formula == null ? test : formula.getWolfram();

        mProgressRelativeLayout.setVisibility(View.GONE);

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

    // все async task надо положить в презентер или вью модель
    private static class DownloadFormulaTask extends AsyncTask<Bitmap, String, Formula> {

        private final WeakReference<CropPhotoFragment> mFragmentReference;
        private final WebDataProvider mProvider;
        private final String mPath;


        private DownloadFormulaTask(@NonNull CropPhotoFragment fragment) {
            mFragmentReference = new WeakReference<>(fragment);
            mProvider = fragment.mWebDataProvider;
            mPath = fragment.mPath;
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
            fragment.scan(formula);
        }
    }
}
