package home.tetris;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Дима on 23.01.2017.
 * Класс служит для описания возможных видов Тетрамино
 * Цифры в именах классов обозначают угол поворота
 */

class Block
{
    static int DELTA = 5;
    boolean active = true;
    Rect rect;
    Rect subRect;
    Rect mid;
    Point p1;
    Point p2;

    Block(int left, int top, int right, int bottom){
        rect = new Rect(left, top, right, bottom);
        subRect = new Rect(rect);
        mid = new Rect(rect);
        p1 = new Point();
        p2 = new Point();
        updateSubRect();
    }

    void updateSubRect()
    {
        subRect.left = rect.left + DELTA;
        subRect.top = rect.top + DELTA;
        subRect.right = rect.right - DELTA;
        subRect.bottom = rect.bottom - DELTA;

        if(p1 != null) {
            p1.x = subRect.left + (Tetramino.SQ_SIZE / 2);
            p1.y = subRect.top + (Tetramino.SQ_SIZE / 2) + ((Tetramino.SQ_SIZE / 2) / 2);
        }

        if(p2 != null) {
            p2.x = subRect.left + (Tetramino.SQ_SIZE / 2) + ((Tetramino.SQ_SIZE / 2) / 2);
            p2.y = subRect.top + (Tetramino.SQ_SIZE / 2);
        }

        mid.left = rect.left + (DELTA / 2);
        mid.top = rect.top + (DELTA / 2);
        mid.right = rect.right - (DELTA / 2);
        mid.bottom = rect.bottom - (DELTA / 2);
    }
}


abstract class Tetramino
{
    static final int MAX_BLOCK_CNT = 4;
    static int SQ_SIZE;

    private static final String TAG = "Tetramino";

    private static final Class[] types = {LineHorizontal.class, LineVertical.class, Square.class,
            L0.class, L90.class, L180.class, L270.class, LR0.class, LR90.class, LR180.class,
            LR270.class, T0.class, T90.class, T180.class, T270.class, Z0.class,
            Z180.class, RZ0.class, RZ180.class};

    private Block[] blocks;
    private int mColor;

    int getMinLeft() {
        int r = Integer.MAX_VALUE;
        for(Block block: blocks)
            if(block.active && block.rect.left < r) r = block.rect.left;

        return r;
    }

    int getMinTop(){
        int r = Integer.MAX_VALUE;
        for(Block block: blocks)
            if(block.active && block.rect.top < r) r = block.rect.top;

        return r;
    }

    void moveDown()
    {
        for(Block block: blocks)
        {
            block.rect.top += 1;
            block.rect.bottom = block.rect.top + Tetramino.SQ_SIZE;
            block.updateSubRect();
        }
    }

    void moveLeft()
    {
        for(Block block: blocks)
        {
            block.rect.left -= SQ_SIZE;
            block.rect.right = block.rect.left + SQ_SIZE;
            block.updateSubRect();
        }
    }

    void moveRight()
    {
        for(Block block: blocks)
        {
            block.rect.left = block.rect.right;
            block.rect.right = block.rect.left + SQ_SIZE;
            block.updateSubRect();
        }
    }

    int getColor(){return mColor;}
    void setColor(int color){mColor = color;}

    Block[] getBlocks(){return blocks;}

    static Tetramino next()
    {
        try{
            int type = (int) (Math.random() * types.length);
            return (Tetramino) types[type].newInstance();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error while creating new tetramino");
        }
        return null;
    }

    static void loadTemplate(Tetramino tetramino, byte[][] template, int left, int top)
    {
        left = (left >= SQ_SIZE)? (left / SQ_SIZE) * SQ_SIZE: 0;
        int k = 0; int oldLeft = left;
        for(byte[] column : template)
        {
            left = oldLeft;
            for(byte value : column)
            {
                if(value == 1)
                {
                    tetramino.blocks[k] = new Block(left, top, left + SQ_SIZE, top + SQ_SIZE);
                    k++;
                }
                left += SQ_SIZE;
            }
            top += SQ_SIZE;
        }
    }

    static int randomColor() {
        int k = Integer.MAX_VALUE;
        return Color.argb((int) (Math.random() * k),
                (int) (Math.random() * k), (int) (Math.random() * k), (int) (Math.random() * k));
    }

    abstract Tetramino rotate();

    Tetramino(){
        blocks = new Block[MAX_BLOCK_CNT];
    }
}

