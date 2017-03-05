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
    private static Scene scene;
    private static BarDeleteListener barDeleteListener;

    DeleteAnimation(Scene aScene)
    {
        scene = aScene;
    }

    void setBarDeleteListener(BarDeleteListener listener){barDeleteListener = listener;}

    private class DeleteTask extends AsyncTask<Block[][], Void, Integer>
    {
        @Override
        protected Integer doInBackground(Block[][]... params) {
            Block[][] bar = params[0];
            while (bar[0][0].rect.bottom != bar[0][0].rect.top) {
                int k = 1;
                for (Block[] column : bar) {
                    for (Block block : column) {
                        block.p1 = null;
                        block.p2 = null;
                        block.rect.bottom -= 1;
                        block.updateSubRect();
                        if (block.rect.bottom == block.rect.top) block.active = false;
                        if ((k == Scene.BLOCKS_PER_WIDTH) && (block.rect.bottom % 2 == 0)) {
                            publishProgress();
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
            return bar.length;
        }

        @Override
        public void onProgressUpdate(Void... params) {
            scene.invalidate();
        }

        @Override
        public void onPostExecute(Integer totalDeleted) {
            falling(totalDeleted);
            barDeleteListener.onDeleteComplete(totalDeleted);
        }
    }

    private void falling(int totalDeleted)
    {
        int bottom = Scene.getHEIGHT();
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
        int count;
        int result = 0;
        int bottom = Scene.getHEIGHT();

        do {
            count = countBlock(bottom);
            if(count == Scene.BLOCKS_PER_WIDTH) result++;
            bottom -= Tetramino.SQ_SIZE;
        }
        while(bottom > 0 && count > 0);

        return result;
    }

    void deleteFullLines()
    {
        int bottom = Scene.getHEIGHT();
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
                line++;
            }
            else if(blockCount == 0) break;

            bottom -= Tetramino.SQ_SIZE;
        }
        new DeleteTask().execute(bar);
    }
}
