package sudo.hdz.com.sudoku;

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

/**
 * Description: a Sudoku demo
 * I want to use a user-defined view to show
 * a Sudoku.
 * <p>
 * Created by hdz on 17/05/2018.
 */

public class SudokuView extends View {

    public static final String TAG = "SudokuView";

    /**
     * 外边框的宽度
     */
    private float outlineWidth = 0;

    /**
     * 外边框线的颜色
     */
    private int outlineColor = 0x000;

    /**
     * 内框线的颜色
     */
    private int inlineColor = 0x000;

    /**
     * 内框的宽度
     */
    private float inlineWidth = 0;

    /**
     * 小方格的边长
     */
    private float sideLength;


    private Paint outlinePaint;
    private Paint inlinePaint;
    private Paint textPaint;
    private Paint fillPaint;

    /**
     * 防止四条边框只显示一半，需要draw line的时候偏移的量
     */
    private float drawOffset = 0f;

    /**
     * 写数字的时候为了写在正中央时引入的FontMetrics
     */
    private Paint.FontMetrics textMetrics;

    /**
     * 当前选择的格子
     */
    private int[] selectedPosition = new int[2];

    private boolean selected = false;

    private OnSetNumberListener onSetNumberListener;

    private int[][] sudoku = new int[9][9];


    public SudokuView(Context context) {
        this(context, null, 0);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
        initPant();
    }

    private void initConfig(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SudokuView);
        outlineWidth = typedArray.getDimension(R.styleable.SudokuView_outline_width, 30.0f);
        outlineColor = typedArray.getColor(R.styleable.SudokuView_outline_color, 0xfff);
        inlineWidth = typedArray.getDimension(R.styleable.SudokuView_inline_width, 20.0f);
        inlineColor = typedArray.getColor(R.styleable.SudokuView_inline_color, 0xfff);
        typedArray.recycle();
    }

    private void initPant() {
        outlinePaint = new Paint();
        outlinePaint.setColor(outlineColor);
        outlinePaint.setAntiAlias(true);
        outlinePaint.setStrokeWidth(outlineWidth);

        inlinePaint = new Paint();
        inlinePaint.setColor(inlineColor);
        inlinePaint.setAntiAlias(true);
        inlinePaint.setStrokeWidth(inlineWidth);

        drawOffset = outlinePaint.getStrokeWidth() / 2.0f;

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#454545"));
        textPaint.setTextSize(40);
        textMetrics = textPaint.getFontMetrics();

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(Color.parseColor("#C4EADA"));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // do not set padding!!!
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int resultLength = Math.min(width, height);
        sideLength = resultLength / 9.0f;
        setMeasuredDimension(resultLength, resultLength);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }


    /**
     * 画水平的外框线
     *
     * @param canvas
     */
    private void drawHorizonOutline(Canvas canvas) {
        //第一条和最后一条线需要偏移
        canvas.drawLine(0, drawOffset, sideLength * 9, drawOffset, outlinePaint);
        for (int i = 1; i < 3; i++) {
            canvas.drawLine(0, i * 3 * sideLength, sideLength * 9, i * 3 * sideLength,
                    outlinePaint);
        }
        canvas.drawLine(0, sideLength * 9 - drawOffset, sideLength * 9, sideLength * 9 -
                drawOffset, outlinePaint);
    }

    /**
     * 画垂直的外框线
     *
     * @param canvas
     */
    private void drawVerticalOutline(Canvas canvas) {
        //第一条和最后一条线需要偏移
        canvas.drawLine(drawOffset, drawOffset * 2, drawOffset, sideLength * 9 - drawOffset * 2,
                outlinePaint);
        for (int i = 1; i < 3; i++) {
            canvas.drawLine(i * 3 * sideLength - drawOffset * 2, 0, i * 3 * sideLength,
                    sideLength * 9 - drawOffset * 2, outlinePaint);
        }
        canvas.drawLine(sideLength * 9 - drawOffset, drawOffset * 2, sideLength * 9 - drawOffset,
                sideLength * 9 - drawOffset * 2, outlinePaint);
    }

    /**
     * 画水平的内框线
     *
     * @param canvas
     */
    private void drawHorizonInline(Canvas canvas) {
        for (int i = 1; i < 9; i++) {
            if (i % 3 == 0) continue;
            canvas.drawLine(0, sideLength * i, sideLength * 9, sideLength * i, inlinePaint);
        }

    }

    /**
     * 画垂直的内框线
     *
     * @param canvas
     */
    private void drawVerticalInline(Canvas canvas) {
        for (int i = 1; i < 9; i++) {
            if (i % 3 == 0) continue;
            canvas.drawLine(sideLength * i, 0, sideLength * i, sideLength * 9, inlinePaint);
        }
    }

    /**
     * 写入数字
     *
     * @param canvas
     * @param number
     * @param startX 对应方格的左侧x坐标值
     * @param startY 对应方格的上侧y坐标值
     */
    private void drawNumber(Canvas canvas, int number, float startX, float startY) {
        float drawY = startY + sideLength / 2 + (Math.abs(textMetrics.ascent) - textMetrics
                .descent) / 2;

        float drawX = startX + sideLength / 2 - textPaint.measureText(String.valueOf(number)) / 2;

        canvas.drawText(String.valueOf(number), drawX, drawY, textPaint);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                selected = true;
                selectedPosition[0] = (int) (event.getX() / sideLength);
                selectedPosition[1] = (int) (event.getY() / sideLength);
                Log.d(TAG, "onTouchEvent x = " + selectedPosition[0] + ", y = " +
                        selectedPosition[1]);

                if (onSetNumberListener != null) {
                    onSetNumberListener.onSetNumber(selectedPosition[0], selectedPosition[1]);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                selected = false;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSelectedColor(canvas);
        drawSudoku(canvas);
        drawHorizonInline(canvas);
        drawVerticalInline(canvas);
        drawHorizonOutline(canvas);
        drawVerticalOutline(canvas);
    }

    private void drawSudoku(Canvas canvas) {
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                if (sudoku[i][j] > 0) {
                    drawSetColor(canvas, i, j);
                    drawNumber(canvas, sudoku[i][j], i * sideLength, j * sideLength);
                }
            }
        }
    }

    private void drawSelectedColor(Canvas canvas) {
        if (!selected) return;
        drawSetColor(canvas, selectedPosition[0], selectedPosition[1]);
    }

    private void drawSetColor(Canvas canvas, int x, int y) {
        canvas.drawRect(x * sideLength, y * sideLength, (x + 1) * sideLength, (y + 1) * sideLength,
                fillPaint);
    }


    public void setOnSetNumberListener(OnSetNumberListener onSetNumberListener) {
        this.onSetNumberListener = onSetNumberListener;
    }

    /**
     * 填入具体位置的一个数
     * @param x
     * @param y
     * @param number
     */
    public void setNumber(int x, int y, int number) {
        sudoku[x][y] = number;
    }

    /**
     * 初始化数独
     * @param array
     */
    public void initSudoku(int[][] array) {
        this.sudoku = array;
        invalidate();
    }
}
