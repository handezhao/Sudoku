package com.github.handezhao.sudoku;

import com.github.handezhao.sudoku.exception.ExceptionHandler;
import com.github.handezhao.sudoku.utils.Constant;
import com.github.handezhao.sudoku.utils.SFHelper;

import java.io.File;

import androidx.multidex.MultiDexApplication;

/**
 * Description:
 * Created by hdz on 18/05/2018.
 */

public class SudokuApp extends MultiDexApplication {

    public static SudokuApp sudokuApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sudokuApp = this;
        init();
    }

    private void init() {
        SFHelper.getInstance().initSharedPreference();
        createDirectory();
        ExceptionHandler.getInstance().setDirectory(Constant.PATH_EXCEPTION);
        ExceptionHandler.getInstance().start();
    }

    private void createDirectory() {
        File file = new File(Constant.APPLICATION_ROOT);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constant.PATH_EXCEPTION);
        if (!file.exists()) {
            file.mkdir();
        }
    }

}
