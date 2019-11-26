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
import ru.ryzhikov.photomathsolver.provider.LearningProgramProvider;

public class CropPhotoFragment extends Fragment implements View.OnClickListener {

    private static final int PIC_CROP = 0;

    private ImageView mImageView;
    private Uri mImageUri;

    private final LearningProgramProvider mLearningProgramProvider = new LearningProgramProvider();
    private int targetW;
    private int targetH;
    private String mPath;

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
                System.out.println("dataUri = " + dataUri);
                mImageView.setImageURI(null);
                mImageView.setImageURI(dataUri);
            } else {
                System.out.println("data is null");
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
                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), mImageUri);

//                    String fname=new File(mImageUri.getEncodedPath()).getAbsolutePath();
//                    Bitmap bitmap = BitmapFactory.decodeFile(fname);

//                    InputStream imageStream = requireActivity().getContentResolver().openInputStream(mImageUri);
//                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                    // Get the dimensions of the View
                    targetW = mImageView.getWidth();
                    targetH = mImageView.getHeight();

                    // Get the dimensions of the bitmap
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;

                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
//                    bmOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(mPath, bmOptions);

                    loadImage(bitmapToBase64(bitmap));
//                } catch (IOException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            System.out.println(mImageUri.getPath());
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", targetW);
            cropIntent.putExtra("outputY", targetH);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult


            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

//            setResult(RESULT_OK, cropIntent);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException ex) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void scan() {
        String test = "\\int _ { 0 } ^ { a } \\frac { 1 } { 2 x ^ { 2 } + 123 } d x";

        Formula formula = mLearningProgramProvider.provideFormula();

        if (formula != null) {
            System.out.println("receivedFormula = " + formula.getLatex());
        } else {
            System.out.println("receivedFormula = null");
        }
        String formulaString = formula == null ? test : formula.getLatex();
        String wolfram = formula == null ? test : formula.getWolfram();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.root, EditFormulaFragment.newInstance(formulaString, wolfram))
                .commit();
    }

    private void loadImage(String formula) {
        DownloadFormulaTask downloadFormulaTask = new DownloadFormulaTask(this);
        downloadFormulaTask.execute(formula);
    }

    private static String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }

    private static class DownloadFormulaTask extends AsyncTask<String, Void, Formula> {

        private final WeakReference<CropPhotoFragment> mFragmentReference;

        private final LearningProgramProvider mProvider;


        private DownloadFormulaTask(@NonNull CropPhotoFragment fragment) {
            mFragmentReference = new WeakReference<>(fragment);
            mProvider = fragment.mLearningProgramProvider;
        }

        protected Formula doInBackground(String... formulas) {
            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mFragmentReference.get().requireActivity().getContentResolver(), mFragmentReference.get().mImageUri);

//                String fname=new File(mFragmentReference.get().mImageUri.getEncodedPath()).getAbsolutePath();
//                Bitmap bitmap = BitmapFactory.decodeFile(fname);

//                InputStream imageStream = mFragmentReference.get().requireActivity().getContentResolver().openInputStream(mFragmentReference.get().mImageUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                int targetW = mFragmentReference.get().targetW;
                int targetH = mFragmentReference.get().targetH;

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
//                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(mFragmentReference.get().mPath, bmOptions);

                return mProvider.loadFormula(bitmap);
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
            fragment.scan();
        }

    }
}
