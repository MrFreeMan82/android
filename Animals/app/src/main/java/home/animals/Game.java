package home.animals;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Дима on 05.02.2017.
 */

class Game {
    private boolean waitForReplyMyAnswear = false;
    private boolean waitForAnswear = false;
    private boolean waitForReplyMyQuestion = false;
    private boolean waitForDifference = false;
    private boolean waitForContinue = false;
    private boolean firstStart = true;
    private TreeElement First, Next, Current;
    private ArrayList<String> buf;
    private Callback callback;
    private static Game game;

    interface Callback {
        void onQuestionChange();
        void onConcede();
        void onContinue();
        void onEndGame();
    }

    private class TreeElement {
        String question, answear;
        TreeElement TrueElement, FalseElement;

        TreeElement (String aQuestion, String aAnswear)
        {
            question = aQuestion;
            answear = aAnswear;
        }

        TreeElement(){}
    }

    private Game(Context context)
    {
        //callback = (Callback) context;
        Current = new TreeElement(context.getString(R.string.first_question), context.getString(R.string.first_answear));
        First = Current;
        Next = null;
        buf = new ArrayList<>();
    }

    static Game get(Context context){
        if(game == null) {
            game = new Game(context);
        }
        game.callback = (Callback) context;

        return game;
    }

    void start(){
        if(firstStart) {
            myQuestion();
            firstStart = false;
        } else {
            callback.onQuestionChange();
        }
    }

    private void myQuestion()
    {
        waitForReplyMyQuestion = true;
        buf.add(Current.question + '?');
        callback.onQuestionChange();
    }

    private void myAnswear()
    {
        waitForReplyMyAnswear = true;
        buf.add("Это - " + Current.answear + '?');
        callback.onQuestionChange();
    }

    private void setNext(TreeElement aNext, boolean ifTrue)
    {
        Next = aNext;
        if(Next == null)
        {
            waitForAnswear = true;
            Next = new TreeElement();
            if(ifTrue) Current.TrueElement = Next; else Current.FalseElement = Next;
            buf.add("Хорошо, я здаюсь. Кто это?");
            callback.onQuestionChange();
            callback.onConcede();
        } else {
            Current = Next;
            myQuestion();
        }
    }

    void ifTrue()
    {
        if(waitForContinue) {
            waitForContinue = false;
            myQuestion();
            return;
        }

        if(waitForReplyMyQuestion) {
            waitForReplyMyQuestion = false;
            myAnswear();
            return;
        }

        if(waitForReplyMyAnswear)
        {
            buf.add("Ура я выграла!!!! Еще разок?");
            callback.onQuestionChange();
            waitForContinue = true;
            callback.onContinue();
            return;
        }

        setNext(Current.TrueElement, true);
    }

    void ifFalse()
    {
       if(waitForContinue) {
           waitForContinue = false;
           callback.onEndGame();
           return;
       }
       setNext(Current.FalseElement, false);
    }

    void ifElse(String elseText)
    {
        if(waitForAnswear) {
            Next.answear = elseText;
            waitForAnswear = false;
        }

        if(waitForDifference) {
            Next.question = elseText;
            waitForDifference = false;
            Current = First;
            waitForContinue = true;
            buf.add("Продолжим?");
            callback.onQuestionChange();
            callback.onContinue();
            return;
        }

        waitForDifference = true;
        buf.add("Какая разница между " + Current.answear + " и " + Next.answear + '?');
        callback.onQuestionChange();
    }

    String getQuestion() {return buf.get(buf.size() - 1);}
}
