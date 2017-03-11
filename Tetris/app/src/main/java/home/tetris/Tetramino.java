package home.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Arrays;

import static home.tetris.Block.SIZE;

/**
 * Created by Дима on 23.01.2017.
 * Класс служит для описания возможных видов Тетрамино
 *
 */

interface MinoFactory{
    Tetramino next();
}

abstract class Tetramino
{
    private Block[] blocks = new Block[4];
    private int color;

    private static class MinoGenerator
    {
        private static final ArrayList<MinoFactory> types = new ArrayList<>
        (
            Arrays.asList(
               new Line.Factory(), new Square.Factory(), new LLike.Factory(),
                    new LRLike.Factory(), new TLike.Factory(), new ZLike.Factory(), new RZLike.Factory()
        ));

        static int[] statistic = new int[types.size()];

        static Tetramino next()
        {
            int type = (int) (Math.random() * types.size());
            statistic[type]++;
            return types.get(type).next();
        }
    }

    int getMinLeft() {
        int r = Integer.MAX_VALUE;
        for(Block block: blocks)
            if(block.visible && block.rect.left < r) r = block.rect.left;

        return r;
    }

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

    static Tetramino getNext(){return MinoGenerator.next();}

    static void clearStatistic(){
        for(int i = 0; i < MinoGenerator.statistic.length; i++) MinoGenerator.statistic[i] = 0;
    }
    static int[] getStatistic(){return MinoGenerator.statistic;}

    static void loadTemplate(Tetramino tetramino, byte[][] template, int left, int top)
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
        return Color.argb((int) (Math.random() * k),
                (int) (Math.random() * k), (int) (Math.random() * k), (int) (Math.random() * k));
    }

    abstract Tetramino rotate();
    abstract int getBlockPerHeight();
}

class Line extends Tetramino{
    private static final int MAX_POSITIONS = 2;
    private static final byte[][] h_line = {{1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] v_line = {{1,0,0,0}, {1,0,0,0}, {1,0,0,0}, {1,0,0,0}};

    private int position;
    private int blockPerHeight;

    static class Factory implements MinoFactory
    {
        public Line next()
        {
            int position = (int) (Math.random() * MAX_POSITIONS + 1);
            int blockPerHeight = position == 1 ? 1: 4;
            int blockPerWidth = position == 1 ? 4: 1;
            int top = -blockPerHeight * SIZE;
            int left = (int)(Math.random() * (Scene.WIDTH - blockPerWidth * SIZE));
            return new Line(left, top, position, randomColor());
        }
    }

    Line(int left, int top, int aPosition, int color)
    {
        position = aPosition;

        if((position == 1) || (position > MAX_POSITIONS) || (position < 1))
        {
            position = 1;
            blockPerHeight = 1;
            Tetramino.loadTemplate(this, h_line, left, top);
        }
        else if (position == 2)
        {
            blockPerHeight = 4;
            Tetramino.loadTemplate(this, v_line, left, top);
        }

        setColor(color);
    }

    int getBlockPerHeight(){return blockPerHeight;}

    Line rotate(){return new Line(getMinLeft(), getMinTop(), position+1, getColor());}
}

class Square extends Tetramino{
    private static final byte[][] square = {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    private int blockPerHeight;

    static class Factory implements MinoFactory
    {
        public Square next()
        {
            int top = -2 * SIZE;
            int left = (int)(Math.random() * (Scene.WIDTH - 2 * SIZE));
            return new Square(left, top, randomColor());
        }
    }

    Square(int left, int top, int color)
    {
        blockPerHeight = 2;
        Tetramino.loadTemplate(this, square, left, top);
        setColor(color);
    }

    int getBlockPerHeight(){return blockPerHeight;}
    Square rotate(){return null;}
}

class LLike extends Tetramino{
    private static final int MAX_POSITIONS = 4;
    private static final byte[][] l0 = {{1,0,0,0}, {1,0,0,0}, {1,1,0,0}, {0,0,0,0}};
    private static final byte[][] l90 = {{1,1,1,0}, {1,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] l180 = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};
    private static final byte[][] l270 = {{0,0,1,0},{1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    private int position;
    private int blockPerHeight;

    static class Factory implements MinoFactory
    {
        public LLike next()
        {
            int position = (int) (Math.random() * MAX_POSITIONS + 1);
            int blockPerHeight = (position == 1) || (position == 3) ? 3 : 2;
            int blockPerWidth = (position == 1) || (position == 3) ? 2 : 3;
            int top = -blockPerHeight * SIZE;
            int left = (int) (Math.random() * (Scene.WIDTH - blockPerWidth * SIZE));
            return new LLike(left, top, position, randomColor());
        }
    }

    LLike(int left, int top, int aPosition, int color)
    {
        position = aPosition;
        if((position == 1) || (position > MAX_POSITIONS) || (position < 1))
        {
            position = 1;
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, l0, left, top);
        }
        else if(position == 2)
        {
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, l90, left, top);
        }
        else if(position == 3)
        {
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, l180, left, top);
        }
        else if(position == 4)
        {
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, l270, left, top);
        }
        setColor(color);
    }

    int getBlockPerHeight(){return blockPerHeight;}
    LLike rotate(){return new LLike(getMinLeft(), getMinTop(), position+1, getColor());}
}

