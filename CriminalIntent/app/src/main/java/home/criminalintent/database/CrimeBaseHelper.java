package home.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import home.criminalintent.database.CrimeDbShema.CrimeTable;

/**
 * Created by Дима on 07.01.2017.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 1;
    public static final String DB_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context)
    {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                   "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CrimeTable.Cols.UUID + " TEXT," +
                    CrimeTable.Cols.TITLE + " TEXT," +
                    CrimeTable.Cols.DATE + " INTEGER," +
                    CrimeTable.Cols.SOLVED + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {}
}
