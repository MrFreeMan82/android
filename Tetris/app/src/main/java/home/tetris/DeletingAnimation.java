package home.tetris;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Дима on 20.02.2017.
 *
 */

class DeletingAnimation extends HandlerThread
{
    private static final String TAG = "DeleteAnimation";
    private static final int DELETE_BAR = 1;

    private Sound sound;
    private List<Tetramino> sceneList;

    private Handler requestHandler;
    private static Handler responseHandler;
    private static BarDeleteListener barDeleteListener;
   // private static ConcurrentMap<Block[][], Integer> concurrentMap = new ConcurrentHashMap<>();

    interface BarDeleteListener
    {
        void onDeleteBlock();
        void onDeleteComplete(int totalLines);
    }

            //В этом классе код выполняется отдельным потоком
    private static class RequestHandler extends Handler
    {
        private void deleteBar(final Block[][] bar)
        {
            for(int i = 0; i < bar.length; i++)
            {
                for (Block block : bar[i])
                {
                    block.active = false;
                    responseHandler.post(new Runnable()
                    {
                        @Override
                        public void run() {
                            barDeleteListener.onDeleteBlock();
                        }
                    });
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ie) {
                        Log.e("DeleteLineAnimation", ie.getMessage());
                    }
                }
            }
            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    barDeleteListener.onDeleteComplete(bar.length);
                }
            });
        }

        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == DELETE_BAR)
            {
                Block[][] bar = (Block[][]) msg.obj;
                deleteBar(bar);
            }
        }
    }

    void setBarDeleteListener(BarDeleteListener listener){barDeleteListener = listener;}

    DeletingAnimation(Handler aResponseHandler, List<Tetramino> aSceneList, Sound aSound)
    {
        super(TAG);
        sound = aSound;
        sceneList = aSceneList;
        responseHandler = aResponseHandler;
    }

    @Override
    protected void onLooperPrepared(){requestHandler = new RequestHandler();}


    private void queueLine(Block[][] bars)
    {
        Log.d(TAG, "request for Bar Delete");
        requestHandler.obtainMessage(DELETE_BAR, bars).sendToTarget();
    }

    private Block[][] getFullLines(int total)
    {
        int bottom = Scene.HEIGHT;
        int lineNo = 0;
        Block[][] result = new Block[Scene.BLOCKS_PER_WIDTH][total];

        while (lineNo != total)
        {
            int index = 0;
            if(countBlock(bottom) == Scene.BLOCKS_PER_WIDTH)
            {
                for (Tetramino current : sceneList)
                {
                    for (Block block : current.getBlocks())
                    {
                        if (block.active && block.rect.bottom == bottom)
                        {
                            result[index][lineNo] = block;
                            index++;
                        }
                    }
                }
                if (index == Scene.BLOCKS_PER_WIDTH) {
                    Arrays.sort(result[lineNo], new CustomComparator());
                    lineNo++;
                }
            }
            bottom -= Tetramino.SQ_SIZE;
        }
        return result;
    }

    private int countBlock(int line)
    {
        int counter = 0;
        for(Tetramino tetramino: sceneList){
            for(Block block: tetramino.getBlocks()){
                if(block.active && block.rect.bottom == line) counter++;
            }
        }
        return counter;
    }

    private int countFullLines()
    {
        int bottom = Scene.HEIGHT;
        int counter = 0;
        while(bottom > 0)
        {
            int k = countBlock(bottom);
            if(k == 0) break;
            if(k == Scene.BLOCKS_PER_WIDTH) counter++;
            bottom -= Tetramino.SQ_SIZE;
        }
        return counter;
    }

    void deleteFullLines()
    {
        int totalFullLines = countFullLines();
        if(totalFullLines == 0) return;

        Block[][] bars = getFullLines(totalFullLines);
        sound.play(Sound.DELETE_LINE);
        queueLine(bars);
    }
}
