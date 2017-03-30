package home.tetris;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


/**
 * Created by Дима on 20.02.2017.
 *
 * Осуществляется поиск и удаление полных линий.
 *
 */

class DeleteAnimation implements Callable<Integer>
{
    private static final String TAG = "DeleteAnimation";

    private Scene scene;
    private Block[][] lines;   // Массив линий которые должны быть удалены

    DeleteAnimation(Scene aScene){scene = aScene;}

    @Override public Integer call()
    {
        decreaseLines(lines);
        hasDeletedMino(lines);
        falling(lines.length);
        return lines.length;
    }

    /**
     *  Уменьшает высоту блоков снизу вверх
     *  @param lines массив линий которые должны быть удалены
     *
     */
    private void decreaseLines(Block[][] lines)
    {
        while (lines[0][0].rect.bottom != lines[0][0].rect.top)
        {
            int k = 1;
            for (Block[] line : lines)
            {
                for (Block block : line)
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
    }


    /**
     *  Ищет удаленные тетрамино и ведет их статистику
     *  @param lines массив линий которые должны быть удалены
     *
     */
    private void hasDeletedMino(Block[][] lines)
    {
        ArrayList<Tetramino> list = new ArrayList<>();
        for (Block[] line : lines)
        {
            for (Block block : line)
            {
                if(list.contains(block.tetramino)) continue;
                int counter = 0;
                for(Block minoBlock: block.tetramino.getBlocks()) if(!minoBlock.visible) counter++;
                if(counter == Tetramino.BLOCKS_PER_MINO)
                {
                    Statistic.deleteMino(block.tetramino);
                    list.add(block.tetramino);
                }
            }
        }
    }

    /**
     * Опускает блоки, которые находятся над удаленными линиями, вниз
     * на величину, соответствующую количеству удаленных линий * Block.SIZE
     * @param totalDeleted количество удаленных линий.
     */
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
                                field[x][k].visible) field[x][k].moveDown(Block.SIZE);
                    }
                }
                movedLines++;
            }
            y--;
        }
    }

    /**
     * Ведет подсчет видимых блоков на одной линии
     * @param y координата поля field[][]
     * @return  колличесво видимых блоков на одной линии y
     */
    private int countBlock(int y)
    {
       int counter = 0;
       Block[][] field = scene.getField();
       for(int x = 0; x < Scene.BLOCKS_PER_WIDTH; x++)
            if(field[x][y] != null && field[x][y].visible) counter++;

       return counter;
    }

    /**
     * Ведет подсчет полных линий которые должны будут удалены.
     * @return  колличесво полных линий
     */
    private int countTotal()
    {
        int count;
        int result = 0;
        int y = Scene.BLOCKS_PER_HEIGHT - 1;

        do {
            count = countBlock(y);
            if(count == Scene.BLOCKS_PER_WIDTH) result++;
            y--;
        } while (y >= 0 && count > 0);

        return result;
    }

    /**
     * Осуществляет поиск и удалений полных линий.
     * Если есть полные линии, тогда метод формирует из них массив линий lines.
     * Далее этот массив используется потоком для удаления
     *@return  ссылку(ярлык) по которому в будущем можно получить результат.
     *          Ссылка содержит в себе информацию о колличестве удаленных линий
     *          Либо null если полных линий нет.
     */
    Future<Integer> deleteFullLines()
    {
        int line = 0;
        int total = countTotal();
        if(total == 0) return null;
        Sound.play(Sound.DELETE_LINE);
        Block[][] field = scene.getField();
        lines = new Block[total][Scene.BLOCKS_PER_WIDTH];
        int y = Scene.BLOCKS_PER_HEIGHT - 1;

        while (y >= 0 && line != total)
        {
            int blockCount = countBlock(y);
            if(blockCount == Scene.BLOCKS_PER_WIDTH)
            {
                for(int x = 0; x < Scene.BLOCKS_PER_WIDTH; x++) lines[line][x] = field[x][y];
                line++;
            }
            y--;
        }
        return MainActivity.submit(this);
    }
}