class LineHorizontal extends Tetramino{
    private static final byte[][] template = {{1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    LineHorizontal(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    LineHorizontal(){
        this((int)(Math.random() * (Scene.WIDTH - (MAX_BLOCK_CNT * SQ_SIZE))), -SQ_SIZE, randomColor());
    }

    LineVertical rotate(){return new LineVertical(getMinLeft(), getMinTop(), getColor());}
}

class LineVertical extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,0,0,0}, {1,0,0,0}};

    LineVertical(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    LineVertical(){
        this((int) (Math.random() * (Scene.WIDTH - SQ_SIZE)), -(MAX_BLOCK_CNT * SQ_SIZE), randomColor());
    }

    LineHorizontal rotate(){return new LineHorizontal(getMinLeft(), getMinTop(), getColor());}
}

class Square extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    Square(){
        setColor(randomColor());
        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    Square rotate(){return null;}
}

class L0 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,1,0,0}, {0,0,0,0}};

    L0(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    L0(){
        this((int) (Math.random() *
                (Scene.WIDTH - (2 * SQ_SIZE))), -(3 * SQ_SIZE), randomColor());
    }

    L90 rotate(){return new L90(getMinLeft(), getMinTop(), getColor());}
}

class L90 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {1,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    L90(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    L90(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    L180 rotate(){return new L180(getMinLeft(), getMinTop(), getColor());}
}

class L180 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    L180(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    L180(){
        this((int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE))), -(3 * SQ_SIZE), randomColor());
    }

    L270 rotate(){return new L270(getMinLeft(), getMinTop(), getColor());}
}

class L270 extends Tetramino{
    private static final byte[][] template = {{0,0,1,0},{1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    L270(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    L270(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    L0 rotate(){return new L0(getMinLeft(), getMinTop(), getColor());}
}

class LR0 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {0,1,0,0}, {1,1,0,0}, {0,0,0,0}};

    LR0(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    LR0(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    LR90 rotate(){return new LR90(getMinLeft(), getMinTop(), getColor());}
}

class LR90 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR90(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    LR90(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    LR180 rotate(){return new LR180(getMinLeft(), getMinTop(), getColor());}
}

class LR180 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}};

    LR180(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    LR180(){
        this((int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE))), -(3 * SQ_SIZE), randomColor());
    }

    LR270 rotate(){return new LR270(getMinLeft(), getMinTop(), getColor());}
}

class LR270 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {0,0,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR270(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    LR270(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    LR0 rotate(){return new LR0(getMinLeft(), getMinTop(), getColor());}
}

class T0 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    T0(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    T0(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    T90 rotate(){return new T90(getMinLeft(), getMinTop(), getColor());}
}

class T90 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {1,0,0,0}, {0,0,0,0}};

    T90(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    T90(){
        this((int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE))), -(3 * SQ_SIZE), randomColor());
    }

    T180 rotate(){return new T180(getMinLeft(), getMinTop(), getColor());}
}

class T180 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    T180(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    T180(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    T270 rotate(){return new T270(getMinLeft(), getMinTop(), getColor());}
}

class T270 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    T270(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    T270(){
        this((int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE))), -(3 * SQ_SIZE), randomColor());
    }

    T0 rotate(){return new T0(getMinLeft(), getMinTop(), getColor());}
}

class Z0 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    Z0(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    Z0(){
        this((int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE))), -(3 * SQ_SIZE), randomColor());
    }

    Z180 rotate(){return new Z180(getMinLeft(), getMinTop(), getColor());}
}

class Z180 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0},{1,0,0,0}, {0,0,0,0}};

    Z180(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    Z180(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    Z0 rotate(){return new Z0(getMinLeft(), getMinTop(), getColor());}
}

class RZ0 extends Tetramino{
    private static final byte[][] template = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    RZ0(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    RZ0(){
        this((int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE))), -(3 * SQ_SIZE), randomColor());
    }

    RZ180 rotate(){return new RZ180(getMinLeft(), getMinTop(), getColor());}
}

class RZ180 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    RZ180(int left, int top, int color){
        setColor(color);
        Tetramino.loadTemplate(this, template, left, top);
    }

    @SuppressWarnings("unused")
    RZ180(){
        this((int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE))), -(2 * SQ_SIZE), randomColor());
    }

    RZ0 rotate(){return new RZ0(getMinLeft(), getMinTop(), getColor());}
}
