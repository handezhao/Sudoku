package sudo.hdz.com.sudoku.widget;

import android.annotation.TargetApi;
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
 * Created by hdz on 22/05/2018.
 */

public class ToolView extends View {

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
        fillPaint.setColor(fillColor);
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        Log.d(TAG, "width is " + width + ", height is " + height);
        singleWidth = width / 5.0f;
        singleHeight = height / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPressRect(canvas, select[0], select[1]);
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
        canvas.drawRoundRect(x * singleWidth, y * singleHeight, (x + 1) * singleWidth, (y + 1) *
                singleHeight, 20.0f, 20.0f, fillPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                select[0] = (int) ((event.getX() - getPaddingLeft()) / singleWidth);
                select[1] = (int) ((event.getY() - getPaddingTop()) / singleHeight);
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
}
