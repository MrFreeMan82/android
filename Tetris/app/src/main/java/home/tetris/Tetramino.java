package home.tetris;

import android.graphics.Rect;

import static home.tetris.Globals.SQ_SIZE;
import static home.tetris.Type.tL0;
import static home.tetris.Type.tL180;
import static home.tetris.Type.tL270;
import static home.tetris.Type.tL90;
import static home.tetris.Type.tLR0;
import static home.tetris.Type.tLR180;
import static home.tetris.Type.tLR270;
import static home.tetris.Type.tLR90;
import static home.tetris.Type.tLineHorizontal;
import static home.tetris.Type.tLineVertical;
import static home.tetris.Type.tRZ0;
import static home.tetris.Type.tRZ180;
import static home.tetris.Type.tSquare;
import static home.tetris.Type.tT0;
import static home.tetris.Type.tT180;
import static home.tetris.Type.tT270;
import static home.tetris.Type.tT90;
import static home.tetris.Type.tZ0;
import static home.tetris.Type.tZ180;

/**
 * Created by Дима on 23.01.2017.
 * Класс служит для описания возможных видов Тетрамино
 */

// Цифры обозначают угол поворота
enum Type{tLineHorizontal, tLineVertical, tL0, tL90, tL180, tL270, tLR0, tLR90, tLR180, tLR270,
           tT0, tT90, tT180, tT270, tZ0, tZ180, tRZ0, tRZ180, tSquare}

class Tetramino
{
    private Rect[] blocks;
    private int mColor;
    private Type mType;
                // leftPos, topPos левая верхняя точка воображаемого прямоугольника описывающего тетрамино.
    Tetramino(Type type, int leftPos, int topPos, int color)
    {
        mType = type;
        mColor = color;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        getFromTemplate(this, templateFromType(mType), leftPos, topPos);
    }

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

    Type getType(){return mType;}

    private static void getFromTemplate(Tetramino tetramino, byte[][] template, int left, int top)
    {
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

    static Type intToType(int n)
    {
        switch (n){
            case 0: return tLineHorizontal;
            case 1: return tLineVertical;
            case 2: return tL0;
            case 3: return tL90;
            case 4: return tL180;
            case 5: return tL270;
            case 6: return tLR0;
            case 7: return tLR90;
            case 8: return tLR180;
            case 9: return tLR270;
            case 10: return tT0;
            case 11: return tT90;
            case 12: return tT180;
            case 13: return tT270;
            case 14: return tZ0;
            case 15: return tZ180;
            case 16: return tRZ0;
            case 17: return tRZ180;
            default: return tSquare;
        }
    }

    private static byte[][] templateFromType(Type type){
        switch (type){
            case tLineHorizontal: return lineHorizontalTemplate;
            case tLineVertical: return lineVerticalTemplate;
            case tSquare: return squareTemplate;
            case tL0: return L0Template;
            case tL90: return L90Template;
            case tL180: return L180Template;
            case tL270: return L270Template;
            case tLR0: return LR0Template;
            case tLR90: return LR90Template;
            case tLR180: return LR180Template;
            case tLR270: return LR270Template;
            case tT0: return T0Template;
            case tT90: return T90Template;
            case tT180: return T180Template;
            case tT270: return T270Template;
            case tZ0: return Z0Template;
            case tZ180: return Z180Template;
            case tRZ0: return ZR0Template;
            case tRZ180: return ZR180Template;

            default: return null;
        }
    }

    private static final byte[][] lineHorizontalTemplate = {{1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] lineVerticalTemplate =  {{1,0,0,0}, {1,0,0,0}, {1,0,0,0}, {1,0,0,0}};
    private static final byte[][] squareTemplate = {{1,1,0,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

    private static final byte[][] L0Template = {{1,0,0,0}, {1,0,0,0}, {1,1,0,0}, {0,0,0,0}};
    private static final byte[][] L90Template = {{1,1,1,0}, {1,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] L180Template = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};
    private static final byte[][] L270Template = {{0,0,1,0},{1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

    private static final byte[][] LR0Template = {{0,1,0,0}, {0,1,0,0}, {1,1,0,0}, {0,0,0,0}};
    private static final byte[][] LR90Template = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] LR180Template = {{1,1,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}};
    private static final byte[][] LR270Template = {{1,1,1,0}, {0,0,1,0}, {0,0,0,0}, {0,0,0,0}};

    private static final byte[][] T0Template = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] T90Template = {{1,0,0,0}, {1,1,0,0}, {1,0,0,0}, {0,0,0,0}};
    private static final byte[][] T180Template = {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] T270Template = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

    private static final byte[][] Z0Template = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] Z180Template = {{0,1,0,0}, {1,1,0,0},{1,0,0,0}, {0,0,0,0}};

    private static final byte[][] ZR0Template = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};
    private static final byte[][] ZR180Template = {{1,0,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};
}
