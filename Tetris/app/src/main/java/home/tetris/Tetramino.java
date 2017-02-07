package home.tetris;

import android.graphics.Rect;

import static home.tetris.Globals.SQ_SIZE;
import static home.tetris.Tetramino.Type.tL;
import static home.tetris.Tetramino.Type.tLReverse;
import static home.tetris.Tetramino.Type.tLine;
import static home.tetris.Tetramino.Type.tSquare;
import static home.tetris.Tetramino.Type.tT;
import static home.tetris.Tetramino.Type.tZ;
import static home.tetris.Tetramino.Type.tZReverse;


/**
 * Created by Дима on 23.01.2017.
 */

class Tetramino
{
    enum Type{tLine, tSquare, tL, tLReverse, tZ, tZReverse, tT}
    private Rect[] blocks;
    private int mColor;
    private int mRotation;
    private Type mType;
    private boolean reverse = false;

    Tetramino(Type type, int rotation, int leftPos, int topPos, int color)
    {
        mType = type;
        mColor = color;
        mRotation = rotation;
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        makeMino(leftPos, topPos);
    }

    Tetramino(Tetramino tetramino, int x)
    {
        mType = tetramino.getType();
        mColor = tetramino.getColor();
        mRotation = tetramino.getRotation();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        int topPos = tetramino.getMinTop();

        makeMino(x, topPos);
    }

    Tetramino(Tetramino tetramino, int x, int y)
    {
        mType = tetramino.getType();
        mColor = tetramino.getColor();
        mRotation = tetramino.getRotation();
        blocks = new Rect[Globals.MAX_BLOCK_CNT];

        makeMino(x, y);
    }

    static Type intToType(int n)
    {
        //tLine, tSquare, tL, tLReverse, tZ, tZReverse, tT
        switch (n){
            case 0: return tLine;
            case 1: return tSquare;
            case 2: return tL;
            case 3: return tLReverse;
            case 4: return tZ;
            case 5: return tZReverse;
            default: return tT;
        }
    }

    // leftPos, topPos левая верхняя точка воображаемого прямоугольника описывающего тетрамино.
    private void makeMino(int leftPos, int topPos)
    {
        leftPos = (leftPos >= SQ_SIZE)? (leftPos / SQ_SIZE) * SQ_SIZE: 0;
        switch(mType)
        {
            case tLine: makeLine(this, leftPos, topPos); break;
            case tSquare: makeSquare(this, leftPos, topPos); break;
            case tL:
                reverse = false;
                makeL(this, leftPos, topPos);
                break;
            case tLReverse:
                reverse = true;
                makeL(this, leftPos, topPos);
                break;
            case tZ:
                reverse = false;
                makeZ(this, leftPos, topPos);
                break;
            case tZReverse:
                reverse = true;
                makeZ(this, leftPos, topPos);
                break;
            case tT:
                makeT(this, leftPos, topPos);
        }
    }

    int getMinLeft() {
        int r = Integer.MAX_VALUE;
        for(Rect rect: blocks)
            if(rect.left < r) r = rect.left;

        return r;
    }

    int getMinTop(){
        int r = Integer.MAX_VALUE;
        for(Rect rect: blocks)
            if(rect.top < r) r = rect.top;

        return r;
    }

    void moveDown()
    {
        for(Rect rect: blocks)
        {
            rect.top += 1;
            rect.bottom = rect.top + SQ_SIZE;
        }
    }

