package home.animals;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Дима on 05.02.2017.
 *
 */

final class Node
{
    int id;
    String question, answear;
    int yesId, noId;
}

class Game implements FetchNode.Callback, NewNode.Callback
{
    static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private enum Status {WAIT_REPLY_QUESTION, WAIT_REPLY_ANSWEAR,
                        WAIT_TRUE_ANSWEAR, WAIT_DIFFERENCE, WAIT_CONTINUE, WAIT_FETCH}

    private static final String TAG = "Game";

    private static final int FIRST_QUESTION = 1;
    private boolean additionalQuestion;
    private boolean needDescribe;
    private boolean yesPointer;
    private String[] words = {"меньше", "больше"};
    private Status oldStatus, status;
    private Node Next, Prev, Current;
    private String lastQuestion;
    private Callback callback;
    private static Game game;

    interface Callback {
        void onQuestionChange();
        void onConcede();
        void onContinue();
        void onEndGame();
        void onError(String msg);
    }

    static Game get(Context context)
    {
        if(game == null) game = new Game();
        game.callback = (Callback) context;
        return game;
    }

    void start()
    {
        additionalQuestion = false;
        needDescribe = false;
        yesPointer = false;
        getNext(FIRST_QUESTION, false);
    }

    private String placeAfterWords(String placeText, String text)
    {
        for(String word: words){
            if(text.toLowerCase().contains(word))
                return text.substring(0, text.indexOf(word)) + word + " " + placeText;
        }
        return text;
    }

    private void logging(String s){
        Log.d(TAG, s);
    }

    private void write(String s)
    {
        lastQuestion = s;
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

    @Override public void onErrorCreating(String msg) {callback.onError(msg);}

    @Override public void onFetchNode(Node node)
    {
        status = oldStatus;
        Current = node;
        myQuestion(Current);
    }

    private void fetchNext(int nextId)
    {
        oldStatus = status;
        status = Status.WAIT_FETCH;
        FetchNode.fetch(this, nextId);
    }

    private boolean getNext(int nextId, boolean ifYesOption)
    {
        if(nextId == 0)
        {
            status = Status.WAIT_TRUE_ANSWEAR;
            yesPointer = ifYesOption;
            write("Хорошо, я здаюсь. Кто это?");
            callback.onQuestionChange();
            callback.onConcede();
            return false;
        } else {
            fetchNext(nextId);
            return true;
        }
    }

    void ifYes()
    {
        logging("Yes");
        switch (status){
            case WAIT_CONTINUE:
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
                Next = new Node();
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
                NewNode.newNode(this, Current.id, yesPointer, Next);
                status = Status.WAIT_CONTINUE;
                write("Продолжим?");
                callback.onQuestionChange();
                callback.onContinue();
        }
    }

    String getQuestion() {return lastQuestion;}
}
