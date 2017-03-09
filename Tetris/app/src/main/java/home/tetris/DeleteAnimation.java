package home.tetris;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Дима on 20.02.2017.
 *
 */

interface BarDeleteListener
{
    void onDeleteComplete(int total);
}

class DeleteAnimation
{
    private static final String TAG = "DeleteAnimation";

    private Scene scene;
    private BarDeleteListener barDeleteListener;

    DeleteAnimation(Scene aScene){scene = aScene;}

    void setBarDeleteListener(BarDeleteListener listener){barDeleteListener = listener;}

    private class DeleteTask extends AsyncTask<Block[][], Void, Integer>
    {
        @Override
        protected Integer doInBackground(Block[][]... params)
        {
            Block[][] bar = params[0];
            while (bar[0][0].rect.bottom != bar[0][0].rect.top)
            {
                int k = 1;
                for (Block[] column : bar)
                {
                    for (Block block : column)
                    {
                        block.decrease();
                        if (block.rect.bottom == block.rect.top) block.visible = false;
                        if ((k == Scene.BLOCKS_PER_WIDTH) && (block.rect.bottom % 2 == 0))
                        {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                                Log.e(TAG, "Delete Animation Error");
                            }
                        }
                        k++;
                    }
                }
            }
            return bar.length;
        }

        @Override
        public void onPostExecute(Integer totalDeleted) {
            falling(totalDeleted);
            barDeleteListener.onDeleteComplete(totalDeleted);
        }
    }

    private void falling(int totalDeleted)
    {
        int movedLines = 0;
        Block[][] field = scene.getField();
        int y = Scene.BLOCKS_PER_HEIGHT - 1;

        while(y >= 0 && totalDeleted != movedLines)
        {
            while(countBlock(y) == 0 && totalDeleted != movedLines)
            {
                for(int x = 0; x < Scene.BLOCKS_PER_WIDTH; x++)
                {
                    for(int y1 = y - 1; y1 >= 0; y1--)
                    {
                        int k = y1 + 1;
                        field[x][k] = field[x][y1];
                        if(field[x][k] != null &&
                                field[x][k].visible)
                                    field[x][k].moveDown(Block.SIZE);
                    }
                }
               movedLines++;
            }
            y--;
        }
    }

    private int countBlock(int y)
    {
       int counter = 0;
       Block[][] field = scene.getField();
       for(int x = 0; x < Scene.BLOCKS_PER_WIDTH; x++)
            if(field[x][y] != null && field[x][y].visible) counter++;

       return counter;
    }

    private int countTotal()
    {
        int count;
        int result = 0;

        for(int y = Scene.BLOCKS_PER_HEIGHT - 1; y >= 0; y--)
        {
            count = countBlock(y);
            if(count == 0) return result;
            else if(count == Scene.BLOCKS_PER_WIDTH) result++;
        }

        return result;
    }

    void deleteFullLines()
    {
        int line = 0;
        int total = countTotal();
        if(total == 0) return;
        scene.getSound().play(Sound.DELETE_LINE);
        Block[][] field = scene.getField();
        Block[][] bar = new Block[total][Scene.BLOCKS_PER_WIDTH];

        for(int y = Scene.BLOCKS_PER_HEIGHT - 1; y >= 0; y--)
        {
            int blockCount = countBlock(y);
            if(blockCount == Scene.BLOCKS_PER_WIDTH)
            {
                for(int x = 0; x < Scene.BLOCKS_PER_WIDTH; x++) bar[line][x] = field[x][y];
                line++;
                if(line == total) break;
            }
        }

        new DeleteTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bar);
    }
}
