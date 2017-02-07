package home.animals.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Locale;

import home.animals.R;
import home.animals.database.DBShema.NodeTable;

/**
 * Created by Дима on 07.02.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private String firstQuestion, firstAnswear;
    private static DBHelper helper = null;
    private static final int VERSION = 1;
    private static final String DB_NAME = "animals.db";

    public static DBHelper get(Context context)
    {
        if(helper == null) helper = new DBHelper(context);
        return helper;
    }

    public static int getNextId(String tableName)
    {
        String qry = String.format(Locale.US,
                "SELECT seq FROM sqlite_sequence WHERE name = \"%s\"", tableName);
        SQLiteDatabase db = helper.getReadableDatabase();
        int id = 0;
        Cursor cursor = db.rawQuery(qry, null);
        try {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex("seq")) + 1;
        }
        finally {
            cursor.close();
        }
        return id;
    }

    private DBHelper(Context context)
    {
        super(context, DB_NAME, null, VERSION);
        firstQuestion = context.getString(R.string.first_question);
        firstAnswear = context.getString(R.string.first_answear);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + NodeTable.NAME + "(" +
                   NodeTable.Cols.NODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                   NodeTable.Cols.QUESTION + " TEXT," +
                   NodeTable.Cols.ANSWEAR + " TEXT," +
                   NodeTable.Cols.YES_NODE_ID + " INTEGER," +
                   NodeTable.Cols.NO_NODE_ID + " INTEGER)"
        );

        db.execSQL("INSERT INTO " + NodeTable.NAME + "(" +
                    NodeTable.Cols.QUESTION + "," +
                    NodeTable.Cols.ANSWEAR + ") VALUES(" +
                    "\"" + firstQuestion + "\"," +
                    "\"" + firstAnswear + "\")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {}

}
