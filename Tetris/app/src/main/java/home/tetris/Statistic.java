package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.EnumMap;
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

    private Map<Tetramino.Type, Tetramino> tetraminos = new EnumMap<>(Tetramino.Type.class);

    private static Map<Tetramino.Type, Integer>
            created = new EnumMap<>(Tetramino.Type.class),
                deleted = new EnumMap<>(Tetramino.Type.class);

    static {clearStatistic();}

    Statistic(Context context)
    {
        super(context);
        paint = new Paint();

        textBounds = new Rect();

        int left, top;
        int space = Block.SIZE / 2;

        top = space;
        left = Scene.WIDTH / 4;

        for(Tetramino.Type type: Tetramino.Type.values())
        {
            switch (type){
                case LINE:
                    tetraminos.put(type, new Line(left, top, LinePosition.FIRST, Color.RED));
                    break;

                case SQUARE:
                    tetraminos.put(type, new Square(left, top, Color.BLUE));
                    break;

                case LLIKE:
                    tetraminos.put(type, new LLike(left, top, LPosition.SECOND, Color.GREEN));
                    break;

                case LRLIKE:
                    tetraminos.put(type, new LRLike(left, top, LPosition.SECOND, Color.CYAN));
                    break;

                case TLIKE:
                    tetraminos.put(type, new TLike(left, top, TPosition.FIRST, Color.YELLOW));
                    break;

                case ZLIKE:
                    tetraminos.put(type, new ZLike(left, top, ZPosition.FIRST, Color.MAGENTA));
                    break;

                case RZLIKE:
                    tetraminos.put(type, new RZLike(left, top, ZPosition.FIRST, Color.GRAY));
            }

            top += space + tetraminos.get(type).getBlockPerHeight() * Block.SIZE;
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

        for(Tetramino.Type type: Tetramino.Type.values())
        {
            Tetramino tetramino = tetraminos.get(type);
            Tetramino.Type minoType = tetramino.getType();
            String text =
                    String.format(Locale.getDefault(),
                            "- %d : %d", created.get(minoType), deleted.get(minoType));
            tetramino.draw(canvas, paint);
            drawText(canvas, tetramino, text);
        }
    }

    static void minoCreated(Tetramino tetramino)
    {
        if(tetramino == null) return;

        Tetramino.Type type = tetramino.getType();
        int value = created.get(type);
        created.put(type, ++value);
    }

    static void minoDeleted(Tetramino tetramino)
    {
        if(tetramino == null) return;

        Tetramino.Type type = tetramino.getType();
        int value = deleted.get(type);
        deleted.put(type, ++value);
    }

    static void clearStatistic()
    {
        for(Tetramino.Type type: Tetramino.Type.values())
        {
            created.put(type, 0);
            deleted.put(type, 0);
        }
    }
}