    private static void makeLine(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] blocks = mino.blocks;
        if(mino.mRotation == 1)  // make line horizontal
        {
            for(int i = 0; i < blocks.length; i++)
            {
                blocks[i] = new Rect();
                if(i == 0)
                    blocks[i].left = leftPos;
                else
                    blocks[i].left = blocks[i - 1].right;
                blocks[i].top = topPos;
                blocks[i].right = blocks[i].left + SQ_SIZE ;
                blocks[i].bottom = blocks[i].top + SQ_SIZE;
                leftPos++;
            }
        }else{
            for(int i = 0; i < blocks.length; i++)
            {
                blocks[i] = new Rect();
                if(i == 0) {
                    blocks[i].left = leftPos;
                    blocks[i].top = topPos;
                } else {
                    blocks[i].left = blocks[i - 1].left;
                    blocks[i].top = blocks[i - 1].bottom;
                }
                blocks[i].right = blocks[i].left + SQ_SIZE ;
                blocks[i].bottom = blocks[i].top + SQ_SIZE ;
            }
        }
    }

    private static void makeSquare(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] blocks = mino.blocks;
        for(int i = 0; i < blocks.length; i++)
        {
            blocks[i] = new Rect();
            switch (i)
            {
                case 0:
                    blocks[i].left = leftPos;
                    blocks[i].top = topPos;
                    break;
                case 1:
                    blocks[i].left = blocks[i - 1].right;
                    blocks[i].top = blocks[i - 1].top;
                    break;
                case 2:
                    blocks[i].left = blocks[0].left;
                    blocks[i].top = blocks[0].bottom;
                    break;
                case 3:
                    blocks[i].left = blocks[i - 1].right;
                    blocks[i].top = blocks[i - 1].top;
            }
            blocks[i].right = blocks[i].left + SQ_SIZE;
            blocks[i].bottom = blocks[i].top + SQ_SIZE;
        }
    }

    private static void makeL(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] blocks = mino.blocks;
        for(int i = 0; i < blocks.length; i ++)
        {
            blocks[i] = new Rect();

            switch (mino.mRotation){
                case 1:                                 // Draw like  |_ or _|
                    switch (i){
                        case 0:
                            blocks[i].left = mino.reverse ? leftPos + SQ_SIZE: leftPos;
                            blocks[i].top = topPos;
                            break;
                        case 1:
                            blocks[i].left = blocks[i - 1].left;
                            blocks[i].top = blocks[i - 1].bottom;
                            break;
                        case 2:
                            blocks[i].left = blocks[i - 1].left;
                            blocks[i].top = blocks[i - 1].bottom;
                            break;
                        case 3:
                            blocks[i].left = mino.reverse ? leftPos: blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                    } break;
                case 2:                                    // Draw like |̅̅̅̅ or |___
                    switch (i){
                        case 0:
                            blocks[i].left = leftPos;
                            blocks[i].top = topPos;
                            break;
                        case 1:
                            blocks[i].left = mino.reverse ? leftPos: blocks[i - 1].right;
                            blocks[i].top = mino.reverse ? blocks[i - 1].bottom: blocks[i - 1].top;
                            break;
                        case 2:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                            break;
                        case 3:
                            blocks[i].left = mino.reverse ? blocks[i - 1].right: leftPos;
                            blocks[i].top =  mino.reverse ? blocks[i - 1].top: blocks[i - 1].bottom;
                    } break;
                case 3:                                 // Draw like ̅|  or  |̅
                    switch (i){                         //            |      |
                        case 0:
                            blocks[i].left = leftPos;
                            blocks[i].top = topPos;
                            break;
                        case 1:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = topPos;
                            break;
                        case 2:
                            blocks[i].left = mino.reverse ? leftPos: blocks[i - 1].left;
                            blocks[i].top = blocks[i - 1].bottom;
                            break;
                        case 3:
                            blocks[i].left = blocks[i - 1].left;
                            blocks[i].top = blocks[i - 1].bottom;
                    } break;
                default:                                 // Draw like ___|  or ̅̅̅|
                    switch (i){
                        case 0:
                            blocks[i].left = leftPos;
                            blocks[i].top = mino.reverse ? topPos: topPos + SQ_SIZE;
                            break;
                        case 1:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                            break;
                        case 2:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                            break;
                        case 3:
                            blocks[i].left = blocks[i - 1].left;
                            blocks[i].top = mino.reverse ? blocks[i - 1].bottom: topPos;
                    }
            }
            blocks[i].right = blocks[i].left + SQ_SIZE;
            blocks[i].bottom = blocks[i].top + SQ_SIZE;
        }
    }

    private static void makeZ(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] blocks = mino.blocks;
        for(int i = 0; i < blocks.length; i++)
        {
            blocks[i] = new Rect();

            if (mino.mRotation == 1){            //Draw like  Z or reverse Z
                    switch (i){
                        case 0:
                            blocks[i].left = leftPos;
                            blocks[i].top = mino.reverse ? topPos + SQ_SIZE: topPos;
                            break;
                        case 1:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                            break;
                        case 2:
                            blocks[i].left = blocks[i - 1].left;
                            blocks[i].top = mino.reverse ? topPos: blocks[i - 1].bottom;
                            break;
                        case 3:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                    }
            } else {                    // Draw line N  or И
                switch (i) {
                    case 0:
                        blocks[i].left = mino.reverse ? leftPos: leftPos + SQ_SIZE;
                        blocks[i].top = topPos;
                        break;
                    case 1:
                        blocks[i].left = blocks[i - 1].left;
                        blocks[i].top = blocks[i - 1].bottom;
                        break;
                    case 2:
                        blocks[i].left = mino.reverse ? blocks[i - 1].right: leftPos;
                        blocks[i].top = blocks[i - 1].top;
                        break;
                    case 3:
                        blocks[i].left = blocks[i - 1].left;
                        blocks[i].top = blocks[i - 1].bottom;
                }
            }
            blocks[i].right = blocks[i].left + SQ_SIZE;
            blocks[i].bottom = blocks[i].top + SQ_SIZE;
        }
    }

    private static void makeT(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] blocks = mino.blocks;
        for(int i = 0; i < blocks.length; i++)
        {
            blocks[i] = new Rect();
            switch (mino.mRotation){
                case 1:                             // Draw like _|_
                    switch (i){
                        case 0:
                            blocks[i].left = leftPos + SQ_SIZE;
                            blocks[i].top = topPos;
                            break;
                        case 1:
                            blocks[i].left = leftPos;
                            blocks[i].top = blocks[i - 1].bottom;
                            break;
                        case 2:
                        case 3:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                    }break;
                case 2:                                 // Draw like    |_
                    switch (i){                         //              |
                        case 0:
                            blocks[i].left = leftPos;
                            blocks[i].top = topPos;
                            break;
                        case 1:
                        case 2:
                            blocks[i].left = blocks[i - 1].left;
                            blocks[i].top = blocks[i - 1].bottom;
                            break;
                        case 3:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top - SQ_SIZE;
                    } break;
                case 3:                         // Draw like T
                    switch (i){
                        case 0:
                            blocks[i].left = leftPos;
                            blocks[i].top = topPos;
                            break;
                        case 1:
                        case 2:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = blocks[i - 1].top;
                            break;
                        case 3:
                            blocks[i].left = blocks[i - 1].left - SQ_SIZE;
                            blocks[i].top = blocks[i - 1].bottom;
                    } break;
                default:                 //                 Draw like _|
                    switch (i) {         //                            |
                        case 0:
                            blocks[i].left = leftPos;
                            blocks[i].top = topPos + SQ_SIZE;
                            break;
                        case 1:
                            blocks[i].left = blocks[i - 1].right;
                            blocks[i].top = topPos;
                            break;
                        case 2:
                        case 3:
                            blocks[i].left = blocks[i - 1].left;
                            blocks[i].top = blocks[i - 1].bottom;
                    }
            }
            blocks[i].right = blocks[i].left + SQ_SIZE;
            blocks[i].bottom = blocks[i].top + SQ_SIZE;
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

    int getRotation(){return mRotation;}
}
