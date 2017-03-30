package home.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static home.tetris.Block.SIZE;

/**
 * Created by Дима on 23.01.2017.
 * Класс служит для описания возможных видов Тетрамино
 *
 */

abstract class Tetramino
{
    static final List<Class<? extends Tetramino>> classes = Collections.unmodifiableList
    (
      Arrays.asList(
           Line.class, Square.class, LLike.class,
              JLike.class, TLike.class, ZLike.class, SLike.class
    ));

    static final int BLOCKS_PER_MINO = 4;
    static final Random random = new Random();

    private Block[] blocks = new Block[BLOCKS_PER_MINO];
    private int color;

    /**
     *@return  Возвращает левую координату прямоугольника в который вписана фигура
     */
    int getMinLeft() {
        int r = Integer.MAX_VALUE;
        for(Block block: blocks)
            if(block.visible && block.rect.left < r) r = block.rect.left;

        return r;
    }

    /**
     *@return  Возвращает верхнюю координату прямоугольника в который вписана фигура
     */
    int getMinTop(){
        int r = Integer.MAX_VALUE;
        for(Block block: blocks)
            if(block.visible && block.rect.top < r) r = block.rect.top;

        return r;
    }

    void draw(Canvas canvas, Paint paint)
    {
        for(Block block: blocks) block.draw(canvas, paint);
    }
    void moveDown()
    {
        for(Block block: blocks) block.moveDown(1);
    }
    void moveLeft()
    {
        for(Block block: blocks) block.moveLeft();
    }
    void moveRight()
    {
        for(Block block: blocks) block.moveRight();
    }

    int getColor(){return color;}
    void setColor(int aColor){color = aColor;}
    Block[] getBlocks(){return blocks;}

    /**
     * Генерирует новую случайную фигуру.
     * @throws ClassCastException если в списке найден класс который не используется в таблице.
     */

    static Tetramino newRandom() throws  ClassCastException
    {
        Class<? extends Tetramino> classOf =  classes.get(random.nextInt(classes.size()));

        if(classOf == Line.class) return Line.newRandom();
        else if(classOf == Square.class) return Square.newRandom();
        else if(classOf == LLike.class) return LLike.newRandom();
        else if(classOf == JLike.class) return JLike.newRandom();
        else if(classOf == TLike.class) return TLike.newRandom();
        else if(classOf == ZLike.class) return ZLike.newRandom();
        else if(classOf == SLike.class) return SLike.newRandom();
        else throw new ClassCastException("Unknown Class.");
    }

    /**
     * По шаблону формирует расположение блоков
     * @param tetramino Фигура в которой настраиваем расположение блоков
     * @param template  Шаблон
     * @param left  левая координата
     * @param top  верхняя координата
     * left top координаты левой верхней точки начиная с которой будут формироватся блоки.
     */
    static void makeByTemplate(Tetramino tetramino, byte[][] template, int left, int top)
    {
        left = (left >= SIZE)? (left / SIZE) * SIZE: 0;
        int k = 0; int oldLeft = left;
        for(byte[] column : template)
        {
            left = oldLeft;
            for(byte value : column)
            {
                if(value == 1)
                {
                    tetramino.blocks[k] = new Block(left, top, left + SIZE, top + SIZE);
                    tetramino.blocks[k].tetramino = tetramino;
                    k++;
                }
                left += SIZE;
            }
            top += SIZE;
        }
    }

    static int randomColor() {
        int k = Integer.MAX_VALUE;
        return Color.argb(random.nextInt(k), random.nextInt(k), random.nextInt(k), random.nextInt(k));
    }

    abstract Tetramino rotate();
    abstract int getBlockPerHeight();
}

/**
 *Перечисляемый тип возможных позиций фигуры линия
 * В скобках указаны колличество блоков по ширине и высоте.
 * Например: FIRST(4,1) имеет 4 блока по ширине и 1 по высоте.  Она горизонтальная.
 */
enum LinePosition{
    FIRST(4,1), SECOND(1,4);

    final int blocksPerWidth;
    final int blocksPerHeight;

    LinePosition(int aBlocksPerWidth, int aBlocksPerHeight){
        blocksPerWidth = aBlocksPerWidth;
        blocksPerHeight = aBlocksPerHeight;
    }

    static LinePosition next(LinePosition position)
        {return position == LinePosition.FIRST ? LinePosition.SECOND: LinePosition.FIRST;}
}

