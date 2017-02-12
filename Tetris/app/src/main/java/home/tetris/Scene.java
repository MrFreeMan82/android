package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static home.tetris.Globals.SQ_SIZE;

/**
 * Created by Дима on 23.01.2017.
 * Класс сингетный. Служит для описания игрового поля (положение всех тетрамино на поле)
 * Описывает их движение и повороты.
 * Удаляет заполненые линии ведет учет текущих уровеня и очков.
 */

class Scene
{
    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private Paint paint;
    private static Scene scene;
    private Random random;
    private Sound sound;
    private boolean gameOver = false;
    private int score = 0;
    private int level = 1;
    private int oldLeft = -1;

    private Scene(Context context)
    {
        sound = new Sound(context);
        sceneList = new ArrayList<>();
        random = new Random();
        paint = new Paint();
        paint.setStrokeWidth(1);
        newMino();
    }

    static Scene get(Context context)
    {
        if(scene == null)scene = new Scene(context);
        return scene;
    }

    private int getColor()
    {
        int k = Integer.MAX_VALUE;
        return Color.argb(random.nextInt(k), random.nextInt(k), random.nextInt(k), random.nextInt(k));
    }
// Создает новое тетрамино за пределами экрана, все параметры выбираются случайно
    private void newMino()
    {
        Tetramino.Type type = Tetramino.intToType(random.nextInt(7));
        int rotation = 1;
        int leftPos = 0;
        int topPos = 0;
        int color = getColor();
        switch (type)
        {
            case tLine:
                rotation = random.nextInt(2);
                if(rotation == 1) {
                    topPos = -SQ_SIZE;
                    leftPos = random.nextInt(Globals.WIDTH - (Globals.MAX_BLOCK_CNT * SQ_SIZE));
                } else {
                    topPos = -(Globals.MAX_BLOCK_CNT * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - SQ_SIZE);
                }
                break;
            case tSquare:
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
                break;
            case tL:
            case tLReverse:
                rotation = random.nextInt(4);
                if((rotation == 1) || (rotation == 3))
                {
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
                }else {
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
                }
                break;
            case tZ:
            case tZReverse:
                rotation = random.nextInt(2);
                if(rotation == 1) {
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
                } else {
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
                }
                break;
            case tT:
                rotation = random.nextInt(4);
                if((rotation == 1) || (rotation == 3)) {
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
                } else {
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
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

            for(Rect block:tetramino.getBlocks())
                if(block != null) canvas.drawRect(block, paint);
        }
    }

    void rotateCurrent()
    {
        sound.play(Globals.ROTATE);
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
            for(Rect block: current.getBlocks())
            {
                if(block != null && block.bottom / SQ_SIZE == line) w += SQ_SIZE;
            }
        }
        return (Globals.WIDTH - w) < SQ_SIZE;
    }

    private void deleteLine(int line)
    {
        if(line == 0) return;
        int i = 0;
        while(i <= sceneList.size() - 1)
        {
            Tetramino tetramino = sceneList.get(i);
            int counter = 0;
            for(Rect block: tetramino.getBlocks())
            {
                if(block == null) {
                    counter++;
                    continue;
                }

                if (block.bottom / SQ_SIZE == line) {
                        tetramino.replaceBlock(null, block);
                }
            }
            if(counter == Globals.MAX_BLOCK_CNT) {
                sceneList.remove(tetramino);
            } else i++;
        }

    }

    private void fallSquares(int line)
    {
        for(Tetramino tetramino: sceneList)
        {
            for (Rect block: tetramino.getBlocks())
            {
                if(block != null && block.bottom / SQ_SIZE <= line)
                {
                    block.top = block.bottom;
                    block.bottom += SQ_SIZE;
                }
            }
        }
    }

    private void deleteFullLines()
    {
        int bottom = Globals.HEIGHT;
        int top = bottom - SQ_SIZE;
        boolean eneblePlay = true;

        while(top >= 0 && bottom >= top)
        {
            int line = bottom / SQ_SIZE;
            while(lineIsFull(line))
            {
               deleteLine(line);
               if(eneblePlay){
                   sound.play(Globals.DELETE_LINE);
                   eneblePlay = false;
               }
               fallSquares(line - 1);
               score++;

               if((score % Globals.SCORE_PER_LEVEL) == 0){
                    level++;
                    sound.play(Globals.LEVEL_UP);
               }
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
            sound.play(Globals.MOVE_MINO);
            sceneList.remove(currentMino);
            sceneList.add(mino);
            currentMino = mino;
        }
    }

    void moveCurrentDown()
    {
        for (int k = level + 2; k > 0; k--) {
            if (collisionBottom(currentMino)) {
                if (collisionUp(currentMino)) {
                    gameOver = true;
                    return;
                }
                sound.play(Globals.IMPACT);
                deleteFullLines();
                newMino();
            }

            currentMino.moveDown();
        }
    }

    void moveCurrentLeft(int x)
    {
      Tetramino mino = new Tetramino(currentMino, x);

      if(!collisionLeftRight(mino, currentMino))
      {
          if(oldLeft != mino.getMinLeft()) {
              oldLeft = mino.getMinLeft();
              sound.play(Globals.MOVE_MINO);
          }
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
           if(oldLeft != mino.getMinLeft()) {
               oldLeft = mino.getMinLeft();
               sound.play(Globals.MOVE_MINO);
           }
           sceneList.remove(currentMino);
           sceneList.add(mino);
           currentMino = mino;
       }
    }

    private boolean collisionUp(Tetramino mino)
    {
        for(Rect current: mino.getBlocks())
            if(current.top < 0) return true;
        return false;
    }

    private boolean collisionBottom(Tetramino mino)
    {
        for(Rect current: mino.getBlocks())
        {
            if(current == null) continue;
            int bottom = current.bottom;
            if(bottom >= Globals.HEIGHT) {
                return true;
            }

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == mino) continue;

                for(Rect prev: tetramino.getBlocks())
                {
                    if(prev == null) continue;
                    if(bottom >= prev.top && current.top <= prev.bottom &&
                               current.left < prev.right && current.right > prev.left
                    )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean collisionLeftRight(Tetramino newMino, Tetramino current)
    {
        for(Rect newBlock: newMino.getBlocks())
        {
            if(newBlock == null) continue;
            if((newBlock.right > Globals.WIDTH) || (newBlock.left < 0) || (newBlock.bottom > Globals.HEIGHT)) return true;
            for(Rect currentRect: current.getBlocks())
            {
                if(currentRect == null) continue;
                for (Tetramino tetramino : sceneList)
                {
                    if(tetramino == current) continue;

                    for (Rect prev : tetramino.getBlocks())
                    {
                        if(prev == null) continue;
                        if((newBlock.top >= prev.top && newBlock.top <= prev.bottom) ||
                                (newBlock.bottom >= prev.top && newBlock.bottom <= prev.bottom))
                        {
                            if (
                                (
                                  (newBlock.left >= prev.left && newBlock.left <= prev.right &&
                                          newBlock.right>= prev.left && newBlock.right <= prev.right) ||
                                       (newBlock.right >= prev.left && newBlock.right <= prev.right &&
                                               newBlock.left >= prev.left && newBlock.left <= prev.right)
                                ) ||
                                (
                                  (prev.left > newBlock.right && prev.right < currentRect.left) ||
                                       (prev.left > currentRect.right && prev.right < newBlock.left)
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

    int getLevel(){return level;}

    void clear()
    {
        sceneList.clear();
        gameOver = false;
        score = 0;
        level = 1;
        newMino();
    }
}
