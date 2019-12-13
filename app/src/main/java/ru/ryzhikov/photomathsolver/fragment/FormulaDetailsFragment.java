//package com.example.learningprogram.fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.learningprogram.adapters.DetailsFragmentSubtopicsAdapter;
//import com.example.learningprogram.models.Lecture;
//
//import ru.ryzhikov.photomathsolver.R;
//import ru.ryzhikov.photomathsolver.data.room.FormulaDB;
//
///**
// * Фрагмент детальной информации о лекции.
// *
// * @author Evgeny Chumak
// **/
//public class FormulaDetailsFragment extends Fragment {
//
//    private static final String ARG_FORMULA = "ARG_FORMULA";
//
//    public static com.example.learningprogram.fragment.FormulaDetailsFragment newInstance(@NonNull FormulaDB lecture) {
//        com.example.learningprogram.fragment.FormulaDetailsFragment fragment = new com.example.learningprogram.fragment.FormulaDetailsFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(ARG_FORMULA, lecture);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_formula_details, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Lecture lecture = getLectureFromArgs();
//        ((TextView) view.findViewById(R.id.number)).setText(String.valueOf(lecture.getNumber()));
//        ((TextView) view.findViewById(R.id.date)).setText(lecture.getDate());
//        ((TextView) view.findViewById(R.id.theme)).setText(lecture.getTheme());
//        ((TextView) view.findViewById(R.id.lector)).setText(lecture.getLector());
//
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setAdapter(new DetailsFragmentSubtopicsAdapter(lecture.getSubtopics()));
//    }
//
//    @NonNull
//    private FormulaDB getLectureFromArgs() {
//        Bundle arguments = getArguments();
//        if (arguments == null) {
//            throw new IllegalStateException("Arguments must be set");
//        }
//        FormulaDB lecture = arguments.getParcelable(ARG_FORMULA);
//        if (lecture == null) {
//            throw new IllegalStateException("Formula must be set");
//        }
//        return lecture;
//    }
//}
