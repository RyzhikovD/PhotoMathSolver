package ru.ryzhikov.photomathsolver.presentation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.presentation.fragment.ChoosePhotoFragment;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_TO_USE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showPermissionRationale();
            } else {
                requestPermission();
            }
        } else {
            chooseImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_TO_USE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                showPermissionRationale();
            }
        }
    }

    private void showPermissionRationale() {
        new AlertDialog.Builder(this)
                .setMessage("This permission is mandatory for the application. Please allow access.")
                .setPositiveButton("OK", (dialog, which) ->
                        requestPermission())
                .setNegativeButton("Cancel", (dialog, which) ->
                        showPermissionRationale())
                .create()
                .show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_TO_USE_EXTERNAL_STORAGE);
    }

    private void chooseImage() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.root, ChoosePhotoFragment.newInstance())
                .commit();
    }
}
