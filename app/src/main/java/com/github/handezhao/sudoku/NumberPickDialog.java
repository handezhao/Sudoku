package com.github.handezhao.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.github.handezhao.sudoku.callback.OnNumberPickListener;
import com.github.handezhao.sudoku.callback.PickNumberCallback;
import com.github.handezhao.sudoku.widget.NumberPicker;

import androidx.annotation.NonNull;

/**
 * Description:
 * Created by hdz on 17/05/2018.
 */

public class NumberPickDialog extends Dialog implements OnNumberPickListener {

    private PickNumberCallback callback;

    public NumberPickDialog(@NonNull Context context) {
        super(context, R.style.PickNumberDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_number_pick);
        NumberPicker numberPicker = findViewById(R.id.picker);
        numberPicker.setOnNumberPickListener(this);
    }

    public void setNumberCallback(PickNumberCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onNumberPick(int number) {
        if (callback != null) {
            callback.onNumberPicked(number);
        }
        dismiss();
    }
}
