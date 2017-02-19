package home.tetris;

import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Дима on 19.02.2017.
 *
 */

// Remove finished tasks when clear button pressed
class TetrisAnimator
{
    private int taskCounter = 0;
    private Callback callback;
    private List<AsyncTask<Integer, Tetramino, Void>> taskList;

    interface Callback
    {
        void onDeleteBlock(Tetramino tetramino, int column);
        void onDeleteComplete();
    }

    private class CustomComparator implements Comparator<Tetramino>
    {
        @Override
        public int compare(Tetramino a, Tetramino b)
        {return (a.getMinLeft() > b.getMinLeft())? 1: -1;}

        @Override
        public boolean equals(Object obj){return false;}
    }

    TetrisAnimator(Callback aCallBack)
    {
        callback = aCallBack;
        taskList = new ArrayList<>(4);
    }

    void clear()
    {
        taskCounter = 0;
        taskList.clear();
    }

    void addDeleteLineAnimation(Tetramino[] deletingLine, int line)
    {
        Arrays.sort(deletingLine, new CustomComparator());

        taskList.add(new DeleteLineAnimation(callback, deletingLine));
        taskList.get(taskCounter).execute(line);
        taskCounter++;
    }
}

class DeleteLineAnimation extends AsyncTask<Integer, Tetramino, Void>
{
    private int column = 0;
    private Tetramino[] deletingLine;
    private TetrisAnimator.Callback callback;

    DeleteLineAnimation(TetrisAnimator.Callback aCallback, Tetramino[] aLine)
    {
        deletingLine = aLine;
        callback = aCallback;
    }

    @Override
    protected Void doInBackground(final Integer ... params)
    {
       for(Tetramino tetramino: deletingLine)
       {
           for(Rect block: tetramino.getBlocks())
           {
               if(block != null && block.bottom == params[0])
               {
                   column++;
                   tetramino.replaceBlock(null, block);
                   publishProgress(tetramino);

                   try {
                       Thread.sleep(20);
                   } catch (InterruptedException ie) {
                       Log.e("DeleteLineAnimation", ie.getMessage());
                   }
               }
           }
       }
       return null;
    }

    @Override
    protected void onProgressUpdate(Tetramino... params)
    {
        callback.onDeleteBlock(params[0], column);
    }

    @Override
    protected void onPostExecute(Void result)
    {
       // Log.d("DeleteLineAnimation", "task #" + result + " complete");
    }
}


