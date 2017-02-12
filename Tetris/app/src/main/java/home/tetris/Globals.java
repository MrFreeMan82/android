package home.tetris;

/**
 * Created by Дима on 04.02.2017.
 * Глобальные константы и переменные
 */

class Globals
{
    static final int BLOCKS_PER_WIDTH = 12;
    static final int MAX_BLOCK_CNT = 4;
    static final int SCORE_PER_LEVEL = 25;
    static int WIDTH, HEIGHT;    // Доступное пространство
    static int SQ_SIZE;         // Размер блока

    // Идентификаторы звука
    static final int IMPACT = 1;
    static final int LEVEL_UP = 2;
    static final int MOVE_MINO = 3;
    static final int ROTATE = 4;
    static final int DELETE_LINE = 5;
}
