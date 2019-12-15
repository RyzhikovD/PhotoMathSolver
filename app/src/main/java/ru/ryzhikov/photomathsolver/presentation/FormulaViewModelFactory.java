package ru.ryzhikov.photomathsolver.presentation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.ryzhikov.photomathsolver.data.FormulasRepository;
import ru.ryzhikov.photomathsolver.domain.FormulasInteractor;
import ru.ryzhikov.photomathsolver.domain.IFormulasRepository;
import ru.ryzhikov.photomathsolver.presentation.utils.ResourceWrapper;

public class FormulaViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Context mApplicationContext;

    public FormulaViewModelFactory(@NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (PhotoMathSolverViewModel.class.equals(modelClass)) {
            IFormulasRepository currenciesRepository = new FormulasRepository(mApplicationContext);
            FormulasInteractor interactor = new FormulasInteractor(currenciesRepository);
            Executor executor = Executors.newSingleThreadExecutor();
            ResourceWrapper resourceWrapper = new ResourceWrapper(mApplicationContext.getResources());
//             noinspection unchecked
            return (T) new PhotoMathSolverViewModel(
                    interactor,
                    executor,
                    resourceWrapper);
        } else {
            return super.create(modelClass);
        }
    }
}
