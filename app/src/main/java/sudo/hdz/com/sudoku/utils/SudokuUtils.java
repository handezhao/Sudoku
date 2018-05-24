package sudo.hdz.com.sudoku.utils;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * Created by hdz on 24/05/2018.
 */

public class SudokuUtils {

    public static final String TAG = "SudokuUtils-SudokuView";

    private SudokuUtils() {
    }

    /**
     * judge a number is valid
     *
     * @param number
     * @param x
     * @param y
     * @param sudoku
     * @return
     */
    public boolean isValidNumber(int number, int x, int y, int[][] sudoku) {
        Set<Integer> row = new HashSet<>();
        Set<Integer> colume = new HashSet<>();
        Set<Integer> squal = new HashSet<>();

        Log.d(TAG, "x=" + x + ", y=" + y);
        for (int i = 0; i < 9; i++) {
            if (sudoku[x][i] != 0) {
                colume.add(sudoku[x][i]);
            }
            if (sudoku[i][y] != 0) {
                row.add(sudoku[i][y]);
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (sudoku[x / 3 * 3 + i][y / 3 * 3 + j] != 0 ) {
                    squal.add(sudoku[x / 3 * 3 + i][y / 3 * 3 + j]);
                }
            }
        }
        return !colume.contains(number) && !row.contains(number) && !squal.contains(number);
    }


    /**
     * get possible numbers of selected position
     * @param x
     * @param y
     * @param sudoku
     * @return
     */
    public Set<Integer> getPossibleNUmber(int x, int y, int[][] sudoku) {
        Set<Integer> row = new HashSet<>();
        Set<Integer> colume = new HashSet<>();
        Set<Integer> squal = new HashSet<>();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            if (sudoku[x][i] != 0) {
                colume.add(sudoku[x][i]);
            }
            if (sudoku[i][y] != 0) {
                row.add(sudoku[i][y]);
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (sudoku[x / 3 * 3 + i][y / 3 * 3 + j] != 0 ) {
                    squal.add(sudoku[x / 3 * 3 + i][y / 3 * 3 + j]);
                }
            }
        }
        for (int i = 1; i < 10; i++) {
            if (row.contains(i) || colume.contains(i) || squal.contains(i)) {
                Log.d(TAG, "not a valid number!");
            } else {
                set.add(i);
            }
        }
        return set;
    }


    public static SudokuUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SudokuUtils INSTANCE = new SudokuUtils();
    }
}
