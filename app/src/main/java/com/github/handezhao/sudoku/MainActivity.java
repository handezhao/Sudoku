package com.github.handezhao.sudoku;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String INTENT_GAME_MODEL = "game_model";
    public static final String INTENT_GAME_DIFFICULTY = "game_difficulty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_about).setOnClickListener(this);
        findViewById(R.id.tv_continue).setOnClickListener(this);
        findViewById(R.id.tv_new_game).setOnClickListener(this);
        findViewById(R.id.tv_exit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_new_game:
                new AlertDialog.Builder(this)
                        .setTitle("请选择难度")
                        .setSingleChoiceItems(new String[]{"普通", "高手", "噩梦", "地狱"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("难度", i + "");
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, SudokuActivity.class);
                                intent.putExtra(INTENT_GAME_MODEL, 1);
                                intent.putExtra(INTENT_GAME_DIFFICULTY, i);
                                startActivity(intent);
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.tv_continue:
                Intent continueIntent = new Intent();
                continueIntent.putExtra(INTENT_GAME_MODEL, 2);
                continueIntent.setClass(this, SudokuActivity.class);
                startActivity(continueIntent);
                break;
            case R.id.tv_about:
                toast("A demo!");
                break;
            case R.id.tv_exit:
                System.exit(1);
                break;
            default:
                break;
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
