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
    private Tetramino[] tetraminos = new Tetramino[Tetramino.MAX_TETRAMINOS];
    private final Rect textBounds;

    private static int[]
            created = new int[Tetramino.MAX_TETRAMINOS],
                deleted = new int[Tetramino.MAX_TETRAMINOS];

    Statistic(Context context)
    {
        super(context);
        paint = new Paint();

        textBounds = new Rect();

        int left, top;
        int space = Block.SIZE / 2;

        top = space;
        left = Scene.WIDTH / 4;

        tetraminos[0] = new Line(left, top, LinePosition.FIRST, Color.RED);
        top += space + tetraminos[0].getBlockPerHeight() * Block.SIZE;

        tetraminos[1] = new Square(left, top, Color.BLUE);
        top += space + tetraminos[1].getBlockPerHeight() * Block.SIZE;

        tetraminos[2] = new LLike(left, top, LPosition.SECOND, Color.GREEN);
        top += space + tetraminos[2].getBlockPerHeight() * Block.SIZE;

        tetraminos[3] = new LRLike(left, top, LPosition.SECOND, Color.CYAN);
        top += space + tetraminos[3].getBlockPerHeight() * Block.SIZE;

        tetraminos[4] = new TLike(left, top, TPosition.FIRST, Color.YELLOW);
        top += space + tetraminos[4].getBlockPerHeight() * Block.SIZE;

        tetraminos[5] = new ZLike(left, top, ZPosition.FIRST, Color.MAGENTA);
        top += space + tetraminos[5].getBlockPerHeight() * Block.SIZE;

        tetraminos[6] = new RZLike(left, top, ZPosition.FIRST, Color.GRAY);
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
        canvas.drawARGB(255, 0, 0, 0);

        paint.setTextSize(Block.SIZE);

        for(int i = 0; i < tetraminos.length; i++)
        {
            tetraminos[i].draw(canvas, paint);
            String text = "- " + created[i] + ':' + deleted[i];
            drawText(canvas, tetraminos[i], 4, tetraminos[i].getBlockPerHeight(), text);
        }
    }

    static void minoCreated(Tetramino tetramino)
    {
        if(tetramino == null) return;

        if(tetramino instanceof Line) created[0]++;
        else if (tetramino instanceof Square) created[1]++;
        else if (tetramino instanceof LLike) created[2]++;
        else if (tetramino instanceof RZLike) created[3]++;
        else if (tetramino instanceof TLike) created[4]++;
        else if (tetramino instanceof ZLike) created[5]++;
        else created[6]++;
    }
    static void minoDeleted(Tetramino tetramino)
    {
        if(tetramino == null) return;

        if(tetramino instanceof Line) deleted[0]++;
        else if (tetramino instanceof Square) deleted[1]++;
        else if (tetramino instanceof LLike) deleted[2]++;
        else if (tetramino instanceof RZLike) deleted[3]++;
        else if (tetramino instanceof TLike) deleted[4]++;
        else if (tetramino instanceof ZLike) deleted[5]++;
        else deleted[6]++;
    }

    static void clearStatistic()
    {
        created = new int[Tetramino.MAX_TETRAMINOS];
        deleted = new int[Tetramino.MAX_TETRAMINOS];
    }
}
