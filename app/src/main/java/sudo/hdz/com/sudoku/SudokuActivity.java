package sudo.hdz.com.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import sudo.hdz.com.sudoku.callback.OnNumberPickListener;
import sudo.hdz.com.sudoku.callback.OnSudokuSelectedListener;
import sudo.hdz.com.sudoku.utils.Constant;
import sudo.hdz.com.sudoku.utils.SFHelper;
import sudo.hdz.com.sudoku.widget.SudokuView;
import sudo.hdz.com.sudoku.widget.ToolView;

public class SudokuActivity extends AppCompatActivity implements OnSudokuSelectedListener,
        OnNumberPickListener {
    public static final String TAG = "SudokuActivity";

    private SudokuView sudokuView;
    private ToolView toolView;
    private int[][] sudoku;
    private int[] selectedPosition = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        Intent intent = getIntent();
        int model = intent.getIntExtra(MainActivity.INTENT_GAME_MODEL, 1);
        sudokuView = findViewById(R.id.sudoku_view);
        sudokuView.setOnSudokuSelectedListener(this);
        toolView = findViewById(R.id.tool_view);
        toolView.setOnNumberPickListener(this);
        sudokuView.registerPossibleNumberListener(toolView);
        sudoku = new int[][]{
                {2, 3, 0, 0, 0, 0, 1, 0, 0},
                {1, 8, 0, 0, 2, 4, 0, 9, 0},
                {0, 0, 0, 0, 6, 0, 8, 0, 2},
                {0, 0, 0, 0, 0, 6, 0, 5, 0},
                {0, 0, 8, 0, 0, 0, 3, 2, 0},
                {0, 4, 0, 2, 0, 0, 0, 1, 0},
                {7, 0, 9, 0, 3, 0, 0, 0, 0},
                {0, 2, 0, 5, 0, 0, 0, 0, 1},
                {0, 0, 1, 0, 0, 0, 0, 0, 7}
        };

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
