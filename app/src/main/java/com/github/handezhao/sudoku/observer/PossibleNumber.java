package com.github.handezhao.sudoku.observer;

import java.util.Set;

/**
 * Description:
 * Created by hdz on 24/05/2018.
 */

public interface PossibleNumber {
    void notifyToolView(Set<Integer> possible);
    void registerPossibleNumberListener(PossibleNumberWatcher watcher);
    void unRegisterPossibleNumberListener(PossibleNumberWatcher watcher);
}
