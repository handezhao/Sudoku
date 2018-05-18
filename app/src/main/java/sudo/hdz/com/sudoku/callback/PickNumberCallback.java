package sudo.hdz.com.sudoku.callback;

/**
 * Description:
 * Created by hdz on 17/05/2018.
 */

public interface PickNumberCallback {
    void onNumberPicked(int number);
    void onCancel();
}