class LRLike extends Tetramino{
    private static final int MAX_POSITIONS = 4;
    private static final byte[][] lr0 = {{0,1,0,0}, {0,1,0,0}, {1,1,0,0}, {0,0,0,0}};
    private static final byte[][] lr90 = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] lr180 = {{1,1,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}};
    private static final byte[][] lr270 = {{1,1,1,0}, {0,0,1,0}, {0,0,0,0}, {0,0,0,0}};

    private int position;
    private int blockPerHeight;

    static class Factory implements MinoFactory
    {
        public LRLike next()
        {
            int position = (int) (Math.random() * MAX_POSITIONS + 1);
            int blockPerHeight = (position == 1) || (position == 3) ? 3 : 2;
            int blockPerWidth = (position == 1) || (position == 3) ? 2 : 3;
            int top = -blockPerHeight * SIZE;
            int left = (int) (Math.random() * (Scene.WIDTH - blockPerWidth * SIZE));
            return new LRLike(left, top, position, randomColor());
        }
    }

    LRLike(int left, int top, int aPosition, int color)
    {
        position = aPosition;
        if((position == 1) || (position > MAX_POSITIONS) || (position < 1))
        {
            position = 1;
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, lr0, left, top);
        }
        else if(position == 2)
        {
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, lr90, left, top);
        }
        else if(position == 3)
        {
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, lr180, left, top);
        }
        else if(position == 4)
        {
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, lr270, left, top);
        }
        setColor(color);
    }

    int getBlockPerHeight(){return blockPerHeight;}
    LRLike rotate(){return new LRLike(getMinLeft(), getMinTop(), position+1, getColor());}
}

class TLike extends Tetramino{
    private static final int MAX_POSITIONS = 4;
    private static final byte[][] t0 = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] t90 = {{1,0,0,0}, {1,1,0,0}, {1,0,0,0}, {0,0,0,0}};
    private static final byte[][] t180 = {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] t270 = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    private int position;
    private int blockPerHeight;

    static class Factory implements MinoFactory
    {

        public TLike next()
        {
            int position = (int) (Math.random() * MAX_POSITIONS + 1);
            int blockPerHeight = (position == 1) || (position == 3) ? 2 : 3;
            int blockPerWidth = (position == 1) || (position == 3) ? 3 : 2;
            int top = -blockPerHeight * SIZE;
            int left = (int) (Math.random() * (Scene.WIDTH - blockPerWidth * SIZE));
            return new TLike(left, top, position, randomColor());
        }
    }

    TLike(int left, int top, int aPosition, int color)
    {
        position = aPosition;
        if((position == 1) || (position > 4) || (position < 1))
        {
            position = 1;
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, t0, left, top);
        }
        else if(position == 2)
        {
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, t90, left, top);
        }
        else if(position == 3)
        {
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, t180, left, top);
        }
        else if(position == 4)
        {
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, t270, left, top);
        }
        setColor(color);
    }

    int getBlockPerHeight(){return blockPerHeight;}
    TLike rotate(){return new TLike(getMinLeft(), getMinTop(), position+1, getColor());}
}

class ZLike extends Tetramino{
    private static final int MAX_POSITIONS = 2;
    private static final byte[][] z0 = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] z180 = {{0,1,0,0}, {1,1,0,0},{1,0,0,0}, {0,0,0,0}};

    private int position;
    private int blockPerHeight;

    static class Factory implements MinoFactory
    {
        public ZLike next()
        {
            int position = (int) (Math.random() * MAX_POSITIONS + 1);
            int blockPerHeight = position == 1 ? 2: 3;
            int blockPerWidth = position == 1 ? 3: 2;
            int top = -blockPerHeight * SIZE;
            int left = (int)(Math.random() * (Scene.WIDTH - blockPerWidth * SIZE));
            return new ZLike(left, top, position, randomColor());
        }
    }

    ZLike(int left, int top, int aPosition, int color)
    {
        position = aPosition;

        if((position == 1) || (position > MAX_POSITIONS) || (position < 1))
        {
            position = 1;
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, z0, left, top);
        }
        else if (position == 2)
        {
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, z180, left, top);
        }
        setColor(color);
    }

    int getBlockPerHeight(){return blockPerHeight;}
    ZLike rotate(){return new ZLike(getMinLeft(), getMinTop(), position+1, getColor());}
}

class RZLike extends Tetramino{
    private static final int MAX_POSITIONS = 2;
    private static final byte[][] rz0 = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] rz180 = {{1,0,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    private int position;
    private int blockPerHeight;

    static class Factory implements MinoFactory
    {
        public RZLike next()
        {
            int position = (int) (Math.random() * MAX_POSITIONS + 1);
            int blockPerHeight = position == 1 ? 2: 3;
            int blockPerWidth = position == 1 ? 3: 2;
            int top = -blockPerHeight * SIZE;
            int left = (int)(Math.random() * (Scene.WIDTH - blockPerWidth * SIZE));
            return new RZLike(left, top, position, randomColor());
        }
    }

    RZLike(int left, int top, int aPosition, int color)
    {
        position = aPosition;

        if((position == 1) || (position > MAX_POSITIONS) || (position < 1))
        {
            position = 1;
            blockPerHeight = 2;
            Tetramino.loadTemplate(this, rz0, left, top);
        }
        else if (position == 2)
        {
            blockPerHeight = 3;
            Tetramino.loadTemplate(this, rz180, left, top);
        }
        setColor(color);
    }

    int getBlockPerHeight(){return blockPerHeight;}
    RZLike rotate(){return new RZLike(getMinLeft(), getMinTop(), position+1, getColor());}
}