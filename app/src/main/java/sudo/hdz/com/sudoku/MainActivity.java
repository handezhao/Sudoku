package sudo.hdz.com.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String INTENT_GAME_MODEL = "game_model";


    Unbinder unbinder;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    @BindView(R.id.tv_continue)
    TextView tvContinue;
    @BindView(R.id.tv_new_game)
    TextView tvNewGame;
    @BindView(R.id.tv_exit)
    TextView tvExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        init();
    }

    private void init() {
        tvAbout.setOnClickListener(this);
        tvContinue.setOnClickListener(this);
        tvNewGame.setOnClickListener(this);
        tvExit.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_new_game:
                Intent intent = new Intent();
                intent.setClass(this, SudokuActivity.class);
                intent.putExtra(INTENT_GAME_MODEL, 1);
                startActivity(intent);
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