class Line extends Tetramino{
    private static final byte[][] h_line = {{1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] v_line = {{1,0,0,0}, {1,0,0,0}, {1,0,0,0}, {1,0,0,0}};

    private LinePosition position;

    Line(int left, int top, LinePosition aPosition, int color)
    {
        position = aPosition;
        switch (aPosition){
            case FIRST: Tetramino.makeByTemplate(this, h_line, left, top); break;
            case SECOND: Tetramino.makeByTemplate(this, v_line, left, top);
        }
        setColor(color);
    }

    /**
     * Фабричный метод для генерации новых вигур линия.
     * //@see Tetramino.Type
     */
    static Line newRandom()
    {
        LinePosition position = LinePosition.values()[random.nextInt(LinePosition.values().length)];
        int top = -position.blocksPerHeight * SIZE;
        int left = random.nextInt(Scene.WIDTH - position.blocksPerWidth * SIZE);
        return new Line(left, top, position, randomColor());
    }

    int getBlockPerHeight(){return position.blocksPerHeight;}
    Line rotate(){return new Line(getMinLeft(), getMinTop(), LinePosition.next(position), getColor());}
}

class Square extends Tetramino{
    private static final int BLOCK_PER_WIDTH = 2;
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] square = {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    Square(int left, int top, int color)
    {
        Tetramino.makeByTemplate(this, square, left, top);
        setColor(color);
    }

    static Square newRandom()
    {
        int top = -BLOCK_PER_HEIGHT * SIZE;
        int left = random.nextInt(Scene.WIDTH - BLOCK_PER_WIDTH * SIZE);
        return new Square(left, top, randomColor());
    }
    int getBlockPerHeight(){return BLOCK_PER_HEIGHT;}
    Square rotate(){return null;}
}

enum LPosition{
    FIRST(2,3), SECOND(3,2), THIRD(2,3), FORTH(3,2);

    final int blocksPerWidth;
    final int blocksPerHeight;

    LPosition(int aBlocksPerWidth, int aBlocksPerHeight){
        blocksPerWidth = aBlocksPerWidth;
        blocksPerHeight = aBlocksPerHeight;
    }

    static LPosition next(LPosition position)
    {
        switch (position){
            case FIRST: return LPosition.SECOND;
            case SECOND: return LPosition.THIRD;
            case THIRD: return LPosition.FORTH;
            default: return LPosition.FIRST;
        }
    }
}

class LLike extends Tetramino{
    private static final byte[][] l0 = {{1,0,0,0}, {1,0,0,0}, {1,1,0,0}, {0,0,0,0}};
    private static final byte[][] l90 = {{1,1,1,0}, {1,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] l180 = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};
    private static final byte[][] l270 = {{0,0,1,0},{1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    private LPosition position;

    LLike(int left, int top, LPosition aPosition, int color)
    {
        position = aPosition;
        switch (position){
            case FIRST: Tetramino.makeByTemplate(this, l0, left, top); break;
            case SECOND: Tetramino.makeByTemplate(this, l90, left, top); break;
            case THIRD: Tetramino.makeByTemplate(this, l180, left, top); break;
            case FORTH: Tetramino.makeByTemplate(this, l270, left, top);
        }
        setColor(color);
    }

    static LLike newRandom()
    {
        LPosition position = LPosition.values()[random.nextInt(LPosition.values().length)];
        int top = -position.blocksPerHeight * SIZE;
        int left = random.nextInt(Scene.WIDTH - position.blocksPerWidth * SIZE);
        return new LLike(left, top, position, randomColor());
    }

    int getBlockPerHeight(){return position.blocksPerHeight;}
    LLike rotate(){return new LLike(getMinLeft(), getMinTop(), LPosition.next(position), getColor());}
}

class JLike extends Tetramino{
    private static final byte[][] j0 = {{0,1,0,0}, {0,1,0,0}, {1,1,0,0}, {0,0,0,0}};
    private static final byte[][] j90 = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] j180 = {{1,1,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}};
    private static final byte[][] j270 = {{1,1,1,0}, {0,0,1,0}, {0,0,0,0}, {0,0,0,0}};

    private LPosition position;

    JLike(int left, int top, LPosition aPosition, int color)
    {
        position = aPosition;
        switch (position){
            case FIRST: Tetramino.makeByTemplate(this, j0, left, top); break;
            case SECOND: Tetramino.makeByTemplate(this, j90, left, top); break;
            case THIRD: Tetramino.makeByTemplate(this, j180, left, top); break;
            case FORTH: Tetramino.makeByTemplate(this, j270, left, top);
        }
        setColor(color);
    }

    static JLike newRandom()
    {
        LPosition position = LPosition.values()[random.nextInt(LPosition.values().length)];
        int top = -position.blocksPerHeight * SIZE;
        int left = random.nextInt(Scene.WIDTH - position.blocksPerWidth * SIZE);
        return new JLike(left, top, position, randomColor());
    }

    int getBlockPerHeight(){return position.blocksPerHeight;}
    JLike rotate(){return new JLike(getMinLeft(), getMinTop(), LPosition.next(position), getColor());}
}

enum TPosition{
    FIRST(3,2), SECOND(2,3), THIRD(3,2), FORTH(2,3);

