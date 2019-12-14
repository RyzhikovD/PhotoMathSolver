package ru.ryzhikov.photomathsolver.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.adapter.FormulasAdapter;
import ru.ryzhikov.photomathsolver.data.room.FormulaDB;
import ru.ryzhikov.photomathsolver.provider.WebDataProvider;

public class ImageListFragment extends Fragment {

    private WebDataProvider mWebDataProvider;
    private RecyclerView mRecyclerView;
    private List<FormulaDB> mFormulas;
    private FormulasAdapter.OnFormulaClickListener mOnFormulaClickListener = (formula) ->
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root, EditFormulaFragment
                            .newInstance(formula.getLatexFormula(), formula.getWolframFormula()))
                    .addToBackStack(EditFormulaFragment.class.getSimpleName())
                    .commit();

    static ImageListFragment newInstance() {
        return new ImageListFragment();
    }

    {
        setRetainInstance(true);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWebDataProvider = new WebDataProvider(requireContext());
        if (mFormulas == null) {
            new LoadFormulasTask(this).execute();
        } else {
            initRecyclerView();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        FormulasAdapter formulasAdapter = new FormulasAdapter();
        formulasAdapter.setNotes(mFormulas);
        formulasAdapter.setClickListener(mOnFormulaClickListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(formulasAdapter);
    }

    // асинк таск желательно в презентер/вью модель перенести
    // на занятии мы делали асинк таск во фрагменте, потому что MVP ещё не проходили
    private static class LoadFormulasTask extends AsyncTask<Void, Void, List<FormulaDB>> {
        private final WeakReference<ImageListFragment> mFragmentRef;

        private final WebDataProvider mProvider;

        private LoadFormulasTask(@NonNull ImageListFragment fragment) {
            mFragmentRef = new WeakReference<>(fragment);
            mProvider = fragment.mWebDataProvider;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<FormulaDB> doInBackground(Void... arg) {
            return mProvider.loadFormulasFromDB();
        }

        @Override
        protected void onPostExecute(List<FormulaDB> formulas) {
            ImageListFragment fragment = mFragmentRef.get();
            if (fragment == null) {
                return;
            }
            if (formulas == null) {
                Toast.makeText(fragment.requireContext(), R.string.failed_to_load_formulas, Toast.LENGTH_SHORT).show();
            } else {
                fragment.mFormulas = new ArrayList<>(formulas);
                fragment.initRecyclerView();
            }
        }

    }
}
