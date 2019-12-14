package ru.ryzhikov.photomathsolver.data.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FormulaDB.class}, version = 1)
public abstract class FormulasDatabase extends RoomDatabase {

    public abstract FormulasDAO getFormulasDao();
}
