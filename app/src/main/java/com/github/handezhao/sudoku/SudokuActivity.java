package com.github.handezhao.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.handezhao.sudoku.callback.OnNumberPickListener;
import com.github.handezhao.sudoku.callback.OnSudokuSelectedListener;
import com.github.handezhao.sudoku.utils.Constant;
import com.github.handezhao.sudoku.utils.SFHelper;
import com.github.handezhao.sudoku.widget.SudokuView;
import com.github.handezhao.sudoku.widget.ToolView;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class SudokuActivity extends AppCompatActivity implements OnSudokuSelectedListener,
        OnNumberPickListener {
    public static final String TAG = "SudokuActivity";

    private SudokuView sudokuView;
    private ToolView toolView;
    private int[][] sudoku;
    private int[] selectedPosition = new int[2];

    private static void printSudoku(int[][] sudoku) {
        StringBuilder s = new StringBuilder();
        for (int[] aSudoku : sudoku) {
            for (int anASudoku : aSudoku) {
                s.append(anASudoku).append(" ");
//                System.out.print(anASudoku + " ");
            }
            Log.d("print", s.toString());
            s.setLength(0);
//            System.out.println();
        }
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        Intent intent = getIntent();
        final int model = intent.getIntExtra(MainActivity.INTENT_GAME_MODEL, 1);
        final int difficulty = intent.getIntExtra(MainActivity.INTENT_GAME_DIFFICULTY, 0);
        sudokuView = findViewById(R.id.sudoku_view);
        sudokuView.setOnSudokuSelectedListener(this);
        toolView = findViewById(R.id.tool_view);
        toolView.setOnNumberPickListener(this);
        sudokuView.registerPossibleNumberListener(toolView);
//        sudoku = new int[][]{
//                {2, 3, 0, 0, 0, 0, 1, 0, 0},
//                {1, 8, 0, 0, 2, 4, 0, 9, 0},
//                {0, 0, 0, 0, 6, 0, 8, 0, 2},
//                {0, 0, 0, 0, 0, 6, 0, 5, 0},
//                {0, 0, 8, 0, 0, 0, 3, 2, 0},
//                {0, 4, 0, 2, 0, 0, 0, 1, 0},
//                {7, 0, 9, 0, 3, 0, 0, 0, 0},
//                {0, 2, 0, 5, 0, 0, 0, 0, 1},
//                {0, 0, 1, 0, 0, 0, 0, 0, 7}
//        };
//        sudoku = UseNative.createNewTwoDimensionalArray(9, 9);
        sudoku = UseNative.createNewSoduku();
        sudoku = setDifficulty(sudoku, difficulty);

        if (model == 1) { // new game
            sudokuView.initSudoku(sudoku);
        } else {
            int[][] history = SFHelper.getInstance().getSudoku(Constant.LAST_GAME_HISTORY);
            if (history == null) {
                // TODO: 18/05/2018 start a new game
                sudokuView.initSudoku(sudoku);
            } else {
                int[][] origin = SFHelper.getInstance().getSudoku(Constant.LAST_GAME_ORIGIN);
                sudokuView.initSudoku(origin, history);
            }
        }
    }

    /**
     * 初始化难度设定
     *
     * @param sudoku     生成的完整数独
     * @param difficulty 难度等级, 0:每行2-5空, 1:每行3-6空, 2:每行4-7空, 3:每行5-8空
     * @return 挖完空的数独
     */
    private int[][] setDifficulty(int[][] sudoku, int difficulty) {
        int min = 0;
        int max = 0;
        switch (difficulty) {
            case 1:
                min = 3;
                max = 6;
                break;
            case 2:
                min = 4;
                max = 7;
                break;
            case 3:
                min = 5;
                max = 9;
                break;
            case 0:
            default:
                min = 2;
                max = 5;
                break;
        }
        for (int i = 0; i < sudoku.length; i++) {
            int hollowingOut = getRandom(min, max);
            while (hollowingOut != 0) {
                for (int j = 0; j < sudoku[i].length; j++) {
                    if (Math.random() < 0.5 && hollowingOut > 0) {
                        sudoku[i][j] = 0;
                        hollowingOut--;
                    }
                }
            }
        }
        return sudoku;
    }

    @Override
    public void onSelected(int[] position) {
//        selectedPosition = position;
        selectedPosition[0] = position[0];
        selectedPosition[1] = position[1];
    }

    @Override
    public void onNumberPick(int number) {
        Log.d(TAG, "pick number is " + number);
        sudokuView.setNumber(selectedPosition[0], selectedPosition[1], number);
    }

    @Override
    protected void onDestroy() {
        sudokuView.unRegisterPossibleNumberListener(toolView);
        super.onDestroy();
    }
}
