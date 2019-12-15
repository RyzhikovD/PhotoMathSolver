package ru.ryzhikov.photomathsolver.presentation.adapter;

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
import java.util.List;

import ru.ryzhikov.photomathsolver.R;
import ru.ryzhikov.photomathsolver.domain.model.Formula;

public class FormulasAdapter extends RecyclerView.Adapter<FormulasAdapter.FormulaHolder> {

    private List<Formula> mFormulas;
    private OnFormulaClickListener mClickListener;

    public FormulasAdapter(List<Formula> formulas) {
        mFormulas = new ArrayList<>(formulas);
    }

    @NonNull
    @Override
    public FormulaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FormulaHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_formula, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FormulaHolder holder, int position) {
        holder.bind(mFormulas.get(position));
    }

    @Override
    public int getItemCount() {
        return mFormulas == null ? 0 : mFormulas.size();
    }

    public void setClickListener(@Nullable OnFormulaClickListener clickListener) {
        mClickListener = clickListener;
    }

    class FormulaHolder extends RecyclerView.ViewHolder {

        private ImageView mPhoto;

        public FormulaHolder(@NonNull View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.photo_item);
        }

        public void bind(Formula formula) {
            Bitmap bitmap = BitmapFactory.decodeFile(formula.getImagePath());
            mPhoto.setImageBitmap(bitmap);

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
        void onItemClick(@NonNull Formula formula);
    }
}
