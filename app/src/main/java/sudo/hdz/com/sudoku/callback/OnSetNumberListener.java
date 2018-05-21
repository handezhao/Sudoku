package sudo.hdz.com.sudoku.callback;

/**
 * Description:
 * Created by hdz on 17/05/2018.
 */

public interface OnSetNumberListener {
    void onSetNumber(int x, int y);
    void onReset(int x, int y);
}
