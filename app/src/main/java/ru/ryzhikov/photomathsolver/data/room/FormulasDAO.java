package ru.ryzhikov.photomathsolver.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FormulasDAO {

    @Query("SELECT * FROM FormulaDB")
    LiveData<List<FormulaDB>> getAllFormulas();

    @Query("SELECT * FROM FormulaDB WHERE id = :formulaId")
    FormulaDB getFormulaById(long formulaId);

    @Query("SELECT * FROM FormulaDB WHERE path = :path")
    FormulaDB getFormulaByPath(String path);

    @Insert
    long addFormula(FormulaDB formula);

    @Delete
    void removeFormulas(FormulaDB... formulas);

    @Query("DELETE FROM FormulaDB WHERE 1")
    void removeAll();

    @Update
    void updateFormula(FormulaDB formula);
}
