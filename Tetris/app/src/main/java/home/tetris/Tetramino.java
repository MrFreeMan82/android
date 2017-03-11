package home.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;

import static home.tetris.Block.SIZE;

/**
 * Created by Дима on 23.01.2017.
 * Класс служит для описания возможных видов Тетрамино
 * Цифры в именах классов обозначают угол поворота
 */

final class Block
{
    static final int SIZE = Scene.WIDTH / Scene.BLOCKS_PER_WIDTH;
    private static final int DELTA = 5 * (Scene.HEIGHT / Scene.SCREEN_DELTA);

    boolean visible = true;
    Tetramino tetramino;
    Rect rect;
    private Rect subRect;
    private Rect mid;
    private Point p1;
    private Point p2;

    Block(int left, int top, int right, int bottom)
    {
        rect = new Rect(left, top, right, bottom);
        subRect = new Rect(rect);
        mid = new Rect(rect);
        p1 = new Point();
        p2 = new Point();
        updateSubRect();
    }

    void moveDown(int value)
    {
        rect.top += value;
        rect.bottom = rect.top + SIZE;
        updateSubRect();
    }

    void moveLeft()
    {
        rect.left -= SIZE;
        rect.right = rect.left + SIZE;
        updateSubRect();
    }

    void moveRight()
    {
        rect.left = rect.right;
        rect.right = rect.left + SIZE;
        updateSubRect();
    }

    void decrease()
    {
        rect.bottom -= 1;
        updateSubRect();
    }

    private void updateSubRect()
    {
        subRect.left = rect.left + DELTA;
        subRect.top = rect.top + DELTA;
        subRect.right = rect.right - DELTA;
        subRect.bottom = rect.bottom - DELTA;

        if(subRect.bottom > subRect.top) {
            int h = subRect.bottom - subRect.top;
            int w = subRect.right - subRect.left;

            p1.x = subRect.right;
            p1.y = subRect.top + (h / 2) + ((h / 2) / 2);

            p2.x = subRect.left + ((w / 2) + ((w / 2) / 2));
            p2.y = subRect.bottom;
        }

        mid.left = rect.left + (DELTA / 2);
        mid.top = rect.top + (DELTA / 2);
        mid.right = rect.right - (DELTA / 2);
        mid.bottom = rect.bottom - (DELTA / 2);
    }

    void draw(Canvas canvas, Paint paint)
    {
        if(!visible) return;
        int color = tetramino.getColor();
        paint.setColor(color);
        canvas.drawRect(rect, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(mid, paint);
        paint.setColor(color);
        canvas.drawRect(subRect, paint);

        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);

        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
    }
}

abstract class Tetramino
{
    static final int MAX_BLOCK_CNT = 4;
    private static final String TAG = "Tetramino";

    private Block[] blocks;
    private int mColor;

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

    int getColor(){return mColor;}
    void setColor(int color){mColor = color;}

    Block[] getBlocks(){return blocks;}

    private static class Generator
    {
        private static final ArrayList<Class<? extends Tetramino>> types = new ArrayList<>
        (
          Arrays.asList
          (
            LineHorizontal.class, LineVertical.class, Square.class, L0.class, L90.class,
            L180.class, L270.class, LR0.class, LR90.class, LR180.class, LR270.class, T0.class,
            T90.class, T180.class, T270.class, Z0.class, Z180.class, RZ0.class, RZ180.class
           )
        );

        static int stat[] = {0, 0, 0, 0, 0, 0, 0};

        private static void add(int type)
        {
            if(type >= 0 && type <= 1) stat[0]++;
            else if(type == 2) stat[1]++;
            else if(type >= 3 && type <= 6) stat[2]++;
            else if(type >= 7 && type <= 10) stat[3]++;
            else if(type >= 11 && type <= 14) stat[4]++;
            else if(type >= 15 && type <= 16) stat[5]++;
            else if(type >= 17 && type <= 18) stat[6]++;
        }

        static Tetramino next()
        {
            try{
                int type = (int) (Math.random() * types.size());
                add(type);
                return types.get(type).newInstance();
            } catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "Error while creating new tetramino");
            }
            return null;
        }
    }

    static void clearStatistic(){
        for(int i = 0; i < Generator.stat.length; i++) Generator.stat[i] = 0;
    }
    static int[] getStatistic(){return Generator.stat;}
    static Tetramino next(){return Generator.next();}

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
    abstract int blockPerHeight();

    Tetramino(){blocks = new Block[MAX_BLOCK_CNT];}
}

