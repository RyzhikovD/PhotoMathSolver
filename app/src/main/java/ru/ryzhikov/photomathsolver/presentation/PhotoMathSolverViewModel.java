package ru.ryzhikov.photomathsolver.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.domain.FormulasInteractor;
import ru.ryzhikov.photomathsolver.domain.SingleLiveEvent;
import ru.ryzhikov.photomathsolver.domain.model.Formula;
import ru.ryzhikov.photomathsolver.presentation.utils.IResourceWrapper;

public class PhotoMathSolverViewModel extends ViewModel {

    private static final int DEFAULT_IMAGE_COMPRESSION = 4;

    private final FormulasInteractor mFormulasInteractor;
    private final Executor mExecutor;
    private final IResourceWrapper mResourceWrapper;

    private final MutableLiveData<List<Formula>> mFormulas = new MutableLiveData<>();
    private final MutableLiveData<Formula> mFormula = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> mImage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final SingleLiveEvent<String> mErrors = new SingleLiveEvent<>();

    PhotoMathSolverViewModel(
            @NonNull FormulasInteractor formulasInteractor,
            @NonNull Executor executor,
            @NonNull IResourceWrapper resourceWrapper) {
        mFormulasInteractor = formulasInteractor;
        mExecutor = executor;
        mResourceWrapper = resourceWrapper;
        mIsLoading.setValue(false);
    }

    public void scanImage(String path, Bitmap bitmap) {
        mIsLoading.setValue(true);
        mExecutor.execute(() -> {
            try {
                Formula formula = mFormulasInteractor.loadFormula(path, bitmapToBase64(bitmap));
                if (formula.getLatex() == null) {
                    throw new IOException();
                } else {
                    mFormula.postValue(formula);
                }
            } catch (IOException e) {
                mErrors.postValue(mResourceWrapper.getString(R.string.failed_to_load_formula));
            }
            mIsLoading.postValue(false);
        });
    }

    public void loadFormulasFromDB() {
        mIsLoading.setValue(true);
        mExecutor.execute(() -> {
            List<Formula> formulas = mFormulasInteractor.loadFormulasFromDB();
            mFormulas.postValue(formulas);
            mIsLoading.postValue(false);
        });
    }

    public void loadImageForFormula(String latexFormula) {
        mIsLoading.setValue(true);
        mExecutor.execute(() -> {
            try {
                Bitmap image = mFormulasInteractor.loadImage(latexFormula);
                mImage.postValue(image);
            } catch (IOException e) {
                mErrors.postValue(mResourceWrapper.getString(R.string.failed_to_load_image));
            }
            mIsLoading.postValue(false);
        });
    }

    public void deleteAllScanedPhotos() {
        mIsLoading.setValue(true);
        mExecutor.execute(() -> {
            mFormulasInteractor.deleteAllFormulas();
            mFormulas.postValue(null);
            mIsLoading.postValue(false);
        });
    }

    public static Bitmap getBitmap(String imagePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = DEFAULT_IMAGE_COMPRESSION;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    private String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }

    @NonNull
    public LiveData<List<Formula>> getFormulas() {
        return mFormulas;
    }

    @NonNull
    public LiveData<Formula> getFormula() {
        return mFormula;
    }

    @NonNull
    public LiveData<Bitmap> getImage() {
        return mImage;
    }

    @NonNull
    public LiveData<Boolean> isLoading() {
        return mIsLoading;
    }

    @NonNull
    public LiveData<String> getErrors() {
        return mErrors;
    }
}
