package ru.ryzhikov.photomathsolver.presentation.utils;

import android.content.res.Resources;

import androidx.annotation.NonNull;

/**
 * Обёртка над ресурсами приложения, нужна для того чтобы
 * вью модель и доменный слой не зависели от классов из Android SDK.
 *
 * @author Evgeny Chumak
 **/
public class ResourceWrapper implements IResourceWrapper {

    private final Resources mResources;

    public ResourceWrapper(@NonNull Resources resources) {
        mResources = resources;
    }

    @Override
    public String getString(int resId) {
        return mResources.getString(resId);
    }
}
