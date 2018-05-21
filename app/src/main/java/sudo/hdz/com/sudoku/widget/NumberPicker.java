package sudo.hdz.com.sudoku.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import sudo.hdz.com.sudoku.R;
import sudo.hdz.com.sudoku.callback.OnNumberPickListener;

/**
 * Description:
 * Created by hdz on 18/05/2018.
 */

public class NumberPicker extends View {

    public static final String TAG = "NumberPicker";
    /**
     * 待选择的数据
     */
    private int[][] numbers = new int[][]{
            {1, 4, 7},
            {2, 5, 8},
            {3, 6, 9}
    };

    private float singleSide = 0;

    private Paint linePaint;

    private int lineColor;

    private float lineWidth;

    private float offset;

    private Paint numberPaint;
    private Paint.FontMetrics textMetrics;

    private boolean pressed = false;

    private OnNumberPickListener onNumberPickListener;
    private int[] selectedPosition = new int[2];

    private Paint pressPaint;
    private float pressLeft;
    private float pressTop;
    private float pressRight;
    private float pressBottom;

    private float numberSize = 50;
    private int pressColor = 0xD7FBE8;
    private int numberColor = 0x000;

    public NumberPicker(Context context) {
        this(context, null, 0);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs == null) return;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker);
        lineColor = array.getColor(R.styleable.NumberPicker_line_color, 0x000);
        lineWidth = array.getDimension(R.styleable.NumberPicker_line_width, 10.0f);
        numberSize = array.getDimension(R.styleable.SudokuView_number_size, 50.0f);
        numberColor = array.getColor(R.styleable.SudokuView_number_color, 0x000);
        pressColor = array.getColor(R.styleable.SudokuView_fill_color, 0xD7FBE8);
        array.recycle();
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);
        offset = linePaint.getStrokeWidth() / 2;

        numberPaint = new Paint();
        numberPaint.setAntiAlias(true);
        numberPaint.setColor(numberColor);
        numberPaint.setTextSize(numberSize);
        textMetrics = numberPaint.getFontMetrics();

        pressPaint = new Paint();
        pressPaint.setColor(pressColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int result = Math.min(width, height);
        singleSide = result / 3.0f;
        setMeasuredDimension(result, result);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHorizonLine(canvas);
        drawVerticalLine(canvas);
        drawSelected(canvas);
        drawNumber(canvas);
    }

    private void drawHorizonLine(Canvas canvas) {
        canvas.drawLine(0, offset, singleSide * 3, offset, linePaint);
        canvas.drawLine(0, singleSide, singleSide * 3, singleSide, linePaint);
        canvas.drawLine(0, singleSide * 2, singleSide * 3, singleSide * 2, linePaint);
        canvas.drawLine(0, singleSide * 3 - offset, singleSide * 3, singleSide * 3 - offset,
                linePaint);
    }

    private void drawVerticalLine(Canvas canvas) {
        canvas.drawLine(offset, offset, offset, singleSide * 3 - offset, linePaint);
        canvas.drawLine(singleSide, offset, singleSide, singleSide * 3 - offset, linePaint);
        canvas.drawLine(singleSide * 2, offset, singleSide * 2, singleSide * 3 - offset, linePaint);
        canvas.drawLine(singleSide * 3 - offset, offset, singleSide * 3 - offset, singleSide * 3
                - offset, linePaint);
    }

    private void drawNumber(Canvas canvas) {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                drawNumber(canvas, numbers[i][j], i * singleSide, j * singleSide);
            }
        }
    }

    private void drawNumber(Canvas canvas, int number, float startX, float startY) {
        float drawY = startY + singleSide / 2 + (Math.abs(textMetrics.ascent) - textMetrics
                .descent) / 2;
        float drawX = startX + singleSide / 2 - numberPaint.measureText(String.valueOf(number)) / 2;
        canvas.drawText(String.valueOf(number), drawX, drawY, numberPaint);
    }

    private void drawSelected(Canvas canvas) {
        if (!pressed) return;
        pressLeft = selectedPosition[0] * singleSide + offset;
        pressTop = selectedPosition[1] * singleSide + offset;
        pressRight = (selectedPosition[0] + 1) * singleSide - offset;
        pressBottom = (selectedPosition[1] + 1) * singleSide - offset;
        canvas.drawRect(pressLeft, pressTop, pressRight, pressBottom, pressPaint);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "action is " + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                pressed = true;
                selectedPosition[0] = (int) (event.getX() / singleSide);
                selectedPosition[1] = (int) (event.getY() / singleSide);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                pressed = false;
                if (onNumberPickListener != null &&
                        numbers[selectedPosition[0]][selectedPosition[1]] > 0) {
                    onNumberPickListener.onNumberPick
                            (numbers[selectedPosition[0]][selectedPosition[1]]);
                }
                invalidate();
                break;
        }
        return true;
    }

    public void setOnNumberPickListener(OnNumberPickListener onNumberPickListener) {
        this.onNumberPickListener = onNumberPickListener;
    }
}
