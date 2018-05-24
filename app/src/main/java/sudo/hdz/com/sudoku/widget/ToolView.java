package sudo.hdz.com.sudoku.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import sudo.hdz.com.sudoku.R;
import sudo.hdz.com.sudoku.callback.OnNumberPickListener;
import sudo.hdz.com.sudoku.observer.PossibleNumberWatcher;

/**
 * Description:
 * Created by hdz on 22/05/2018.
 */

public class ToolView extends View implements PossibleNumberWatcher {

    public static final String TAG = "ToolView";

    private float singleHeight;
    private float singleWidth;

    private Paint textPaint;
    private Paint.FontMetrics textMetrics;

    private float numberSize;
    private int numberColor;
    private int fillColor;

    private Paint fillPaint;

    private OnNumberPickListener onNumberPickListener;
    private boolean selected = false;

    private int[] select = new int[2];
    private int[][] options = new int[][]{
            {1, 6},
            {2, 7},
            {3, 8},
            {4, 9},
            {5, 0}
    };

    private Set<Integer> possibleNumber = new HashSet<>();
    private static final float INSETX = 10.0f;
    private static final float INSETY = 2.0f;

    public ToolView(Context context) {
        this(context, null, 0);
    }

    public ToolView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ToolView);
        numberColor = array.getColor(R.styleable.ToolView_number_color, 0x000);
        numberSize = array.getDimension(R.styleable.ToolView_number_size, 50);
        fillColor = array.getColor(R.styleable.ToolView_fill_color, 0xf0f0f0);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setTextSize(numberSize);
        textPaint.setColor(numberColor);
        textPaint.setAntiAlias(true);
        textMetrics = textPaint.getFontMetrics();

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        singleWidth = width / 5.0f;
        singleHeight = height / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPressRect(canvas, select[0], select[1]);
        drawPossibleArea(canvas);
        drawOptions(canvas);
    }

    private void drawOptions(Canvas canvas) {
        for (int i = 0; i < options.length; i++) {
            for (int j = 0; j < options[i].length; j++) {
                drawText(canvas, i * singleWidth, j * singleHeight, options[i][j]);
            }
        }
    }

    private void drawText(Canvas canvas, float startX, float startY, int number) {
        float drawY = startY + singleHeight / 2 + (Math.abs(textMetrics.ascent) - textMetrics
                .descent) / 2;
        float drawX = startX + singleWidth / 2 - textPaint.measureText(String.valueOf(number)) / 2;
        canvas.drawText(String.valueOf(number), drawX, drawY, textPaint);
    }

    @TargetApi(21)
    private void drawPressRect(Canvas canvas, int x, int y) {
        if (!selected) return;
        if (x < 0 || y < 0 || x > 4 || y > 1) return;
        fillPaint.setColor(fillColor);
        canvas.drawRoundRect(x * singleWidth + INSETX, y * singleHeight + INSETY, (x + 1) *
                singleWidth - INSETX, (y + 1) * singleHeight - INSETY, 20.0f, 20.0f, fillPaint);
    }

    private void drawPossibleArea(Canvas canvas) {
        if (possibleNumber.isEmpty()) return;
        for (int item : possibleNumber) {
            drawPossibleReact(canvas, getPositionByNumber(item)[0], getPositionByNumber(item)[1]);
        }
    }

    @TargetApi(21)
    private void drawPossibleReact(Canvas canvas, int x, int y) {
        if (selected) return;
        if (x < 0 || y < 0 || x > 4 || y > 1) return;
        fillPaint.setColor(Color.parseColor("#97de95"));
        canvas.drawRoundRect(x * singleWidth + INSETX, y * singleHeight + INSETY, (x + 1) *
                singleWidth - INSETX, (y + 1) * singleHeight - INSETY, 20.0f, 20.0f, fillPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                select[0] = (int) ((event.getX() - getPaddingLeft()) / singleWidth);
                select[1] = (int) ((event.getY() - getPaddingTop()) / singleHeight);
                if (isUnableArea(select[0], select[1])) {
                    Log.d(TAG, "unable area");
                    return false;
                }
                selected = true;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (onNumberPickListener != null) {
                    onNumberPickListener.onNumberPick(options[select[0]][select[1]]);
                }
                selected = false;
                invalidate();
                break;
        }
        return true;
    }

    public void setOnNumberPickListener(OnNumberPickListener onNumberPickListener) {
        this.onNumberPickListener = onNumberPickListener;
    }

    @Override
    public void onPossibleNumberChanged(Set<Integer> possibleNumber) {
        Log.d(TAG, "onPossibleNumberChanged " + possibleNumber);
        this.possibleNumber = possibleNumber;
        invalidate();
    }

    private boolean isUnableArea(int x, int y) {
        if (x == 4 && y == 1) return false; // zero always enable
        if (possibleNumber.isEmpty()) return false;
        for (int item : possibleNumber) {
            int[] pos = getPositionByNumber(item);
            if (pos[0] == x && pos[1] == y) {
                return false;
            }
        }
        return true;
    }

    private int[] getPositionByNumber(int number) {
        int[] pos = new int[2];
        if (number == 0) {
            pos[0] = 4;
            pos[1] = 1;
        } else if (number < 6) {
            pos[0] = number - 1;
            pos[1] = number / 6;
        } else {
            pos[0] = number - 6;
            pos[1] = number / 6;
        }
        return pos;
    }
}
