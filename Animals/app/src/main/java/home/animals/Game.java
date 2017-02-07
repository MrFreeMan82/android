package home.animals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import home.animals.database.DBShema.NodeTable;
import home.animals.database.DBHelper;


import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Дима on 05.02.2017.
 */

class Game {
    private enum Status {NONE, WAIT_REPLY_QUESTION,
        WAIT_REPLY_ANSWEAR, WAIT_TRUE_ANSWEAR, WAIT_DIFFERENCE, WAIT_CONTINUE}

    private static final int FIRST_QUESTION = 1;
    private boolean trueOption = false;
    private boolean firstStart = true;
    private Status status;
    private Node First, Next, Current;
    private ArrayList<String> buf;
    private Callback callback;
    private SQLiteDatabase db;
    private static Game game;

    interface Callback {
        void onQuestionChange();
        void onConcede();
        void onContinue();
        void onEndGame();
    }

    private class Node {
        int id;
        String question, answear;
        int yesId, noId;
    }

    private class NodeCursorWrapper extends CursorWrapper{
        NodeCursorWrapper(Cursor cursor) {super(cursor);}

        Node getNode(){
            Node node = new Node();
            node.id = getInt(getColumnIndex(NodeTable.Cols.NODE_ID));
            node.question = getString(getColumnIndex(NodeTable.Cols.QUESTION));
            node.answear = getString(getColumnIndex(NodeTable.Cols.ANSWEAR));
            node.yesId = getInt(getColumnIndex(NodeTable.Cols.YES_NODE_ID));
            node.noId = getInt(getColumnIndex(NodeTable.Cols.NO_NODE_ID));

            return node;
        }
    }

    private Node getNext(int nextId) {
        String qry = String.format(Locale.US,
                "SELECT * FROM %s WHERE %s= %d",
                NodeTable.NAME, NodeTable.Cols.NODE_ID, nextId);

        Node node = null;
        Cursor cursor = db.rawQuery(qry, null);
        NodeCursorWrapper cw = new NodeCursorWrapper(cursor);
        try {
            cw.moveToFirst();
            node = cw.getNode();
        }
        finally {
            cursor.close();
        }
        return node;
    }

    private ContentValues getContentValues(Node node)
    {
        ContentValues values = new ContentValues();
        values.put(NodeTable.Cols.NODE_ID, node.id);
        values.put(NodeTable.Cols.QUESTION, node.question);
        values.put(NodeTable.Cols.ANSWEAR, node.answear);
        values.put(NodeTable.Cols.YES_NODE_ID, node.yesId);
        values.put(NodeTable.Cols.NO_NODE_ID, node.noId);
        return values;
    }

    static Game get(Context context) {
        if(game == null) {
            game = new Game();
            game.buf = new ArrayList<>();
            game.db = DBHelper.get(context.getApplicationContext()).getWritableDatabase();
        }
        game.callback = (Callback) context;
        return game;
    }

    void start(){
        if(firstStart) {
            First = getNext(FIRST_QUESTION);
            Current = First;
            myQuestion();
            firstStart = false;
        } else {
            callback.onQuestionChange();
        }
    }

    private void myQuestion()
    {
        status = Status.WAIT_REPLY_QUESTION;
        buf.add(Current.question + '?');
        callback.onQuestionChange();
    }

    private void myAnswear()
    {
        status = Status.WAIT_REPLY_ANSWEAR;
        buf.add("Это - " + Current.answear + '?');
        callback.onQuestionChange();
    }

    private void setNext(int nextId)
    {
        if(nextId == 0)
        {
            Next = new Node();
            Next.id = DBHelper.getNextId(NodeTable.NAME);
            status = Status.WAIT_TRUE_ANSWEAR;
            buf.add("Хорошо, я здаюсь. Кто это?");
            callback.onQuestionChange();
            callback.onConcede();
        } else {
            Current = getNext(nextId);
            myQuestion();
        }
    }

    void ifTrue()
    {
        switch (status){
            case WAIT_CONTINUE:
                status = Status.NONE;
                myQuestion();
                return;
            case WAIT_REPLY_QUESTION:
                status = Status.NONE;
                myAnswear();
                return;
            case WAIT_REPLY_ANSWEAR:
                status = Status.WAIT_CONTINUE;
                buf.add("Ура я выиграла!!!! Еще разок?");
                callback.onQuestionChange();
                callback.onContinue();
                return;
            default:
                trueOption = true;
                setNext(Current.yesId);
        }
    }

    void ifFalse()
    {
       switch (status) {
           case WAIT_CONTINUE:
               callback.onEndGame();
               return;
           default:
               trueOption = false;
               setNext(Current.noId);
       }
    }

    void ifElse(String elseText)
    {
        switch (status) {
            case WAIT_TRUE_ANSWEAR:
                Next.answear = elseText.toLowerCase();
                status = Status.WAIT_DIFFERENCE;
                buf.add("Какая разница между '" + Current.answear + "' и '" + Next.answear + "'?");
                callback.onQuestionChange();
                return;
            case WAIT_DIFFERENCE:
                Next.question = elseText.toLowerCase();
                ContentValues values = getContentValues(Next);
                db.insert(NodeTable.NAME, null, values);
                if(trueOption) Current.yesId = Next.id; else Current.noId = Next.id;
                values = getContentValues(Current);
                db.update(NodeTable.NAME, values,
                        NodeTable.Cols.NODE_ID + "=?",
                        new String[]{Integer.toString(Current.id)});
                Current = First;
                status = Status.WAIT_CONTINUE;
                buf.add("Продолжим?");
                callback.onQuestionChange();
                callback.onContinue();
        }
    }

    String getQuestion() {return buf.get(buf.size() - 1);}
}