    final int blocksPerWidth;
    final int blocksPerHeight;

    TPosition(int aBlocksPerWidth, int aBlocksPerHeight){
        blocksPerWidth = aBlocksPerWidth;
        blocksPerHeight = aBlocksPerHeight;
    }

    static TPosition next(TPosition position)
    {
        switch (position){
            case FIRST: return TPosition.SECOND;
            case SECOND: return TPosition.THIRD;
            case THIRD: return TPosition.FORTH;
            default: return TPosition.FIRST;
        }
    }
}

class TLike extends Tetramino{
    private static final byte[][] t0 = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] t90 = {{1,0,0,0}, {1,1,0,0}, {1,0,0,0}, {0,0,0,0}};
    private static final byte[][] t180 = {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] t270 = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    private TPosition position;

    TLike(int left, int top, TPosition aPosition, int color)
    {
        position = aPosition;
        switch (position){
            case FIRST: Tetramino.makeByTemplate(this, t0, left, top); break;
            case SECOND: Tetramino.makeByTemplate(this, t90, left, top); break;
            case THIRD: Tetramino.makeByTemplate(this, t180, left, top); break;
            case FORTH: Tetramino.makeByTemplate(this, t270, left, top);
        }
        setColor(color);
    }

    static TLike newRandom()
    {
        TPosition position = TPosition.values()[random.nextInt(TPosition.values().length)];
        int top = -position.blocksPerHeight * SIZE;
        int left = random.nextInt(Scene.WIDTH - position.blocksPerWidth * SIZE);
        return new TLike(left, top, position, randomColor());
    }

    int getBlockPerHeight(){return position.blocksPerHeight;}
    TLike rotate(){return new TLike(getMinLeft(), getMinTop(), TPosition.next(position), getColor());}
}

enum ZPosition{
    FIRST(3,2), SECOND(2,3);

    final int blocksPerWidth;
    final int blocksPerHeight;

    ZPosition(int aBlocksPerWidth, int aBlocksPerHeight){
        blocksPerWidth = aBlocksPerWidth;
        blocksPerHeight = aBlocksPerHeight;
    }

    static ZPosition next(ZPosition position)
         {return position == ZPosition.FIRST ? ZPosition.SECOND: ZPosition.FIRST;}
}

class ZLike extends Tetramino{
    private static final byte[][] z0 = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] z180 = {{0,1,0,0}, {1,1,0,0},{1,0,0,0}, {0,0,0,0}};

    private ZPosition position;

    ZLike(int left, int top, ZPosition aPosition, int color)
    {
        position = aPosition;
        switch (aPosition){
            case FIRST: Tetramino.makeByTemplate(this, z0, left, top); break;
            case SECOND: Tetramino.makeByTemplate(this, z180, left, top);
        }
        setColor(color);
    }

    static ZLike newRandom()
    {
        ZPosition position = ZPosition.values()[random.nextInt(ZPosition.values().length)];
        int top = -position.blocksPerHeight * SIZE;
        int left = random.nextInt(Scene.WIDTH - position.blocksPerWidth * SIZE);
        return new ZLike(left, top, position, randomColor());
    }

    int getBlockPerHeight(){return position.blocksPerHeight;}
    ZLike rotate(){return new ZLike(getMinLeft(), getMinTop(), ZPosition.next(position), getColor());}
}

class SLike extends Tetramino{
    private static final byte[][] s0 = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] s180 = {{1,0,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    private ZPosition position;

    SLike(int left, int top, ZPosition aPosition, int color)
    {
        position = aPosition;
        switch (aPosition){
            case FIRST: Tetramino.makeByTemplate(this, s0, left, top); break;
            case SECOND: Tetramino.makeByTemplate(this, s180, left, top);
        }
        setColor(color);
    }

    static SLike newRandom()
    {
        ZPosition position = ZPosition.values()[random.nextInt(ZPosition.values().length)];
        int top = -position.blocksPerHeight * SIZE;
        int left = random.nextInt(Scene.WIDTH - position.blocksPerWidth * SIZE);
        return new SLike(left, top, position, randomColor());
    }

    int getBlockPerHeight(){return position.blocksPerHeight;}
    SLike rotate(){return new SLike(getMinLeft(), getMinTop(), ZPosition.next(position), getColor());}
}