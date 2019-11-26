package ru.ryzhikov.photomathsolver.fragment;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.URLConverter;
import ru.ryzhikov.photomathsolver.provider.LearningProgramProvider;

public class EditFormulaFragment extends Fragment implements View.OnClickListener {

    private final String mScannedFormula;
    private EditText mFormula;
    private ImageView mImageView;
    private final LearningProgramProvider mLearningProgramProvider = new LearningProgramProvider();

    {
        setRetainInstance(true);
    }

    static EditFormulaFragment newInstance(String formula) {
        return new EditFormulaFragment(formula);
    }

    private EditFormulaFragment(String formula) {
        mScannedFormula = formula;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_formula, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFormula = view.findViewById(R.id.edit_text_formula);
        mImageView = view.findViewById(R.id.image_of_formula);
        view.findViewById(R.id.button_solve).setOnClickListener(this);
        view.findViewById(R.id.button_update_image).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFormula.setText(mScannedFormula);
        loadImage(URLConverter.getUrlForFormula(mScannedFormula));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_solve:
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.root, WebViewFragment.newInstance(mFormula.getText().toString()))
                        .addToBackStack(WebViewFragment.class.getSimpleName())
                        .commit();
                break;
            case R.id.button_update_image:
                loadImage(URLConverter.getUrlForFormula(mFormula.getText().toString()));
                break;
        }
    }

    private void loadImage(String formula) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(this);
        downloadImageTask.execute(formula);
    }

    private void updateImage() {
        mImageView.setImageBitmap(mLearningProgramProvider.provideImage());
    }

//    private void initEditText() {
//        mFormula.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<EditFormulaFragment> mFragmentReference;

        private final LearningProgramProvider mProvider;


        private DownloadImageTask(@NonNull EditFormulaFragment fragment) {
            mFragmentReference = new WeakReference<>(fragment);
            mProvider = fragment.mLearningProgramProvider;
        }

        protected Bitmap doInBackground(String... formulas) {
            return mProvider.loadImage(formulas[0]);
        }

        protected void onPostExecute(Bitmap result) {
            EditFormulaFragment fragment = mFragmentReference.get();
            if (fragment == null) {
                return;
            }
            fragment.updateImage();
        }

    }
}
