package sudo.hdz.com.sudoku;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import sudo.hdz.com.sudoku.callback.OnSetNumberListener;
import sudo.hdz.com.sudoku.callback.PickNumberCallback;
import sudo.hdz.com.sudoku.widget.SudokuView;

public class SudokuActivity extends AppCompatActivity implements OnSetNumberListener {

    private SudokuView sudokuView;
    private int[][] sudoku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
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
        sudokuView.initSudoku(sudoku);
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


}
