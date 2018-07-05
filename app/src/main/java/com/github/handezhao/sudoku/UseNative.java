package com.github.handezhao.sudoku;

/**
 * Created by jingweiwang on 2018/7/3.
 */

public class UseNative {
    static {
        System.loadLibrary("sudoku");
    }

    public static native int[][] createNewSoduku();

    public static native int[][] createNewTwoDimensionalArray(int x, int y);
}
