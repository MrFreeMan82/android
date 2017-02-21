package home.tetris;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Дима on 20.02.2017.
 *
 */

class DeletingAnimation extends HandlerThread
{
    private static final String TAG = "DeleteAnimation";
    private static final int DELETE_BAR = 1;

    private static Scene scene;
    private Handler requestHandler;
    private static Handler responseHandler;
    private static BarDeleteListener barDeleteListener;
   // private static ConcurrentMap<Block[][], Integer> concurrentMap = new ConcurrentHashMap<>();

    interface BarDeleteListener
    {
        void onRepaint();
        void onBarDelete();
        void onDeleteComplete();
    }

            //В этом классе код выполняется отдельным потоком
    private static class RequestHandler extends Handler
    {
        Runnable complete = new Runnable() {
            @Override
            public void run() {barDeleteListener.onDeleteComplete();}
        };

        Runnable repaint = new Runnable() {
            @Override
            public void run() {barDeleteListener.onRepaint();}
        };

        Runnable delete = new Runnable() {
            @Override
            public void run() {
                barDeleteListener.onBarDelete();
            }
        };

        private void deleteBars()
        {
            boolean playOnes = true;
            int bottom = Scene.HEIGHT;
            int deleted = 0;

            while (bottom > 0)
            {
                int blockCount = countBlock(bottom);
                if(blockCount == Scene.BLOCKS_PER_WIDTH)
                {
                    int index = 0;
                    Block[] bar = new Block[Scene.BLOCKS_PER_WIDTH];

                    for(Tetramino tetramino : scene.getSceneList())
                    {
                        for(Block block : tetramino.getBlocks())
                        {
                            if(block.active && block.rect.bottom == bottom)
                            {
                                bar[index] = block;
                                index++;
                            }
                        }
                    }
                    if(playOnes) {
                        scene.getSound().play(Sound.DELETE_LINE);
                        playOnes = false;
                    }
                    deleted++;
                    deleting(bar);
                    responseHandler.post(delete);
                }
                else if(blockCount == 0) break;

                bottom -= Tetramino.SQ_SIZE;
            }
            falling(bottom, deleted);
            responseHandler.post(complete);
        }

        private void deleting(Block[] bar)
        {
            Arrays.sort(bar, new CustomComparator());

            while (bar[0].rect.bottom != bar[0].rect.top)
            {
                int k = 1;
                for (Block block : bar)
                {
                    block.rect.bottom -= 1;
                    block.updateSubRect();
                    if(block.rect.bottom == block.rect.top) block.active = false;
                    if ((k == Scene.BLOCKS_PER_WIDTH) && (block.rect.bottom % 2 == 0))
                    {
                        responseHandler.post(repaint);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            Log.e("DeleteLineAnimation", ie.getMessage());
                        }
                    }
                    k++;
                }
            }
        }

        private void falling(int top, int totalDeleted)
        {
            int bottom = Scene.HEIGHT;
            int moved = 0;
            int size = scene.getSceneList().size();
            if((size == 0) || (size == 1)) return;

            while (bottom > top)
            {
                while (countBlock(bottom) == 0 && totalDeleted != moved)
                {
                    for (Tetramino tetramino : scene.getSceneList())
                    {
                        if(tetramino == scene.getCurrentMino()) continue;
                        for (Block block : tetramino.getBlocks())
                        {
                            if (block.active && block.rect.bottom < bottom)
                            {
                                block.rect.top += Tetramino.SQ_SIZE;
                                block.rect.bottom = block.rect.top + Tetramino.SQ_SIZE;
                                block.updateSubRect();
                            }
                        }
                    }
                    moved++;
                }
                bottom -= Tetramino.SQ_SIZE;
            }
        }

        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == DELETE_BAR)
            {
               // Block[][] bar = (Block[][]) msg.obj;
                deleteBars();
            }
        }
    }

    void setBarDeleteListener(BarDeleteListener listener){barDeleteListener = listener;}

    DeletingAnimation(Handler aResponseHandler, Scene aScene)
    {
        super(TAG);
        scene = aScene;
        responseHandler = aResponseHandler;
    }

    @Override
    protected void onLooperPrepared(){requestHandler = new RequestHandler();}


    private static int countBlock(int line)
    {
        int counter = 0;
        for(Tetramino tetramino: scene.getSceneList()){
            for(Block block: tetramino.getBlocks()){
                if(block.active && block.rect.bottom == line) counter++;
            }
        }
        return counter;
    }

    void deleteFullLines()
    {
        requestHandler.obtainMessage(DELETE_BAR).sendToTarget();
    }
}
