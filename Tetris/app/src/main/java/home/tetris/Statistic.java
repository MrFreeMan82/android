package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Дима on 10.03.2017.
 *
 */

class Statistic extends View
{
    private Paint paint;
    private final Rect textBounds;

    private Map<Class<? extends Tetramino>, Tetramino> tetraminos = new HashMap<>();

    private Map<Class<? extends Tetramino>, Integer>
            created = new HashMap<>(), deleted = new HashMap<>();

    private static ArrayList<Tetramino> tetraminoList = new ArrayList<>();

    Statistic(Context context)
    {
        super(context);
        paint = new Paint();
        textBounds = new Rect();

        int left, top;
        int space = Block.SIZE / 2;

        top = space;
        left = Scene.WIDTH / 4;

        for(Class<? extends Tetramino> classOf: Tetramino.classes)
        {
            if(classOf == Line.class)
                tetraminos.put(classOf, new Line(left, top, LinePosition.FIRST, Color.RED));

            else if(classOf == Square.class)
                tetraminos.put(classOf, new Square(left, top, Color.BLUE));

            else if(classOf == LLike.class)
                tetraminos.put(classOf, new LLike(left, top, LPosition.SECOND, Color.GREEN));

            else if(classOf == JLike.class)
                tetraminos.put(classOf, new JLike(left, top, LPosition.SECOND, Color.CYAN));

            else if(classOf == TLike.class)
                tetraminos.put(classOf, new TLike(left, top, TPosition.FIRST, Color.YELLOW));

            else if(classOf == ZLike.class)
                tetraminos.put(classOf, new ZLike(left, top, ZPosition.FIRST, Color.MAGENTA));

            else if(classOf == SLike.class)
                tetraminos.put(classOf, new SLike(left, top, ZPosition.FIRST, Color.GRAY));

            created.put(classOf, 0); deleted.put(classOf, 0);
            top += space + tetraminos.get(classOf).getBlockPerHeight() * Block.SIZE;
        }
        calcStatistic();
    }

    private void calcStatistic()
    {
        for(Tetramino tetramino: tetraminoList)
        {
            int value;
            int counter = 0;
            Class<? extends Tetramino> classOf = tetramino.getClass();
            value = created.get(classOf);
            created.put(classOf, ++value);
            for(Block block: tetramino.getBlocks()) if(!block.visible) counter++;
            if(counter == Tetramino.BLOCKS_PER_MINO)
            {
                value = deleted.get(classOf);
                deleted.put(classOf, ++value);
            }
        }
    }

    private void drawText(Canvas canvas, Tetramino tetramino, String text)
    {
        paint.setColor(0xFFD2E1DE);
        float x = tetramino.getMinLeft() + 4 * Block.SIZE + 10;
        float y = tetramino.getMinTop() + tetramino.getBlockPerHeight() * Block.SIZE / 2;
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x, y - textBounds.exactCenterY(), paint);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawARGB(255, 0, 0, 0);

        paint.setTextSize(Block.SIZE);

        for(Class<? extends Tetramino> classOf: Tetramino.classes)
        {
            Tetramino tetramino = tetraminos.get(classOf);
            String text =
                    String.format(Locale.getDefault(),
                            "- %d : %d", created.get(classOf), deleted.get(classOf));
            tetramino.draw(canvas, paint);
            drawText(canvas, tetramino, text);
        }
    }
    static void newMino(Tetramino tetramino) {tetraminoList.add(tetramino);}
    static void clearStatistic() {tetraminoList.clear();}
}
