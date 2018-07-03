package com.github.handezhao.sudoku.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sudo.hdz.com.sudoku.R;
import com.github.handezhao.sudoku.callback.OnSudokuSelectedListener;
import com.github.handezhao.sudoku.observer.PossibleNumber;
import com.github.handezhao.sudoku.observer.PossibleNumberWatcher;
import com.github.handezhao.sudoku.utils.Constant;
import com.github.handezhao.sudoku.utils.SFHelper;
import com.github.handezhao.sudoku.utils.SudokuUtils;

/**
 * Description: a Sudoku demo
 * I want to use a user-defined view to show
 * a Sudoku.
 * <p>
 * Created by hdz on 17/05/2018.
 */

public class SudokuView extends View implements PossibleNumber {

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
    private Paint selectedPaint;

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
    private int[] selectedPosition = new int[]{-1, -1};

    private int[] lastSelectedPosition = new int[]{-1, -1};

    private OnSudokuSelectedListener onSudokuSelectedListener;

    /**
     * 当前展示的数独
     */
    private int[][] sudoku = new int[9][9];

    /**
     * 原题对应的数独
     */
    private int[][] originSudoku = new int[9][9];

    private float roundR = 16.0f;

    private static final float overSpace = 50.0f;

    private float outSignle;
    int resultLength;
    RectF selectedRect = new RectF(0, 0, 0, 0);

    private List<PossibleNumberWatcher> possibleNumberWatchers = new ArrayList<>();


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