class LineHorizontal extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 1;
    private static final byte[][] template = {{1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    LineHorizontal(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    LineHorizontal(){
        this((int)(Math.random() *
                (Scene.WIDTH - MAX_BLOCK_CNT * SIZE)), -SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}

    LineVertical rotate(){return new LineVertical(getMinLeft(), getMinTop(), getColor());}
}

class LineVertical extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 4;
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,0,0,0}, {1,0,0,0}};

    LineVertical(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    LineVertical(){
        this((int) (Math.random() *
                (Scene.WIDTH - SIZE)),
                    -(BLOCK_PER_HEIGHT * SIZE), randomColor());
    }

    final int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    LineHorizontal rotate(){return new LineHorizontal(getMinLeft(), getMinTop(), getColor());}
}

class Square extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    Square(int left, int top, int color)
    {
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    Square(){
        this((int) (Math.random() *
                (Scene.WIDTH - 2 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    Square rotate(){return null;}
}

class L0 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{1,0,0,0}, {1,0,0,0}, {1,1,0,0}, {0,0,0,0}};

    L0(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    L0(){
        this((int) (Math.random() *
                (Scene.WIDTH - 2 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    L90 rotate(){return new L90(getMinLeft(), getMinTop(), getColor());}
}

class L90 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{1,1,1,0}, {1,0,0,0}, {0,0,0,0}, {0,0,0,0}};

    L90(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    L90(){
        this((int) (Math.random() *
                (Scene.WIDTH - 3 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    L180 rotate(){return new L180(getMinLeft(), getMinTop(), getColor());}
}

class L180 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    L180(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    L180(){
        this((int) (Math.random() *
                (Scene.WIDTH - 2 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    L270 rotate(){return new L270(getMinLeft(), getMinTop(), getColor());}
}

class L270 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{0,0,1,0},{1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    L270(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    L270(){
        this((int) (Math.random() *
                (Scene.WIDTH - 3 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    L0 rotate(){return new L0(getMinLeft(), getMinTop(), getColor());}
}

class LR0 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{0,1,0,0}, {0,1,0,0}, {1,1,0,0}, {0,0,0,0}};

    LR0(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    LR0(){
        this((int) (Math.random() *
                (Scene.WIDTH - 3 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    LR90 rotate(){return new LR90(getMinLeft(), getMinTop(), getColor());}
}

class LR90 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR90(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    LR90(){
        this((int) (Math.random() *
                (Scene.WIDTH - 3 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    LR180 rotate(){return new LR180(getMinLeft(), getMinTop(), getColor());}
}

class LR180 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{1,1,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}};

    LR180(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    LR180(){
        this((int) (Math.random() *
                (Scene.WIDTH - 2 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    LR270 rotate(){return new LR270(getMinLeft(), getMinTop(), getColor());}
}

class LR270 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{1,1,1,0}, {0,0,1,0}, {0,0,0,0}, {0,0,0,0}};

    LR270(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    LR270(){
        this((int) (Math.random() *
                (Scene.WIDTH - 3 * SIZE)),
                    -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    LR0 rotate(){return new LR0(getMinLeft(), getMinTop(), getColor());}
}

class T0 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    T0(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    T0(){
        this((int) (Math.random() *
                (Scene.WIDTH - 3 * SIZE)),
                        -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    T90 rotate(){return new T90(getMinLeft(), getMinTop(), getColor());}
}

class T90 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {1,0,0,0}, {0,0,0,0}};

    T90(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    T90(){
        this((int) (Math.random() *
                    (Scene.WIDTH - 2 * SIZE)),
                            -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    T180 rotate(){return new T180(getMinLeft(), getMinTop(), getColor());}
}

class T180 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    T180(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    T180(){
        this((int) (Math.random() *
                    (Scene.WIDTH - 3 * SIZE)),
                            -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    T270 rotate(){return new T270(getMinLeft(), getMinTop(), getColor());}
}

class T270 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    T270(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    T270(){
        this((int) (Math.random() *
                (Scene.WIDTH - 2 * SIZE)),
                        -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    T0 rotate(){return new T0(getMinLeft(), getMinTop(), getColor());}
}

class Z0 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    Z0(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    Z0(){
        this((int) (Math.random() *
                (Scene.WIDTH - 2 * SIZE)),
                        -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    Z180 rotate(){return new Z180(getMinLeft(), getMinTop(), getColor());}
}

class Z180 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{0,1,0,0}, {1,1,0,0},{1,0,0,0}, {0,0,0,0}};

    Z180(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    Z180(){
        this((int) (Math.random() *
                (Scene.WIDTH - 3 * SIZE)),
                        -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    Z0 rotate(){return new Z0(getMinLeft(), getMinTop(), getColor());}
}

class RZ0 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 2;
    private static final byte[][] template = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    RZ0(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    RZ0(){
        this((int) (Math.random() *
                (Scene.WIDTH - 2 * SIZE)),
                        -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    RZ180 rotate(){return new RZ180(getMinLeft(), getMinTop(), getColor());}
}

class RZ180 extends Tetramino{
    private static final int BLOCK_PER_HEIGHT = 3;
    private static final byte[][] template = {{1,0,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    RZ180(int left, int top, int color){
        Tetramino.loadTemplate(this, template, left, top);
        setColor(color);
    }

    @SuppressWarnings("unused")
    RZ180(){
        this((int) (Math.random() *
                        (Scene.WIDTH - 3 * SIZE)),
                            -BLOCK_PER_HEIGHT * SIZE, randomColor());
    }

    int blockPerHeight(){return BLOCK_PER_HEIGHT;}
    RZ0 rotate(){return new RZ0(getMinLeft(), getMinTop(), getColor());}
}
