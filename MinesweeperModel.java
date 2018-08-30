package hu.ait.android.chau.minesweepergame.Model;

import java.util.Random;

/**
 * Created by Chau on 2/25/2015.
 */
public class MinesweeperModel {

    public static final int NUM_MINE = 3;
    public static final int NUM_COL = 5;
    public static final int NUM_ROW = 5;

    public static final int VM_UNCHECKED = -1;
    public static final int VM_MINE = -2;
    public static final int VM_FLAG = -3;
    private static MinesweeperModel instance = null;

    private MinesweeperModel() {
        placeMines();
    }

    public static MinesweeperModel getInstance() {
        if (instance == null) {
            instance = new MinesweeperModel();
        }
        return instance;
    }

    public static enum State {EMPTY, MINE, FLAG}

    public static enum Mode {TRYFIELD, PLACEFLAG}

    private Mode mode = Mode.TRYFIELD;
    private int foundCount = 0;

    private State[][] gameModel = {
            {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},
            {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},
            {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},
            {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY},
            {State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY, State.EMPTY}
    };

    // Each field contains a viewModel state or number of neighboring mines to be viewed by user.
    private int[][] viewModel = {
        {VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED},
        {VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED},
        {VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED},
        {VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED},
        {VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED, VM_UNCHECKED}
    };

    public State getModelContent(int i, int j) {
        return gameModel[i][j];
    }

    public void setModelContent(int i, int j, State content) {
        gameModel[i][j] = content;
    }

    public int getViewField(int i, int j) { return viewModel[i][j]; }

    public void setViewField(int i, int j, int val) { viewModel[i][j] = val; }

    public int countNeighboringMines(int i, int j) {
        short neighMines = 0;

        for (int xOffset = -1; xOffset < 2; xOffset++) {
            for (int yOffset = -1; yOffset < 2; yOffset++) {
                if (    (i+xOffset > -1 && i+xOffset < NUM_COL) &&
                        (j+yOffset > -1 && j+yOffset < NUM_ROW) &&
                        (gameModel[i+xOffset][j+yOffset] == State.MINE) ) {
                    neighMines++;
                }
            }
        }
        return neighMines;
    }

    public Mode getMode() { return mode; }

    public void toggleMode() { mode = (mode == Mode.TRYFIELD) ? Mode.PLACEFLAG : Mode.TRYFIELD; }

    public void resetGame() {
        resetModel();
        resetViewModel();
        foundCount = 0;
        mode = Mode.TRYFIELD;
    }

    private void resetModel() {
        for (int i = 0; i < NUM_COL; i++) {
            for (int j = 0; j < NUM_ROW; j++) {
                gameModel[i][j] = State.EMPTY;
            }
        }
        placeMines();
    }

    private void resetViewModel() {
        for (int i = 0; i < NUM_COL; i++) {
            for (int j = 0; j < NUM_ROW; j++) {
                viewModel[i][j] = VM_UNCHECKED;
            }
        }
    }

    private void placeMines() {
        int mineX, mineY;
        int counter = 0;
        Random randGen = new Random(System.currentTimeMillis());

        while (counter < NUM_MINE) {
            mineX = randGen.nextInt(NUM_COL);
            mineY = randGen.nextInt(NUM_ROW);
            if (gameModel[mineX][mineY] != State.MINE) {
                gameModel[mineX][mineY] = State.MINE;
                counter++;
            }
        }
    }

    public void revealMines() {
        for (int i = 0; i < NUM_COL; i++) {
            for (int j = 0; j < NUM_ROW; j++) {
                if (gameModel[i][j] == State.MINE && viewModel[i][j] != VM_FLAG) {
                    viewModel[i][j] = VM_MINE;
                }
            }
        }
    }

    public void incrementFoundCount() { foundCount++; }

    public boolean checkWinner() { return (foundCount == 3); }

}
