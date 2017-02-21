package home.tetris;

import android.graphics.Color;
import android.graphics.Rect;

import java.util.Comparator;


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

    Block(int left, int top, int right, int bottom){
        rect = new Rect(left, top, right, bottom);
        subRect = new Rect(rect);
        updateSubRect();
    }

    void updateSubRect()
    {
        subRect.left = rect.left + DELTA;
        subRect.top = rect.top + DELTA;
        subRect.right = rect.right - DELTA;
        subRect.bottom = rect.bottom - DELTA;
    }
}

abstract class Tetramino{

    static final int MAX_BLOCK_CNT = 4;
    static int SQ_SIZE;

    Block[] blocks;
    int mColor;

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

    Block[] getBlocks(){return blocks;}

     static void loadTemplate(Tetramino tetramino, byte[][] template, int left, int top)
    {
        left = (left >= SQ_SIZE)? (left / SQ_SIZE) * SQ_SIZE: 0;
        int k = 0; int oldLeft = left;
        for(int i = 0; i < template.length; i++)
        {
            left = oldLeft;
            for(int j = 0; j < template[i].length; j++)
            {
                if(template[i][j] == 1)
                {
                    tetramino.blocks[k] = new Block(left, top, left + SQ_SIZE, top + SQ_SIZE);
                    k++;
                }
                left += SQ_SIZE;
            }
            top += SQ_SIZE;
        }
    }

    static Tetramino intToTetramino(int value)
    {
        switch (value)
        {
            case 0: return new LineHorizontal();
            case 1: return new LineVertical();
            case 2: return new Square();
            case 3: return new L0();
            case 4: return new L90();
            case 5: return new L180();
            case 6: return new L270();
            case 7: return new LR0();
            case 8: return new LR90();
            case 9: return new LR180();
            case 10: return new LR270();
            case 11: return new T0();
            case 12: return new T90();
            case 13: return new T180();
            case 14: return new T270();
            case 15: return new Z0();
            case 16: return new Z180();
            case 17: return new RZ0();
            case 18: return new RZ180();
            default: return null;
        }
    }

    static int randomColor() {
        int k = Integer.MAX_VALUE;
        return Color.argb((int) (Math.random() * k),
                (int) (Math.random() * k), (int) (Math.random() * k), (int) (Math.random() * k));
    }

    abstract Tetramino rotate();
}

class CustomComparator implements Comparator<Block>
{
    @Override
    public int compare(Block a, Block b)
    {
        if((a == null) || (b == null)) return 0;
        return (a.rect.left > b.rect.left)? 1: -1;
    }

    @Override
    public boolean equals(Object obj){return false;}
}

class LineHorizontal extends Tetramino{
    private static final byte[][] template = {{1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    LineHorizontal(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineHorizontal(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -SQ_SIZE;
        int left = (int) (Math.random() * (Scene.WIDTH - (MAX_BLOCK_CNT * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineVertical rotate(){return new LineVertical(getMinLeft(), getMinTop(), mColor);}
}

class LineVertical extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,0,0,0}, {1,0,0,0}};

    LineVertical(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineVertical(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(MAX_BLOCK_CNT * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineHorizontal rotate(){return new LineHorizontal(getMinLeft(), getMinTop(), mColor);}
}

class Square extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    Square(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    Square rotate(){return null;}
}

class L0 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,1,0,0}, {0,0,0,0}};

    L0(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L0(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L90 rotate(){return new L90(getMinLeft(), getMinTop(), mColor);}
}

class L90 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {1,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    L90(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L90(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L180 rotate(){return new L180(getMinLeft(), getMinTop(), mColor);}
}

class L180 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    L180(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L180(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L270 rotate(){return new L270(getMinLeft(), getMinTop(), mColor);}
}

class L270 extends Tetramino{
    private static final byte[][] template = {{0,0,1,0},{1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    L270(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L270(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L0 rotate(){return new L0(getMinLeft(), getMinTop(), mColor);}
}

class LR0 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {0,1,0,0}, {1,1,0,0}, {0,0,0,0}};

    LR0(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR0(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR90 rotate(){return new LR90(getMinLeft(), getMinTop(), mColor);}
}

class LR90 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR90(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR90(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR180 rotate(){return new LR180(getMinLeft(), getMinTop(), mColor);}
}

class LR180 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}};

    LR180(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR180(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR270 rotate(){return new LR270(getMinLeft(), getMinTop(), mColor);}
}

class LR270 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {0,0,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR270(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR270(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR0 rotate(){return new LR0(getMinLeft(), getMinTop(), mColor);}
}

class T0 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    T0(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T0(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T90 rotate(){return new T90(getMinLeft(), getMinTop(), mColor);}
}

class T90 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {1,0,0,0}, {0,0,0,0}};

    T90(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T90(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T180 rotate(){return new T180(getMinLeft(), getMinTop(), mColor);}
}

class T180 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    T180(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T180(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T270 rotate(){return new T270(getMinLeft(), getMinTop(), mColor);}
}

class T270 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    T270(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T270(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T0 rotate(){return new T0(getMinLeft(), getMinTop(), mColor);}
}

class Z0 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    Z0(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z0(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z180 rotate(){return new Z180(getMinLeft(), getMinTop(), mColor);}
}

class Z180 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0},{1,0,0,0}, {0,0,0,0}};

    Z180(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z180(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z0 rotate(){return new Z0(getMinLeft(), getMinTop(), mColor);}
}

class RZ0 extends Tetramino{
    private static final byte[][] template = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    RZ0(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ0(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (2 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ180 rotate(){return new RZ180(getMinLeft(), getMinTop(), mColor);}
}

class RZ180 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    RZ180(int left, int top, int color){
        mColor = color;
        blocks = new Block[MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ180(){
        mColor = randomColor();
        blocks = new Block[MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = (int) (Math.random() * (Scene.WIDTH - (3 * SQ_SIZE)));
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ0 rotate(){return new RZ0(getMinLeft(), getMinTop(), mColor);}
}
