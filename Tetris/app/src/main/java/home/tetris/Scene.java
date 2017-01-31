package home.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static home.tetris.Display.SQ_SIZE;
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

class Scene
{
    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private Paint paint;
    private static Scene scene;
    private Random random;
    private boolean gameOver = false;
    private int score = 0;

    private Scene()
    {
        sceneList = new ArrayList<>();
        random = new Random();
        paint = new Paint();
        paint.setStrokeWidth(1);
        newMino();
    }

    static Scene get()
    {
        if(scene == null){
            scene = new Scene();
            return scene;
        } else {
            return scene;
        }
    }

    private int getColor()
    {
        int k = Integer.MAX_VALUE;
        return Color.argb(random.nextInt(k), random.nextInt(k), random.nextInt(k), random.nextInt(k));
    }

    private Tetramino.Type intToType(int n)
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

    private void newMino()
    {
        Tetramino.Type type = intToType(random.nextInt(7));
        int rotation = random.nextInt(4);
        int leftPos = 0;
        int topPos = 0;
        int color = getColor();
        switch (type)
        {
            case tLine:
                if(rotation == 1) {
                    topPos = -SQ_SIZE;
                    leftPos = random.nextInt(Display.Width - (4 * SQ_SIZE));
                } else {
                    topPos = -(4 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - SQ_SIZE);
                }

                break;
            case tSquare:
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - (2 * SQ_SIZE));
                break;
            case tL:
            case tLReverse:
                if((rotation == 1) || (rotation == 3))
                {
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - (2 * SQ_SIZE));
                }else {
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - (3 * SQ_SIZE));
                }
                break;
            case tZ:
            case tZReverse:
                if(rotation == 1) {
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - (3 * SQ_SIZE));
                } else {
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - (2 * SQ_SIZE));
                }
                break;
            case tT:
                if((rotation == 1) || (rotation == 3)) {
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - (3 * SQ_SIZE));
                } else {
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Display.Width - (2 * SQ_SIZE));
                }
        }
        sceneList.add(new Tetramino(type, rotation, leftPos, topPos, color));
        currentMino = sceneList.get(sceneList.size() - 1);
    }

    void Draw(Canvas canvas)
    {
        canvas.drawARGB(255, 0, 0, 0);

        for(Tetramino tetramino:sceneList)
        {
            paint.setColor(tetramino.getColor());

            for(Rect rect:tetramino.getRects())
                if(rect != null) canvas.drawRect(rect, paint);
        }
    }

    void rotateCurrent()
    {
        int rotation = currentMino.getRotation();
        switch(currentMino.getType())
        {
            case tLine:
            case tZ:
            case tZReverse:
                rotation = (rotation == 1)? 2: 1; break;
            case tT:
            case tL:
            case tLReverse:
                rotation = (rotation == 4)? 1: ++rotation; break;
            case tSquare: return;
        }
        Tetramino mino = new Tetramino(currentMino.getType(), rotation,
                    currentMino.getMinLeft(),  currentMino.getMinTop(), currentMino.getColor());

        if(!collisionLeftRight(mino, currentMino))
        {
            sceneList.remove(currentMino);
            sceneList.add(mino);
            currentMino = mino;
        }
    }

    private boolean lineIsFull(int line)
    {
        int w = 0;
        for(Tetramino current: sceneList)
        {
            for(Rect rect: current.getRects())
            {
                if(rect != null && rect.bottom / SQ_SIZE == line) w += SQ_SIZE;
            }
        }
        return (Display.Width - w) < SQ_SIZE;
    }

    private void deleteLine(int line)
    {
        if(line == 0) return;
        int i = 0;
        while(i <= sceneList.size() - 1)
        {
            Tetramino tetramino = sceneList.get(i);
            int counter = 0;
            for(Rect rect: tetramino.getRects())
            {
                if(rect == null) {
                    counter++;
                    continue;
                }

                if (rect.bottom / SQ_SIZE == line) {
                        tetramino.replaceRect(null, rect);
                }
            }
            if(counter >= 4) sceneList.remove(tetramino); else i++;
        }

    }

    private void fallSquares(int line)
    {
        for(Tetramino tetramino: sceneList)
        {
            for (Rect rect: tetramino.getRects())
            {
                if(rect != null && rect.bottom / SQ_SIZE <= line)
                {
                    rect.top = rect.bottom;
                    rect.bottom += SQ_SIZE;
                }
            }
        }
    }

    private void deleteFullLines()
    {
        int bottom = Display.Height;
        int top = bottom - SQ_SIZE;

        while(top >= 0 && bottom >= top)
        {
            int line = bottom / SQ_SIZE;
            while(lineIsFull(line))
            {
               deleteLine(line);
               fallSquares(line - 1);
               score++;
            }

            bottom = top;
            top -= SQ_SIZE;
        }
    }

    void moveCurrentDown(int y)
    {
        if(y <= currentMino.getMinTop()) return;
        
        Tetramino mino = new Tetramino(currentMino, currentMino.getMinLeft(), y);
        if(!collisionBottom(mino))
        {
            sceneList.remove(currentMino);
            sceneList.add(mino);
            currentMino = mino;
        }
    }

    void moveCurrentDown()
    {
        if (collisionBottom(currentMino))
        {
            if(collisionUp(currentMino))
            {
                gameOver = true;
                return;
            }
            deleteFullLines();
            newMino();
        }
        currentMino.moveDown();
    }

    void moveCurrentLeft(int x)
    {
      Tetramino mino = new Tetramino(currentMino, x);

      if(!collisionLeftRight(mino, currentMino))
      {
          sceneList.remove(currentMino);
          sceneList.add(mino);
          currentMino = mino;
      }
    }

    void moveCurrentRight(int x)
    {
       Tetramino mino = new Tetramino(currentMino, x);
       if(!collisionLeftRight(mino, currentMino))
       {
           sceneList.remove(currentMino);
           sceneList.add(mino);
           currentMino = mino;
       }
    }

    private boolean collisionUp(Tetramino mino)
    {
        for(Rect current: mino.getRects())
            if(current.top < 0) return true;
        return false;
    }

    private boolean collisionBottom(Tetramino mino)
    {
        for(Rect current: mino.getRects())
        {
            if(current == null) continue;
            int bottom = current.bottom;
            if(bottom >= Display.Height) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == mino) continue;

                for(Rect prev: tetramino.getRects())
                {
                    if(prev == null) continue;
                    if(bottom >= prev.top && current.top <= prev.bottom &&
                               current.left < prev.right && current.right > prev.left
                    )
                    { return true;}
                }
            }
        }
        return false;
    }

    private boolean collisionLeftRight(Tetramino newMino, Tetramino current)
    {
        for(Rect newRect: newMino.getRects())
        {
            if(newRect == null) continue;
            if((newRect.right > Display.Width) || (newRect.left < 0) || (newRect.bottom > Display.Height)) return true;
            for(Rect currentRect: current.getRects())
            {
                if(currentRect == null) continue;
                for (Tetramino tetramino : sceneList)
                {
                    if(tetramino == current) continue;

                    for (Rect prev : tetramino.getRects())
                    {
                        if(prev == null) continue;
                        if((newRect.top >= prev.top && newRect.top <= prev.bottom) ||
                                (newRect.bottom >= prev.top && newRect.bottom <= prev.bottom))
                        {
                            if (
                                (
                                  (newRect.left >= prev.left && newRect.left <= prev.right &&
                                     newRect.right>= prev.left && newRect.right <= prev.right) ||
                                       (newRect.right >= prev.left && newRect.right <= prev.right &&
                                          newRect.left >= prev.left && newRect.left <= prev.right)
                                ) ||
                                (
                                  (prev.left > newRect.right && prev.right < currentRect.left) ||
                                       (prev.left > currentRect.right && prev.right < newRect.left)
                                )
                               ) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean getGameOver(){return gameOver;}

    int getScore(){return score;}

    void clear()
    {
        sceneList.clear();
        gameOver = false;
        score = 0;
        newMino();
    }
}
