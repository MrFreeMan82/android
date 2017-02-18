package home.tetris;

import android.graphics.Color;
import android.graphics.Rect;

import java.util.Random;

import static home.tetris.Globals.SQ_SIZE;

/**
 * Created by Дима on 23.01.2017.
 * Класс служит для описания возможных видов Тетрамино
 * Цифры в именах классов обозначают угол поворота
 */

abstract class Tetramino {
    Rect[] blocks;
    int mColor;

    int getMinLeft() {
        int r = Integer.MAX_VALUE;
        for(Rect block: blocks)
            if(block != null && block.left < r) r = block.left;

        return r;
    }

    int getMinTop(){
        int r = Integer.MAX_VALUE;
        for(Rect block: blocks)
            if(block != null && block.top < r) r = block.top;

        return r;
    }

    void moveDown()
    {
        for(Rect block: blocks)
        {
            if(block == null) continue;
            block.top += 1;
            block.bottom = block.top + SQ_SIZE;
        }
    }

    void moveLeft()
    {
        for(Rect block: blocks)
        {
            block.left -= SQ_SIZE;
            block.right = block.left + SQ_SIZE;
        }
    }

    void moveRight()
    {
        for(Rect block: blocks)
        {
            block.left = block.right;
            block.right = block.left + SQ_SIZE;
        }
    }

    int getColor(){return mColor;}

    Rect[] getBlocks(){return blocks;}

    void replaceBlock(Rect newBlock, Rect oldBlock)
    {
        for (int i = 0; i < blocks.length; i++)
            if(blocks[i] != null && blocks[i].equals(oldBlock)) blocks[i] = newBlock;
    }

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
                    tetramino.blocks[k] = new Rect(left, top, left + SQ_SIZE, top + SQ_SIZE);
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
}

class LineHorizontal extends Tetramino{
    private static final byte[][] template = {{1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    LineHorizontal(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineHorizontal(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -SQ_SIZE;
        int left = random.nextInt(Globals.WIDTH - (Globals.MAX_BLOCK_CNT * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineVertical rotate(){return new LineVertical(getMinLeft(), getMinTop(), mColor);}
}

class LineVertical extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,0,0,0}, {1,0,0,0}};

    LineVertical(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineVertical(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(Globals.MAX_BLOCK_CNT * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - SQ_SIZE);
        Tetramino.loadTemplate(this, template, left, top);
    }

    LineHorizontal rotate(){return new LineHorizontal(getMinLeft(), getMinTop(), mColor);}
}

class Square extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    Square(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    Square rotate(){return null;}
}

class L0 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,1,0,0}, {0,0,0,0}};

    L0(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L0(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L90 rotate(){return new L90(getMinLeft(), getMinTop(), mColor);}
}

class L90 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {1,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    L90(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L90(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L180 rotate(){return new L180(getMinLeft(), getMinTop(), mColor);}
}

class L180 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    L180(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L180(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L270 rotate(){return new L270(getMinLeft(), getMinTop(), mColor);}
}

class L270 extends Tetramino{
    private static final byte[][] template = {{0,0,1,0},{1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    L270(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    L270(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    L0 rotate(){return new L0(getMinLeft(), getMinTop(), mColor);}
}

class LR0 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {0,1,0,0}, {1,1,0,0}, {0,0,0,0}};

    LR0(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR0(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR90 rotate(){return new LR90(getMinLeft(), getMinTop(), mColor);}
}

class LR90 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR90(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR90(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR180 rotate(){return new LR180(getMinLeft(), getMinTop(), mColor);}
}

class LR180 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}};

    LR180(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR180(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR270 rotate(){return new LR270(getMinLeft(), getMinTop(), mColor);}
}

class LR270 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {0,0,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR270(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR270(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    LR0 rotate(){return new LR0(getMinLeft(), getMinTop(), mColor);}
}

class T0 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    T0(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T0(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T90 rotate(){return new T90(getMinLeft(), getMinTop(), mColor);}
}

class T90 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {1,0,0,0}, {0,0,0,0}};

    T90(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T90(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T180 rotate(){return new T180(getMinLeft(), getMinTop(), mColor);}
}

class T180 extends Tetramino{
    private static final byte[][] template = {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    T180(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T180(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T270 rotate(){return new T270(getMinLeft(), getMinTop(), mColor);}
}

class T270 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    T270(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    T270(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    T0 rotate(){return new T0(getMinLeft(), getMinTop(), mColor);}
}

class Z0 extends Tetramino{
    private static final byte[][] template = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    Z0(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z0(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z180 rotate(){return new Z180(getMinLeft(), getMinTop(), mColor);}
}

class Z180 extends Tetramino{
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0},{1,0,0,0}, {0,0,0,0}};

    Z180(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z180(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    Z0 rotate(){return new Z0(getMinLeft(), getMinTop(), mColor);}
}

class RZ0 extends Tetramino{
    private static final byte[][] template = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    RZ0(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ0(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(3 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ180 rotate(){return new RZ180(getMinLeft(), getMinTop(), mColor);}
}

class RZ180 extends Tetramino{
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    RZ180(int left, int top, int color){
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ180(){
        Random random = new Random();
        mColor = randomColor();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int top = -(2 * SQ_SIZE);
        int left = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        Tetramino.loadTemplate(this, template, left, top);
    }

    RZ0 rotate(){return new RZ0(getMinLeft(), getMinTop(), mColor);}
}
