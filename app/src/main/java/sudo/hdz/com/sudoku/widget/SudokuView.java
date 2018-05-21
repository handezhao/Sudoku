package sudo.hdz.com.sudoku.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import sudo.hdz.com.sudoku.R;
import sudo.hdz.com.sudoku.callback.OnSetNumberListener;
import sudo.hdz.com.sudoku.utils.Constant;
import sudo.hdz.com.sudoku.utils.SFHelper;

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

    private int fillColor = 0xD7FBE8;

    private float numberSize = 60.0f;
    private int numberColor = 0x000;


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

    /**
     * 当前展示的数独
     */
    private int[][] sudoku = new int[9][9];

    /**
     * 原题对应的数独
     */
    private int[][] originSudoku = new int[9][9];

    private long lastClickTime;


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
        fillColor = typedArray.getColor(R.styleable.SudokuView_fill_color, 0xD7FBE8);
        numberSize = typedArray.getDimension(R.styleable.SudokuView_number_size, 50);
        numberColor = typedArray.getColor(R.styleable.SudokuView_number_color, 0x000);
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
        textPaint.setColor(numberColor);
        textPaint.setTextSize(numberSize);
        textMetrics = textPaint.getFontMetrics();

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(fillColor);
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
                lastClickTime = System.currentTimeMillis();
                performClick();
                selectedPosition[0] = (int) (event.getX() / sideLength);
                selectedPosition[1] = (int) (event.getY() / sideLength);

                // orign Sudoku position not available
                if (!isOriginPosition(selectedPosition[0], selectedPosition[1])) {
                    selected = true;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isOriginPosition(selectedPosition[0], selectedPosition[1])) {
                    if (sudoku[selectedPosition[0]][selectedPosition[1]] != 0 && System
                            .currentTimeMillis() - lastClickTime > 500) {
                        // long clicked
                        if (onSetNumberListener != null) {
                            onSetNumberListener.onReset(selectedPosition[0], selectedPosition[1]);
                        }
                    } else {
                        if (onSetNumberListener != null) {
                            onSetNumberListener.onSetNumber(selectedPosition[0],
                                    selectedPosition[1]);
                        }
                    }
                }
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
                    if (originSudoku[i][j] != 0) {
                        drawSetColor(canvas, i, j); // only origin Sudoku high light
                    }
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
     *
     * @param x
     * @param y
     * @param number
     */
    public void setNumber(int x, int y, int number) {
        sudoku[x][y] = number;
        SFHelper.getInstance().putSudoku(Constant.LAST_GAME_HISTORY, sudoku);
        invalidate();
    }

    /**
     * new game
     *
     * @param origin
     */
    public void initSudoku(int[][] origin) {
        this.originSudoku = origin;
        for (int i = 0; i < originSudoku.length; i++) {
            for (int j = 0; j < originSudoku[i].length; j++) {
                sudoku[i][j] = originSudoku[i][j];
            }
        }
        SFHelper.getInstance().putSudoku(Constant.LAST_GAME_ORIGIN, origin);

        // start a new game meaning to clear history
        SFHelper.getInstance().putSudoku(Constant.LAST_GAME_HISTORY, null);
        invalidate();
    }

    /**
     * continue game
     *
     * @param origin
     * @param saved
     */
    public void initSudoku(int[][] origin, int[][] saved) {
        this.originSudoku = origin;
        this.sudoku = saved;
        SFHelper.getInstance().putSudoku(Constant.LAST_GAME_HISTORY, saved);
        SFHelper.getInstance().putSudoku(Constant.LAST_GAME_ORIGIN, origin);
        invalidate();
    }

    private boolean isOriginPosition(int x, int y) {
        return originSudoku[x][y] != 0;
    }
}
