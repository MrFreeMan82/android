package home.opengl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static home.opengl.Square.SQ_SIZE;
import static home.opengl.Square.VERTEX_SIZE;

/**
 * Created by Дима on 18.01.2017.
 */


class Tetramino
{
    enum Type {tLineVertical, tLineHorizontal, tSquare, tT, tL}
    static final int DOWN_DELTA = 2;
    static final int MOVE_DELTA = 5;

    private List<Square> mTetraminoList;
    private Display mDisplay;
    private int square_count = 0;
    private float[] color = {0.0f, 0.0f, 1.0f, 1.0f};
    private Random random;
    private static int id = 0;

    Tetramino(Display display, Type type)
    {
        mDisplay = display;
        mTetraminoList = new ArrayList<>();
        random = new Random();
        randomColor();
        square_count = 4;
        id += 1;

        switch (type) {
            case tLineHorizontal: makeLineHorizontal(this); break;
            case tLineVertical: makeLineVertical(this); break;
            case tSquare: makeSquare(this); break;
            case tT: makeT(this); break;
        }
    }

    private void randomColor()
    {
        color[0] = random.nextFloat();
        color[1] = random.nextFloat();
        color[2] = random.nextFloat();
    }

    private static void makeLineHorizontal(Tetramino tetramino)
    {
        Square current, prev;
        int leftPos = tetramino.random.nextInt(tetramino.mDisplay.getWidth() - tetramino.square_count * SQ_SIZE);
        tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
        current = tetramino.mTetraminoList.get(0);
        current.setBounds(leftPos, 1);

        for(int i = 1; i < tetramino.square_count; i++)
        {
            tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
            prev = current;
            current = tetramino.mTetraminoList.get(i);
            current.setBounds(prev.getRight(), prev.getTop());
        }
    }

    private static void makeLineVertical(Tetramino tetramino)
    {
        Square current, prev;
        int leftPos = tetramino.random.nextInt(tetramino.mDisplay.getWidth() - SQ_SIZE);
        tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
        current = tetramino.mTetraminoList.get(0);
        current.setBounds(leftPos, 1);

        for(int i = 1; i < tetramino.square_count; i++)
        {
            tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
            prev = current;
            current = tetramino.mTetraminoList.get(i);
            current.setBounds(leftPos, prev.getTop() - SQ_SIZE);
        }
    }

    private static void makeSquare(Tetramino tetramino)
    {
        Square current, prev;
        int leftPos = tetramino.random.nextInt(tetramino.mDisplay.getWidth() - SQ_SIZE * 2);
        tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
        current = tetramino.mTetraminoList.get(0);
        current.setBounds(leftPos, 1);

        for(int i = 1; i < tetramino.square_count; i++)
        {
            tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
            prev = current;
            current = tetramino.mTetraminoList.get(i);

            switch (i) {
                case 1:
                    current.setBounds(prev.getRight(), prev.getTop());
                    break;
                case 2:
                    current.setBounds(leftPos, prev.getBottom());
                    break;
                case 3:
                    current.setBounds(prev.getRight(), prev.getTop());
            }
        }
    }

    private static void makeT(Tetramino tetramino)
    {
        Square current, prev;
        int leftPos = tetramino.random.nextInt(tetramino.mDisplay.getWidth() - SQ_SIZE * 3);
        tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
        current = tetramino.mTetraminoList.get(0);
        current.setBounds(leftPos, 1);

        for(int i = 1; i < tetramino.square_count; i++)
        {
            tetramino.mTetraminoList.add(new Square(tetramino.mDisplay));
            prev = current;
            current = tetramino.mTetraminoList.get(i);

            switch (i) {
                case 1:
                    current.setBounds(prev.getRight(), prev.getTop());
                    break;
                case 2:
                    current.setBounds(prev.getLeft(), prev.getBottom());
                    break;
                case 3:
                    current.setBounds(prev.getRight(), prev.getTop() - SQ_SIZE);
            }
        }
    }

    void moveDown()
    {
        Square current;
        for(int i = 0; i < square_count; i++)
        {
            current = mTetraminoList.get(i);
            current.incTop(DOWN_DELTA);
        }
    }

    void moveLeft()
    {
        Square current;
        for(int i = 0; i < square_count; i++)
        {
            current = mTetraminoList.get(i);
            current.decLeft(MOVE_DELTA);
        }
    }

    void moveRight()
    {
        Square current;
        for(int i = 0; i < square_count; i++)
        {
            current = mTetraminoList.get(i);
            current.incLeft(MOVE_DELTA);
        }
    }

    float[] getVertices()
    {
       float[] vertices = new float[VERTEX_SIZE * square_count];
       Square current;
       int k = 0;
       for(int i = 0; i < square_count; i++)
       {
           current = mTetraminoList.get(i);
           float[] vert = current.getVertices();
           for(int j = 0; j < VERTEX_SIZE; j++)
           {
               vertices[k] = vert[j];
               k++;
           }
       }
       return vertices;
    }

    float[] getColor(){return color;}

    public void setColor(float[] value){color = value;}

     int getSquare_count(){return square_count;}

    Square getSquare(int index){return mTetraminoList.get(index);}

    List<Square> getSquareList(){ return mTetraminoList;}

    int getId(){return id;}

}
