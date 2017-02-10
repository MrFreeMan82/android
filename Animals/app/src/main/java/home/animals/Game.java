package home.animals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import home.animals.database.DBShema.NodeTable;
import home.animals.database.DBHelper;


import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Дима on 05.02.2017.
 */

class Game {
    private enum Status {WAIT_REPLY_QUESTION, WAIT_REPLY_ANSWEAR,
                        WAIT_TRUE_ANSWEAR, WAIT_DIFFERENCE, WAIT_CONTINUE}

    private static String TAG = "Game";

    private static final int FIRST_QUESTION = 1;
    private boolean firstStart = true;
    private boolean additionalQuestion = false;
    private boolean needDescribe = false;
    private String[] words = {"меньше", "больше"};
    private Status status;
    private Node Next, Prev, Current;
    private String question;
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

    private Node getNextFromDB(int nextId) {
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
            //game.buf = new ArrayList<>();
            game.db = DBHelper.get(context.getApplicationContext()).getWritableDatabase();
        }
        game.callback = (Callback) context;
        return game;
    }

    void start(){
        if(firstStart) {
            Current = getNextFromDB(FIRST_QUESTION);
            myQuestion(Current);
            firstStart = false;
            additionalQuestion = false;
            needDescribe = false;
        } else {
            callback.onQuestionChange();
        }
    }

    private String placeAfterWords(String placeText, String text){
        for(String word: words){
            if(text.toLowerCase().contains(word))
                return text.substring(0, text.indexOf(word)) + word + " " + placeText;
        }
        return text;
    }

    private void logging(String s){
        Log.d(TAG, s);
    }

    private void write(String s){
        //buf.add(s);
        question = s;
        logging(s);
    }

    private void myQuestion(Node node)
    {
        status = Status.WAIT_REPLY_QUESTION;
        write(node.question + '?');
        callback.onQuestionChange();
    }

    private void myAnswear(Node node)
    {
        status = Status.WAIT_REPLY_ANSWEAR;
        write("Это - " + node.answear + '?');
        callback.onQuestionChange();
    }

    private boolean getNext(int nextId, boolean ifYesOption)
    {
        if(nextId == 0)
        {
            Next = new Node();
            Next.id = DBHelper.getNextId(NodeTable.NAME);
            if(ifYesOption) Current.yesId = Next.id; else Current.noId = Next.id;
            status = Status.WAIT_TRUE_ANSWEAR;
            write("Хорошо, я здаюсь. Кто это?");
            callback.onQuestionChange();
            callback.onConcede();
            return false;
        } else {
            Current = getNextFromDB(nextId);
            return true;
        }
    }

    void ifYes()
    {
        logging("Yes");
        switch (status){
            case WAIT_CONTINUE:
                firstStart = true;
                start();
                return;
            case WAIT_REPLY_QUESTION:
                if(Current.yesId > 0) {
                    additionalQuestion = true;
                    Prev = Current;
                    getNext(Current.yesId, false);
                    myQuestion(Current);
                } else {
                    additionalQuestion = false;
                    myAnswear(Current);
                }
                return;
            case WAIT_REPLY_ANSWEAR:
                status = Status.WAIT_CONTINUE;
                write("Ура я выиграла!!!! Еще разок?");
                callback.onQuestionChange();
                callback.onContinue();
        }
    }

    void ifNo()
    {
       logging("No");
       switch (status) {
           case WAIT_CONTINUE:
               firstStart = true;
               callback.onEndGame();
               return;
           case WAIT_REPLY_QUESTION:
               if(additionalQuestion) {
                   myAnswear(Prev);
               } else {
                   if (getNext(Current.noId, false)){
                       myQuestion(Current);
                   }
                   else {
                       needDescribe = true;
                   }
               }
               return;
           case WAIT_REPLY_ANSWEAR:
               if(additionalQuestion) {
                   if(getNext(Current.noId, false)){
                       myQuestion(Current);
                   } else {
                       needDescribe = true;
                   }
                   additionalQuestion = false;
               } else {
                   if (getNext(Current.yesId, true)) myQuestion(Current);
               }
       }
    }

    void ifElse(String elseText)
    {
        logging(elseText);
        switch (status) {
            case WAIT_TRUE_ANSWEAR:
                Next.answear = elseText.toLowerCase();
                status = Status.WAIT_DIFFERENCE;
                if(needDescribe){
                    write("Опишите " + Next.answear + " в двух словах?");
                    needDescribe = false;
                } else {
                    write("Чем " + Next.answear + " отличается от " + Current.answear + "?");
                }
                callback.onQuestionChange();
                return;
            case WAIT_DIFFERENCE:
                elseText = elseText.substring(0, 1).toUpperCase() + elseText.substring(1).toLowerCase();
                Next.question = placeAfterWords("чем " + Current.answear, elseText);
                ContentValues values = getContentValues(Next);
                db.insert(NodeTable.NAME, null, values);
                values = getContentValues(Current);
                db.update(NodeTable.NAME, values,
                        NodeTable.Cols.NODE_ID + "=?",
                        new String[]{Integer.toString(Current.id)});
                status = Status.WAIT_CONTINUE;
                write("Продолжим?");
                callback.onQuestionChange();
                callback.onContinue();
        }
    }

    String getQuestion() {return question;}
}
