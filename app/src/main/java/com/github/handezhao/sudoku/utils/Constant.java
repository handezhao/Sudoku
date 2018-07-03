package com.github.handezhao.sudoku.utils;

import android.os.Environment;

import java.io.File;

/**
 * Description:
 * Created by hdz on 18/05/2018.
 */

public class Constant {

    public static final String LAST_GAME_HISTORY = "last_game_history";

    public static final String LAST_GAME_ORIGIN = "last_game_origin";

    public static final String APPLICATION_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Sudoku";
    public static final String PATH_EXCEPTION = APPLICATION_ROOT + "/exception/";

}
