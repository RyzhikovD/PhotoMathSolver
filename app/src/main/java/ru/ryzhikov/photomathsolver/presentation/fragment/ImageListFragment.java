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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.presentation.PhotoMathSolverViewModel;
import ru.ryzhikov.photomathsolver.presentation.adapter.FormulasAdapter;

public class ImageListFragment extends Fragment implements View.OnClickListener {

    private PhotoMathSolverViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private View mLoadingView;
    private TextView mNoScannedImagesText;
    private ExtendedFloatingActionButton mDeleteAllButton;
    private FormulasAdapter mFormulasAdapter;
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
        (mDeleteAllButton = view.findViewById(R.id.button_delete_all)).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mDeleteAllButton.hide();
        mRecyclerView.setLayoutManager(layoutManager);
        mViewModel.getFormulas().observe(this, formulas -> {
            if (formulas == null || formulas.isEmpty()) {
                mNoScannedImagesText.setVisibility(View.VISIBLE);
                if (mFormulasAdapter != null) {
                    mFormulasAdapter.deleteAllFormulas();
                    mRecyclerView.setAdapter(mFormulasAdapter);
                }
                mDeleteAllButton.hide();
            } else {
                mNoScannedImagesText.setVisibility(View.GONE);
                mFormulasAdapter = new FormulasAdapter(formulas);
                mFormulasAdapter.setClickListener(mOnFormulaClickListener);
                mRecyclerView.setAdapter(mFormulasAdapter);
                mDeleteAllButton.show();
            }
        });
        mViewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                mLoadingView.setVisibility(View.VISIBLE);
                mDeleteAllButton.hide();
            } else {
                mLoadingView.setVisibility(View.GONE);
                if (mFormulasAdapter != null && mFormulasAdapter.getItemCount() != 0) {
                    mDeleteAllButton.show();
                }
            }
        });
        mViewModel.getErrors().observe(this, error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show());
        mViewModel.loadFormulasFromDB();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_delete_all) {
            mViewModel.deleteAllScanedPhotos();
        }
    }
}
