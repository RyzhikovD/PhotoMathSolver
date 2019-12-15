package ru.ryzhikov.photomathsolver.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.presentation.PhotoMathSolverViewModel;
import ru.ryzhikov.photomathsolver.presentation.adapter.FormulasAdapter;

public class ImageListFragment extends Fragment {

    private PhotoMathSolverViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private View mLoadingView;
    private TextView mNoScannedImagesText;
    private FormulasAdapter.OnFormulaClickListener mOnFormulaClickListener = (formula) ->
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, EditFormulaFragment.newInstance(formula, mViewModel))
                    .addToBackStack(EditFormulaFragment.class.getSimpleName())
                    .commit();

    {
        setRetainInstance(true);
    }

    static ImageListFragment newInstance(PhotoMathSolverViewModel viewModel) {
        return new ImageListFragment(viewModel);
    }

    private ImageListFragment(PhotoMathSolverViewModel viewModel) {
        mViewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recycler_view_images);
        mLoadingView = view.findViewById(R.id.progress_view);
        mNoScannedImagesText = view.findViewById(R.id.text_scanned_photos);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mViewModel.getFormulas().observe(this, formulas -> {
            if (formulas == null) {
                mNoScannedImagesText.setVisibility(View.VISIBLE);
            } else {
                FormulasAdapter formulasAdapter = new FormulasAdapter(formulas);
                formulasAdapter.setClickListener(mOnFormulaClickListener);
                mRecyclerView.setAdapter(formulasAdapter);
            }
        });
        mViewModel.isLoading().observe(this, isLoading ->
                mLoadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE));
        mViewModel.getErrors().observe(this, error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show());
        mViewModel.loadFormulasFromDB();
    }
}
