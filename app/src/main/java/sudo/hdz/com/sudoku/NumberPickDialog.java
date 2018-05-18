package sudo.hdz.com.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Description:
 * Created by hdz on 17/05/2018.
 */

public class NumberPickDialog extends Dialog implements View.OnClickListener {

    private PickNumberCallback callback;
    private EditText editText;
    private Button buttonSure;
    private Button buttonCancel;
    private int pick;

    public NumberPickDialog(@NonNull Context context) {
        super(context, R.style.PickNumberDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_number_pick);
        editText = findViewById(R.id.et_number);
        buttonCancel = findViewById(R.id.bt_cancel);
        buttonSure = findViewById(R.id.bt_sure);

        buttonSure.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pick = Integer.valueOf(s.toString());
            }
        });
    }

    public void setNumberCallback(PickNumberCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_cancel:
                if (callback != null) {
                    callback.onCancel();
                }
                dismiss();
                break;
            case R.id.bt_sure:
                if (callback != null) {
                    if (pickIsLegal()) {
                        callback.onNumberPicked(pick);
                    } else {
                        callback.onCancel();
                    }
                }
                dismiss();
                break;
        }
    }

    private boolean pickIsLegal() {
        return pick > 0 && pick < 10;
    }
}
