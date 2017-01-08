package home.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import home.criminalintent.database.CrimeBaseHelper;
import static home.criminalintent.database.CrimeDbShema.*;

/**
 * Created by Дима on 02.01.2017.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDB;

    private class CrimeCursorWrapper extends CursorWrapper
    {
        public CrimeCursorWrapper(Cursor cursor)
        {super(cursor);}

        public Crime getCrime()
        {
            String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
            String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
            long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
            int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));

            Crime crime = new Crime(UUID.fromString(uuidString));
            crime.setTitle(title);
            crime.setDate(new Date(date));
            crime.setSolved(isSolved != 0);

            return crime;
        }
    }

    public static CrimeLab get(Context context)
    {
        if(sCrimeLab == null){sCrimeLab = new CrimeLab(context);}
        return sCrimeLab;
    }
    private CrimeLab(Context context)
    {
        mContext = context.getApplicationContext();
        mDB = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public List<Crime> getCrimes()
    {
        List<Crime> crimes = new ArrayList<>();

        String qry = String.format("SELECT * FROM " + CrimeTable.NAME);

        CrimeCursorWrapper cw = qryCrime(qry);

        try{
            cw.moveToFirst();
            while(!cw.isAfterLast())
            {
                crimes.add(cw.getCrime());
                cw.moveToNext();
            }
        }
        finally {
            cw.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id)
    {
        String qry = String.format(
                "SELECT * FROM " + CrimeTable.NAME +
                        " WHERE " + CrimeTable.Cols.UUID + "=\"%s\"", id.toString()
        );

        CrimeCursorWrapper cw = qryCrime(qry);
        try {
            if (cw.getCount() == 0) return null;

            cw.moveToFirst();
            return cw.getCrime();
        }
        finally {
            cw.close();
        }
    }

    public void addCrime(Crime c)
    {
        ContentValues values = getContentValues(c);
        mDB.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime c)
    {
        String uuidString = c.getID().toString();
        ContentValues values = getContentValues(c);

        mDB.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + "=?",
                            new String[]{uuidString});
    }

    public void deleteCrime(Crime c)
    {
        Log.d("TAG", "Deleting crime with id = " + c.getID().toString(), new Exception());

        String qry = String.format(
          "DELETE FROM " + CrimeTable.NAME +
                  " WHERE " + CrimeTable.Cols.UUID + "=\"%s\"", c.getID().toString()
        );
        mDB.execSQL(qry);
    }

    private static ContentValues getContentValues(Crime crime)
    {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getID().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved()? 1:0);
        return values;
    }

    private CrimeCursorWrapper qryCrime(String qry)
    {
        Cursor cursor = mDB.rawQuery(qry, null);

        return new CrimeCursorWrapper(cursor);
    }

}
