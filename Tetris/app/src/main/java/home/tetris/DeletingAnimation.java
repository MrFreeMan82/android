package home.tetris;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;
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

    private static Scene scene;
    private Handler requestHandler;
    private static Handler responseHandler;
    private static BarDeleteListener barDeleteListener;
    private static ConcurrentMap<Block[][], Integer> concurrentMap = new ConcurrentHashMap<>();

    interface BarDeleteListener
    {
        void onRepaint();
        void onBarDelete(int total);
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

            //В этом классе код выполняется отдельным потоком
    private static class RequestHandler extends Handler
    {
        Runnable repaint = new Runnable() {
            @Override
            public void run() {barDeleteListener.onRepaint();}
        };

        private void deleting(Block[][] bar)
        {
            while (bar[0][0].rect.bottom != bar[0][0].rect.top)
            {
                int k = 1;
                for(int i = 0; i < bar.length; i++)
                {
                    for (Block block : bar[i])
                    {
                        block.p1 = null;
                        block.p2 = null;
                        block.rect.bottom -= 1;
                        block.updateSubRect();
                        if (block.rect.bottom == block.rect.top) block.active = false;
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
        }

        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == DELETE_BAR)
            {
                Block[][] bar = (Block[][]) msg.obj;
                final int total = concurrentMap.get(bar);
                deleting(bar);
                responseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        barDeleteListener.onBarDelete(total);
                    }
                });
            }
        }
    }

    void falling(int totalDeleted)
    {
        int bottom = Scene.HEIGHT;
        int moved = 0;
        int size = scene.getSceneList().size();
        if((totalDeleted == 0) || (size == 0) || (size == 1)) return;

        while (bottom > 0 && totalDeleted != moved)
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

    private int countBlock(int line)
    {
        int counter = 0;
        for(Tetramino tetramino: scene.getSceneList()){
            for(Block block: tetramino.getBlocks()){
                if(block.active && block.rect.bottom == line) counter++;
            }
        }
        return counter;
    }

    private int countTotal()
    {
        int result = 0;
        int bottom = Scene.HEIGHT;

        while (bottom > 0)
        {
            int count = countBlock(bottom);
            if(count == Scene.BLOCKS_PER_WIDTH) result++;

            bottom -= Tetramino.SQ_SIZE;
        }
        return result;
    }

    void deleteFullLines()
    {
        int bottom = Scene.HEIGHT;
        int line = 0;
        int total = countTotal();
        if(total == 0) return;
        scene.getSound().play(Sound.DELETE_LINE);
        Block[][] bar = new Block[total][Scene.BLOCKS_PER_WIDTH];

        while (bottom > 0)
        {
            int blockCount = countBlock(bottom);
            if(blockCount == Scene.BLOCKS_PER_WIDTH)
            {
                int index = 0;
                for(Tetramino tetramino : scene.getSceneList())
                {
                    for(Block block : tetramino.getBlocks())
                    {
                        if(block.active && block.rect.bottom == bottom)
                        {
                            bar[line][index] = block;
                            index++;
                        }
                    }
                }

                Arrays.sort(bar[line], new CustomComparator());
                line++;
            }
            else if(blockCount == 0) break;

            bottom -= Tetramino.SQ_SIZE;
        }
        concurrentMap.put(bar, total);
        requestHandler.obtainMessage(DELETE_BAR, bar).sendToTarget();
    }
}
