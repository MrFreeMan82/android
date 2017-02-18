package home.tetris;

import android.graphics.Rect;
import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Дима on 17.02.2017.
 *
 */

//To-Do Добавить синхронизацию когда удаляется несколько линий

class DeleteLineAnimation {

    private ArrayList<Tetramino> tetraminosAtLine;
    private Callback callback;

    private static int doneCounter = 0;

    interface Callback{
        void onDeleteBlock(Tetramino tetramino);
        void onDeleteComplete(int line);
        void onBeginDelete();
    }

    private void findAtLine(List<Tetramino> sceneList, int line){

        for(Tetramino tetramino: sceneList){

            for(Rect block: tetramino.getBlocks()){

                if(block != null && block.bottom == line) {
                    tetraminosAtLine.add(tetramino);
                    break;
                }
            }
        }
    }

    private class CustomComparator implements Comparator<Tetramino>{

        @Override
        public int compare(Tetramino a, Tetramino b){

            return (a.getMinLeft() > b.getMinLeft())? 1: -1;
        }

        @Override
        public boolean equals(Object obj){
            return false;
        }
    }

    DeleteLineAnimation(List<Tetramino> sceneList, Callback aCallback, final int line){

        tetraminosAtLine = new ArrayList<>();
        callback = aCallback;

        findAtLine(sceneList, line);

        Collections.sort(tetraminosAtLine, new CustomComparator());

        if(tetraminosAtLine.isEmpty()) return;

        new CountDownTimer(Long.MAX_VALUE, 30){

            @Override
            public void onFinish() {}

            @Override
            public void onTick(long millisUntilFinished){

                for(Tetramino tetramino: tetraminosAtLine){

                    for(Rect block: tetramino.getBlocks()) {

                        if(block != null && block.bottom == line) {
                            if(doneCounter == 0) callback.onBeginDelete();
                            tetramino.replaceBlock(null, block);
                            callback.onDeleteBlock(tetramino);
                            doneCounter++;
                            if(doneCounter == Globals.BLOCKS_PER_WIDTH){
                                doneCounter = 0;
                                callback.onDeleteComplete(line);
                                this.cancel();
                            }
                            return;
                        }
                    }
                }
            }
        }.start();
    }
}
