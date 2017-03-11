package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Дима on 10.03.2017.
 *
 */

class Statistic extends View
{
    private Paint paint;
    private Tetramino[] tetraminos;
    private final Rect textBounds;

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

        tetraminos[0] = new LineHorizontal(left, top, Color.RED);
        top += space + tetraminos[0].blockPerHeight() * Block.SIZE;

        tetraminos[1] = new Square(left, top, Color.BLUE);
        top += space + tetraminos[1].blockPerHeight() * Block.SIZE;

        tetraminos[2] = new L90(left, top, Color.GREEN);
        top += space + tetraminos[2].blockPerHeight() * Block.SIZE;

        tetraminos[3] = new LR90(left, top, Color.CYAN);
        top += space + tetraminos[3].blockPerHeight() * Block.SIZE;

        tetraminos[4] = new T0(left, top, Color.YELLOW);
        top += space + tetraminos[4].blockPerHeight() * Block.SIZE;

        tetraminos[5] = new Z0(left, top, Color.MAGENTA);
        top += space + tetraminos[5].blockPerHeight() * Block.SIZE;

        tetraminos[6] = new RZ0(left, top, Color.GRAY);
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
        int[] stat = Tetramino.getStatistic();
        canvas.drawARGB(255, 0, 0, 0);

        paint.setTextSize(Block.SIZE);

        for(int i = 0; i < stat.length; i++)
        {
            for(Block block: tetraminos[i].getBlocks()) block.draw(canvas, paint);
            drawText(canvas, tetraminos[i], 4, tetraminos[i].blockPerHeight(), "- " + stat[i]);
        }
    }
}
