package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Дима on 10.03.2017.
 *
 */

interface StatisticInterface{
    int[] getStatistic();
    void clearStatistic();
}

class Statistic extends View
{
    private Paint paint;
    private Tetramino[] tetraminos;
    private final Rect textBounds;
    private ArrayList<StatisticInterface> stat = new ArrayList<>(2);

    Statistic(Context context)
    {
        super(context);
        paint = new Paint();

        textBounds = new Rect();

        tetraminos = new Tetramino[7];
        int left, top;
        int space = Block.SIZE / 2;

        top = space;
        left = Scene.WIDTH / 4;

        tetraminos[0] = new Line(left, top, 1, Color.RED);
        top += space + tetraminos[0].getBlockPerHeight() * Block.SIZE;

        tetraminos[1] = new Square(left, top, Color.BLUE);
        top += space + tetraminos[1].getBlockPerHeight() * Block.SIZE;

        tetraminos[2] = new LLike(left, top, 2, Color.GREEN);
        top += space + tetraminos[2].getBlockPerHeight() * Block.SIZE;

        tetraminos[3] = new LRLike(left, top, 2, Color.CYAN);
        top += space + tetraminos[3].getBlockPerHeight() * Block.SIZE;

        tetraminos[4] = new TLike(left, top, 1, Color.YELLOW);
        top += space + tetraminos[4].getBlockPerHeight() * Block.SIZE;

        tetraminos[5] = new ZLike(left, top, 1, Color.MAGENTA);
        top += space + tetraminos[5].getBlockPerHeight() * Block.SIZE;

        tetraminos[6] = new RZLike(left, top, 1, Color.GRAY);
    }

    private void drawText(Canvas canvas, Tetramino tetramino, int blockPerWidth, int blockPerHeight, String text)
    {
        paint.setColor(0xFFD2E1DE);
        float x = tetramino.getMinLeft() + blockPerWidth * Block.SIZE + 10;
        float y = tetramino.getMinTop() + blockPerHeight * Block.SIZE / 2f;
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x, y - textBounds.exactCenterY(), paint);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        int[] newMinos = stat.get(0).getStatistic();
        int[] deletedMinos = stat.get(1).getStatistic();

        canvas.drawARGB(255, 0, 0, 0);

        paint.setTextSize(Block.SIZE);

        for(int i = 0; i < tetraminos.length; i++)
        {
            tetraminos[i].draw(canvas, paint);
            String text = "- " + newMinos[i] + ':' + deletedMinos[i];
            drawText(canvas, tetraminos[i], 4, tetraminos[i].getBlockPerHeight(), text);
        }
    }

    void registerNewMinoStatistic(StatisticInterface statisticInterface)
    {
        stat.add(0, statisticInterface);
    }

    void registerDeletedMinoStatistic(StatisticInterface statisticInterface)
    {
        stat.add(1, statisticInterface);
    }

    void clearStatistic()
    {
        stat.get(0).clearStatistic();
        stat.get(1).clearStatistic();
    }
}
