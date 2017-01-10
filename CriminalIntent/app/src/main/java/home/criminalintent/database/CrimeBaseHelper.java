package home.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import home.criminalintent.database.CrimeDbShema.CrimeTable;

import static android.content.ContentValues.TAG;

/**
 * Created by Дима on 07.01.2017.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper
{
    private static CrimeBaseHelper mInstance = null;
    private static final int VERSION = 2;
    public static final String DB_NAME = "crimeBase.db";

    public static CrimeBaseHelper get(Context context)
    {
        if(mInstance == null)
            mInstance = new CrimeBaseHelper(context);
        return mInstance;
    }

    private CrimeBaseHelper(Context context)
    {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
      //  Log.d(TAG, "onCreate database");
        db.execSQL("CREATE TABLE " + CrimeTable.NAME + "(" +
                   "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CrimeTable.Cols.UUID + " TEXT," +
                    CrimeTable.Cols.TITLE + " TEXT," +
                    CrimeTable.Cols.DATE + " INTEGER," +
                    CrimeTable.Cols.SOLVED + " INTEGER)"
        );
        onUpgrade(db, 1, 2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
       // Log.d(TAG, "onUpgrade from " + oldVersion + " to " + newVersion);
        switch (oldVersion)
        {
            case 1:
                if(newVersion == 2) {
                    db.beginTransaction();
                    try {
                        db.execSQL("ALTER TABLE " + CrimeTable.NAME +
                                " ADD COLUMN " + CrimeTable.Cols.SUSPECT + " TEXT");
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                }
                break;
        }
    }
}
