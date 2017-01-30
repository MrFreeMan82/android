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
// leftPos, topPos левая верхняя точка воображаемого прямоугольника описывающего тетрамино.
    private void makeMino(int leftPos, int topPos)
    {
        leftPos = (leftPos > SQ_SIZE)? (leftPos / SQ_SIZE) * SQ_SIZE: 0;
        switch(mType)
        {
            case tLine: makeLine(leftPos, topPos); break;
            case tSquare: makeSquare(leftPos, topPos); break;
            case tL:
                reverse = false;
                makeL(leftPos, topPos);
                break;
            case tLReverse:
                reverse = true;
                makeL(leftPos, topPos);
                break;
            case tZ:
                reverse = false;
                makeZ(leftPos, topPos);
                break;
            case tZReverse:
                reverse = true;
                makeZ(leftPos, topPos);
                break;
            case tT:
                makeT(leftPos, topPos);
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

    private void makeLine(int leftPos, int topPos)
    {
        if(mRotation == 1)  // make line horizontal
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
                 //   mMargins[i] = new Margins(rects[i], INTERVAL, INTERVAL, INTERVAL, 0);
                } else {
                    rects[i].left = rects[i - 1].left;
                    rects[i].top = rects[i - 1].bottom;
                  //  mMargins[i] = new Margins(rects[i], INTERVAL, 0, INTERVAL, 0);
                }
                rects[i].right = rects[i].left + SQ_SIZE ;
                rects[i].bottom = rects[i].top + SQ_SIZE ;
            }
        }
    }

    private void makeSquare(int leftPos, int topPos)
    {
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

    private void makeL(int leftPos, int topPos)
    {
        for(int i = 0; i < rects.length; i ++)
        {
            rects[i] = new Rect();

            switch (mRotation){
                case 1:                                 // Draw like  |_ or _|
                    switch (i){
                        case 0:
                            rects[i].left = reverse ? leftPos + SQ_SIZE: leftPos;
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
                            rects[i].left = reverse ? leftPos: rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                    } break;
                case 2:                                    // Draw like |̅̅̅̅ or |___
                    switch (i){
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = topPos;
                            break;
                        case 1:
                            rects[i].left = reverse ? leftPos: rects[i - 1].right;
                            rects[i].top = reverse ? rects[i - 1].bottom: rects[i - 1].top;
                            break;
                        case 2:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                            break;
                        case 3:
                            rects[i].left = reverse ? rects[i - 1].right: leftPos;
                            rects[i].top =  reverse ? rects[i - 1].top: rects[i - 1].bottom;
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
                            rects[i].left = reverse ? leftPos: rects[i - 1].left;
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
                            rects[i].top = reverse ? topPos: topPos + SQ_SIZE;
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
                            rects[i].top = reverse ? rects[i - 1].bottom: topPos;
                    }
            }
            rects[i].right = rects[i].left + SQ_SIZE;
            rects[i].bottom = rects[i].top + SQ_SIZE;
        }
    }

    private void makeZ(int leftPos, int topPos)
    {
        for(int i = 0; i < rects.length; i++)
        {
            rects[i] = new Rect();

            if (mRotation == 1){            //Draw like  Z or reverse Z
                    switch (i){
                        case 0:
                            rects[i].left = leftPos;
                            rects[i].top = reverse ? topPos + SQ_SIZE: topPos;
                            break;
                        case 1:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                            break;
                        case 2:
                            rects[i].left = rects[i - 1].left;
                            rects[i].top = reverse ? topPos: rects[i - 1].bottom;
                            break;
                        case 3:
                            rects[i].left = rects[i - 1].right;
                            rects[i].top = rects[i - 1].top;
                    }
            } else {                    // Draw line N  or И
                switch (i) {
                    case 0:
                        rects[i].left = reverse ? leftPos: leftPos + SQ_SIZE;
                        rects[i].top = topPos;
                        break;
                    case 1:
                        rects[i].left = rects[i - 1].left;
                        rects[i].top = rects[i - 1].bottom;
                        break;
                    case 2:
                        rects[i].left = reverse ? rects[i - 1].right: leftPos;
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

    private void makeT(int leftPos, int topPos)
    {
        for(int i = 0; i < rects.length; i++)
        {
            rects[i] = new Rect();
            switch (mRotation){
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
