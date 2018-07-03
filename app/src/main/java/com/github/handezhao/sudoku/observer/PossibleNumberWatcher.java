package com.github.handezhao.sudoku.observer;

import java.util.Set;

/**
 * Description:
 * Created by hdz on 24/05/2018.
 */

public interface PossibleNumberWatcher {
    void onPossibleNumberChanged(Set<Integer> possibleNumber);
}
