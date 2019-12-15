package ru.ryzhikov.photomathsolver.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.domain.model.Formula;
import ru.ryzhikov.photomathsolver.domain.utils.URLConverter;
import ru.ryzhikov.photomathsolver.presentation.PhotoMathSolverViewModel;

public class EditFormulaFragment extends Fragment implements View.OnClickListener {

    private final Formula mFormula;
    private final PhotoMathSolverViewModel mViewModel;
    private EditText mEditFormula;
    private ImageView mImageView;
    private View mLoadingView;

    {
        setRetainInstance(true);
    }

    static EditFormulaFragment newInstance(Formula formula, PhotoMathSolverViewModel viewModel) {
        return new EditFormulaFragment(formula, viewModel);
    }

    private EditFormulaFragment(Formula formula, PhotoMathSolverViewModel viewModel) {
        mFormula = formula;
        mViewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_formula, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditFormula = view.findViewById(R.id.edit_text_formula);
        mImageView = view.findViewById(R.id.image_of_formula);
        mLoadingView = view.findViewById(R.id.progress_view);
        view.findViewById(R.id.button_solve).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEditFormula.setText(mFormula.getWolfram());
        mViewModel.getImage().observe(this, bitmap -> mImageView.setImageBitmap(bitmap));
        mViewModel.isLoading().observe(this, isLoading ->
                mLoadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        mViewModel.getErrors().observe(this, error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show());
        mViewModel.loadImageForFormula(URLConverter.getUrlForLatexFormula(mFormula.getLatex()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_solve) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, WebViewFragment.newInstance(URLConverter.getUrlForWolframFormula(mEditFormula.getText().toString())))
                    .addToBackStack(WebViewFragment.class.getSimpleName())
                    .commit();
        }
    }
}
