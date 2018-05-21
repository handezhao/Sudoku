package sudo.hdz.com.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import sudo.hdz.com.sudoku.callback.OnSetNumberListener;
import sudo.hdz.com.sudoku.callback.PickNumberCallback;
import sudo.hdz.com.sudoku.utils.Constant;
import sudo.hdz.com.sudoku.utils.SFHelper;
import sudo.hdz.com.sudoku.widget.SudokuView;

public class SudokuActivity extends AppCompatActivity implements OnSetNumberListener {

    private SudokuView sudokuView;
    private int[][] sudoku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        Intent intent = getIntent();
        int model = intent.getIntExtra(MainActivity.INTENT_GAME_MODEL, 1);
        sudokuView = findViewById(R.id.sudoku_view);
        sudokuView.setOnSetNumberListener(this);
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
    public void onSetNumber(final int x, final int y) {
        NumberPickDialog dialog = new NumberPickDialog(this);
        dialog.setNumberCallback(new PickNumberCallback() {
            @Override
            public void onNumberPicked(int number) {
                sudokuView.setNumber(x, y, number);
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void onReset(int x, int y) {
        sudokuView.setNumber(x, y, 0);
    }
}