        selectedPaint = new Paint();
        selectedPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // do not set padding!!!
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        resultLength = Math.min(width, height);
        outSignle = (resultLength - 2 * overSpace - 2 * drawOffset) / 3.0f;
        sideLength = (outSignle - 2 * drawOffset) / 3.0f;
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
        for (int i = 0; i < 4; i++) {
            canvas.drawLine(overSpace, overSpace + drawOffset + i * outSignle,
                    resultLength - overSpace, overSpace + drawOffset + i * outSignle, outlinePaint);
        }
    }

    /**
     * 画垂直的外框线
     *
     * @param canvas
     */
    private void drawVerticalOutline(Canvas canvas) {
        for (int i = 0; i < 4; i++) {
            canvas.drawLine(overSpace + drawOffset + i * outSignle, overSpace + 2 *
                    drawOffset, overSpace + drawOffset + i * outSignle, resultLength - 2 *
                    drawOffset - overSpace, outlinePaint);
        }
    }

    /**
     * 画水平的内框线
     *
     * @param canvas
     */
    private void drawHorizonInline(Canvas canvas) {
        float y;
        for (int i = 1; i < 9; i++) {
            if (i % 3 == 0) continue;
            if (i < 3) {
                y = overSpace + 2 * drawOffset + i * sideLength;
            } else if (3 < i && i < 6) {
                y = overSpace + outSignle + 2 * drawOffset + sideLength * (i % 4 + 1);
            } else {
                y = overSpace + outSignle * 2 + 2 * drawOffset + sideLength * (i % 7 + 1);
            }
            canvas.drawLine(overSpace + 2 * drawOffset, y, resultLength - overSpace - 2 *
                    drawOffset, y, inlinePaint);
        }


    }

    /**
     * 画垂直的内框线
     *
     * @param canvas
     */
    private void drawVerticalInline(Canvas canvas) {
        float x;
        for (int i = 1; i < 9; i++) {
            if (i % 3 == 0) continue;
            if (i < 3) {
                x = overSpace + 2 * drawOffset + sideLength * i;
            } else if (3 < i && i < 6) {
                x = overSpace + outSignle + drawOffset * 2 + (i % 4 + 1) * sideLength;
            } else {
                x = overSpace + outSignle * 2 + drawOffset * 2 + (i % 7 + 1) * sideLength;
            }
            canvas.drawLine(x, overSpace + 2 * drawOffset, x, resultLength - 2 * drawOffset -
                    overSpace, inlinePaint);
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
        if (number == 0) return;
        float drawY = startY + sideLength / 2 + (Math.abs(textMetrics.ascent) - textMetrics
                .descent) / 2;
        float drawX = startX + sideLength / 2 - textPaint.measureText(String.valueOf(number)) / 2;
        canvas.drawText(String.valueOf(number), drawX, drawY, textPaint);
    }

    private float getXPostion(int x) {
        return overSpace + x * sideLength + drawOffset * 2 * (x / 3 + 1);
    }

    private float getYPosition(int y) {
        return overSpace + y * sideLength + drawOffset * 2 * (y / 3 + 1);
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
                float exactX = event.getX() - overSpace - 2 * drawOffset;
                float exactY = event.getY() - overSpace - 2 * drawOffset;
                if (isOutBound(event.getX(), event.getY())) {
                    Log.w(TAG, " isOutBound !!!");
                    return false;
                }
                selectedPosition[0] = (int) (exactX / sideLength);
                selectedPosition[1] = (int) (exactY / sideLength);
                // 边角处理
                if (selectedPosition[0] > 8) {
                    selectedPosition[0] = 8;
                }
                if (selectedPosition[1] > 8) {
                    selectedPosition[1] = 8;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isInBound(selectedPosition[0], selectedPosition[1])) return false;
                if (!isOriginPosition(selectedPosition[0], selectedPosition[1])) {
                    if (onSudokuSelectedListener != null) {
                        onSudokuSelectedListener.onSelected(selectedPosition);
                    }
                    if (!sameSelectedPosition()) {
                        notifyToolView(SudokuUtils.getInstance().getPossibleNUmber
                                (selectedPosition[0], selectedPosition[1], sudoku));
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHighlightLimit(canvas);
        drawSudoku(canvas);
        drawHorizonInline(canvas);
        drawVerticalInline(canvas);
        drawHorizonOutline(canvas);
        drawVerticalOutline(canvas);
        drawSelectedPosition(canvas);
    }


    private void drawSudoku(Canvas canvas) {
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                if (sudoku[i][j] > 0) {
                    if (originSudoku[i][j] != 0) {
                        //                        drawSetColor(canvas, i, j); // only origin
                        // Sudoku high light
                        textPaint.setColor(numberColor);
                    } else {
                        textPaint.setColor(Color.BLACK);
                    }
                    drawNumber(canvas, sudoku[i][j], getXPostion(i), getYPosition(j));
                }
            }
        }
    }

    private void drawSelectedPosition(Canvas canvas) {
        if (!isInBound(selectedPosition[0], selectedPosition[1])) return;
        if (originSudoku[selectedPosition[0]][selectedPosition[1]] != 0) return;
        drawSelectedReact(canvas, selectedPosition[0], selectedPosition[1]);
        textPaint.setColor(Color.BLACK);
        drawNumber(canvas, sudoku[selectedPosition[0]][selectedPosition[1]], getXPostion
                (selectedPosition[0]), getYPosition(selectedPosition[1]));
        lastSelectedPosition[0] = selectedPosition[0];
        lastSelectedPosition[1] = selectedPosition[1];
    }

    private void drawSetColor(Canvas canvas, int x, int y) {
        canvas.drawRect(getXPostion(x), getYPosition(y), getXPostion(x) + sideLength,
                getYPosition(y) + sideLength, fillPaint);
    }

    private void drawSelectedReact(Canvas canvas, int x, int y) {
        float left = getXPostion(x) - roundR;
        float top = getYPosition(y) - roundR;
        float right = getXPostion(x) + sideLength + roundR;
        float bottom = getYPosition(y) + sideLength + roundR;
        selectedRect.set(left, top, right, bottom);
        selectedPaint.setStyle(Paint.Style.FILL);
        selectedPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(selectedRect, roundR, roundR, selectedPaint);

        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setColor(getResources().getColor(R.color.outline_color));
        selectedPaint.setStrokeWidth(outlineWidth);
        canvas.drawRoundRect(selectedRect, roundR + 5, roundR + 5, selectedPaint);
    }

    private void drawHighlightLimit(Canvas canvas) {
        int x = selectedPosition[0];
        int y = selectedPosition[1];
        if (x < 0 || y < 0) return;
        // 先画当前所在的大方格
        float startX = overSpace + 2 * drawOffset * (x / 3 + 1) + sideLength * 3 * (x / 3);
        float startY = overSpace + 2 * drawOffset * (y / 3 + 1) + sideLength * 3 * (y / 3);
        canvas.drawRect(startX, startY, startX + sideLength * 3, startY + sideLength * 3,
                fillPaint);

        // 画对应的列
        startX = overSpace + drawOffset * 2 * (x / 3 + 1) + sideLength * x;
        startY = overSpace + drawOffset * 2;
        canvas.drawRect(startX, startY, startX + sideLength, resultLength - overSpace -
                drawOffset * 2, fillPaint);
        // 画对应的行
        startX = overSpace + 2 * drawOffset;
        startY = overSpace + drawOffset * 2 * (y / 3 + 1) + sideLength * y;
        canvas.drawRect(startX, startY, resultLength - overSpace - drawOffset * 2, startY +
                sideLength, fillPaint);

    }

    /**
     * 填入具体位置的一个数
     *
     * @param x
     * @param y
     * @param number
     */
    public void setNumber(int x, int y, int number) {
        if (!isInBound(x, y)) {
            Log.e(TAG, "x is " + x + ", y is " + y);
            Log.e(TAG, "unexpect x or y ");
            return;
        }
        if (originSudoku[x][y] != 0) return;
        //check valid
        if (!SudokuUtils.getInstance().isValidNumber(number, x, y, sudoku)) {
            Log.d(TAG, "invalid number!");
            return;
        }
        sudoku[x][y] = number;
        SFHelper.getInstance().putSudoku(Constant.LAST_GAME_HISTORY, sudoku);
        notifyToolView(SudokuUtils.getInstance().getPossibleNUmber
                (selectedPosition[0], selectedPosition[1], sudoku));
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
        if (!isInBound(x, y)) {
            Log.d(TAG, "touch outside");
            return false;
        } else {
            return originSudoku[x][y] != 0;
        }
    }

    public void setOnSudokuSelectedListener(OnSudokuSelectedListener onSudokuSelectedListener) {
        this.onSudokuSelectedListener = onSudokuSelectedListener;
    }

    private boolean sameSelectedPosition() {
        return lastSelectedPosition[0] == selectedPosition[0] && lastSelectedPosition[1] ==
                selectedPosition[1] && selectedPosition[0] > 0;
    }

    private boolean isInBound(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 9;
    }

    private boolean isOutBound(float x, float y) {
        return x <= (overSpace + drawOffset * 2) || x >= (resultLength - overSpace) || y <=
                (overSpace + drawOffset * 2) || y >= (resultLength - overSpace);
    }

    @Override
    public void notifyToolView(Set<Integer> possible) {
        for (PossibleNumberWatcher watcher : possibleNumberWatchers) {
            watcher.onPossibleNumberChanged(possible);
        }
    }

    @Override
    public void registerPossibleNumberListener(PossibleNumberWatcher watcher) {
        if (!possibleNumberWatchers.contains(watcher)) {
            possibleNumberWatchers.add(watcher);
        }
    }

    @Override
    public void unRegisterPossibleNumberListener(PossibleNumberWatcher watcher) {
        if (possibleNumberWatchers.contains(watcher)) {
            possibleNumberWatchers.remove(watcher);
        }
    }
}
