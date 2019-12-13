package ru.ryzhikov.photomathsolver.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.data.room.FormulaDB;

public class FormulasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FormulaDB> mFormulas = Collections.emptyList();
    private OnFormulaClickListener mClickListener;

    public FormulasAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FormulaHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_formula, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FormulaHolder) holder).bind(mFormulas.get(position));
    }

    @Override
    public int getItemCount() {
        return mFormulas == null ? 0 : mFormulas.size();
    }

    @Override
    public long getItemId(int position) {
        return mFormulas.get(position).getId();
    }

    public void setNotes(@Nullable List<FormulaDB> formulas) {
        if (formulas == null) {
            mFormulas = new ArrayList<>();
        } else {
            mFormulas = new ArrayList<>(formulas);
        }
        notifyDataSetChanged();
    }

    public void setClickListener(@Nullable OnFormulaClickListener clickListener) {
        mClickListener = clickListener;
    }

    private class FormulaHolder extends RecyclerView.ViewHolder {

        private ImageView mPhoto;
        private String mWolframText;
        private String mLatexText;


        public FormulaHolder(@NonNull View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.photo_item);
        }

        public void bind(FormulaDB formula) {
            Bitmap bitmap = BitmapFactory.decodeFile(formula.getPath());
            mPhoto.setImageBitmap(bitmap);

//            File imgFile = new File(formula.getPath());
//
//            if(imgFile.exists()){
//                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                mPhoto.setImageBitmap(bitmap);
//            }

            mWolframText = formula.getWolframFormula();
            mLatexText = formula.getLatexFormula();
            itemView.setOnClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.onItemClick(formula);
                }
            });
        }
    }

    public interface OnFormulaClickListener {
        /**
         * Обрабатывает нажатие на элемент списка с переданной формулой
         *
         * @param formula на элемент списка с какой формулой нажали
         */
        void onItemClick(@NonNull FormulaDB formula);
    }
}
