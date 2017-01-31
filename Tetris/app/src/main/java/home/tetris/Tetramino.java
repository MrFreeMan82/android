package home.tetris;

import android.graphics.Rect;

import static home.tetris.Display.SQ_SIZE;


/**
 * Created by Дима on 23.01.2017.
 */

class Tetramino
{
    enum Type{tLine, tSquare, tL, tLReverse, tZ, tZReverse, tT}
    private Rect[] rects;
    private int mColor;
    private int mRotation;
    private Type mType;
    private boolean reverse = false;

    Tetramino(Type type, int rotation, int leftPos, int topPos, int color)
    {
        mType = type;
        mColor = color;
        mRotation = rotation;
        rects = new Rect[4];

        makeMino(leftPos, topPos);
    }

    Tetramino(Tetramino tetramino, int x)
    {
        mType = tetramino.getType();
        mColor = tetramino.getColor();
        mRotation = tetramino.getRotation();
        rects = new Rect[4];

        int topPos = tetramino.getMinTop();

        makeMino(x, topPos);
    }

    Tetramino(Tetramino tetramino, int x, int y)
    {
        mType = tetramino.getType();
        mColor = tetramino.getColor();
        mRotation = tetramino.getRotation();
        rects = new Rect[4];

        makeMino(x, y);
    }
// leftPos, topPos левая верхняя точка воображаемого прямоугольника описывающего тетрамино.
    private void makeMino(int leftPos, int topPos)
    {
        leftPos = (leftPos > SQ_SIZE)? (leftPos / SQ_SIZE) * SQ_SIZE: 0;
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
        for(Rect rect: rects)
            if(rect.left < r) r = rect.left;

        return r;
    }

    int getMinTop(){
        int r = Integer.MAX_VALUE;
        for(Rect rect: rects)
            if(rect.top < r) r = rect.top;

        return r;
    }

    void moveDown()
    {
        for(Rect rect: rects)
        {
            rect.top += 1;
            rect.bottom = rect.top + SQ_SIZE;
        }
    }

    private static void makeLine(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] rects = mino.rects;
        if(mino.mRotation == 1)  // make line horizontal
        {
            for(int i = 0; i < rects.length; i++)
            {
                rects[i] = new Rect();
                if(i == 0)
                    rects[i].left = leftPos;
                else
                    rects[i].left = rects[i - 1].right;
                rects[i].top = topPos;
                rects[i].right = rects[i].left + SQ_SIZE ;
                rects[i].bottom = rects[i].top + SQ_SIZE;
                leftPos++;
            }
        }else{
            for(int i = 0; i < rects.length; i++)
            {
                rects[i] = new Rect();
                if(i == 0) {
                    rects[i].left = leftPos;
                    rects[i].top = topPos;
                } else {
                    rects[i].left = rects[i - 1].left;
                    rects[i].top = rects[i - 1].bottom;
                }
                rects[i].right = rects[i].left + SQ_SIZE ;
                rects[i].bottom = rects[i].top + SQ_SIZE ;
            }
        }
    }

    private static void makeSquare(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] rects = mino.rects;
        for(int i = 0; i < rects.length; i++)
        {
            rects[i] = new Rect();
            switch (i)
            {
                case 0:
                    rects[i].left = leftPos;
                    rects[i].top = topPos;
                    break;
                case 1:
                    rects[i].left = rects[i - 1].right;
                    rects[i].top = rects[i - 1].top;
                    break;
                case 2:
                    rects[i].left = rects[0].left;
                    rects[i].top = rects[0].bottom;
                    break;
                case 3:
                    rects[i].left = rects[i - 1].right;
                    rects[i].top = rects[i - 1].top;
            }
            rects[i].right = rects[i].left + SQ_SIZE;
            rects[i].bottom = rects[i].top + SQ_SIZE;
        }
    }

    private static void makeL(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] rects = mino.rects;
        for(int i = 0; i < rects.length; i ++)
        {
            rects[i] = new Rect();

            switch (mino.mRotation){
                case 1:                                 // Draw like  |_ or _|
                    switch (i){
                        case 0:
                            rects[i].left = mino.reverse ? leftPos + SQ_SIZE: leftPos;
                            rects[i].top = topPos;
                            break;
                        case 1:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = rects[i - 1].bottom;
                            break;
                        case 2:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = rects[i - 1].bottom;
                            break;
                        case 3:
                            rects[i].left = mino.reverse ? leftPos: rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                    } break;
                case 2:                                    // Draw like |̅̅̅̅ or |___
                    switch (i){
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = topPos;
                            break;
                        case 1:
                            rects[i].left = mino.reverse ? leftPos: rects[i - 1].right;
                            rects[i].top = mino.reverse ? rects[i - 1].bottom: rects[i - 1].top;
                            break;
                        case 2:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                            break;
                        case 3:
                            rects[i].left = mino.reverse ? rects[i - 1].right: leftPos;
                            rects[i].top =  mino.reverse ? rects[i - 1].top: rects[i - 1].bottom;
                    } break;
                case 3:                                 // Draw like ̅|  or  |̅
                    switch (i){                         //            |      |
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = topPos;
                            break;
                        case 1:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = topPos;
                            break;
                        case 2:
                            rects[i].left = mino.reverse ? leftPos: rects[i - 1].left;
                            rects[i].top = rects[i - 1].bottom;
                            break;
                        case 3:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = rects[i - 1].bottom;
                    } break;
                default:                                 // Draw like ___|  or ̅̅̅|
                    switch (i){
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = mino.reverse ? topPos: topPos + SQ_SIZE;
                            break;
                        case 1:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                            break;
                        case 2:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                            break;
                        case 3:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = mino.reverse ? rects[i - 1].bottom: topPos;
                    }
            }
            rects[i].right = rects[i].left + SQ_SIZE;
            rects[i].bottom = rects[i].top + SQ_SIZE;
        }
    }

    private static void makeZ(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] rects = mino.rects;
        for(int i = 0; i < rects.length; i++)
        {
            rects[i] = new Rect();

            if (mino.mRotation == 1){            //Draw like  Z or reverse Z
                    switch (i){
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = mino.reverse ? topPos + SQ_SIZE: topPos;
                            break;
                        case 1:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                            break;
                        case 2:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = mino.reverse ? topPos: rects[i - 1].bottom;
                            break;
                        case 3:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                    }
            } else {                    // Draw line N  or И
                switch (i) {
                    case 0:
                        rects[i].left = mino.reverse ? leftPos: leftPos + SQ_SIZE;
                        rects[i].top = topPos;
                        break;
                    case 1:
                        rects[i].left = rects[i - 1].left;
                        rects[i].top = rects[i - 1].bottom;
                        break;
                    case 2:
                        rects[i].left = mino.reverse ? rects[i - 1].right: leftPos;
                        rects[i].top = rects[i - 1].top;
                        break;
                    case 3:
                        rects[i].left = rects[i - 1].left;
                        rects[i].top = rects[i - 1].bottom;
                }
            }
            rects[i].right = rects[i].left + SQ_SIZE;
            rects[i].bottom = rects[i].top + SQ_SIZE;
        }
    }

    private static void makeT(Tetramino mino, int leftPos, int topPos)
    {
        Rect[] rects = mino.rects;
        for(int i = 0; i < rects.length; i++)
        {
            rects[i] = new Rect();
            switch (mino.mRotation){
                case 1:                             // Draw like _|_
                    switch (i){
                        case 0:
                            rects[i].left = leftPos + SQ_SIZE;
                            rects[i].top = topPos;
                            break;
                        case 1:
                            rects[i].left = leftPos;
                            rects[i].top = rects[i - 1].bottom;
                            break;
                        case 2:
                        case 3:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                    }break;
                case 2:                                 // Draw like    |_
                    switch (i){                         //              |
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = topPos;
                            break;
                        case 1:
                        case 2:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = rects[i - 1].bottom;
                            break;
                        case 3:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top - SQ_SIZE;
                    } break;
                case 3:                         // Draw like T
                    switch (i){
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = topPos;
                            break;
                        case 1:
                        case 2:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                            break;
                        case 3:
                            rects[i].left = rects[i - 1].left - SQ_SIZE;
                            rects[i].top = rects[i - 1].bottom;
                    } break;
                default:                 //                 Draw like _|
                    switch (i) {         //                            |
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = topPos + SQ_SIZE;
                            break;
                        case 1:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = topPos;
                            break;
                        case 2:
                        case 3:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = rects[i - 1].bottom;
                    }
            }
            rects[i].right = rects[i].left + SQ_SIZE;
            rects[i].bottom = rects[i].top + SQ_SIZE;
        }
    }

    int getColor(){return mColor;}

    Rect[] getRects(){return rects;}

    void replaceRect(Rect newRect, Rect oldRect)
    {
        for (int i = 0; i < rects.length; i++)
            if(rects[i] != null && rects[i].equals(oldRect)) rects[i] = newRect;
    }

    Type getType(){return mType;}

    int getRotation(){return mRotation;}
}
