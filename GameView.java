package hu.ait.android.chau.minesweepergame.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import hu.ait.android.chau.minesweepergame.MainActivity;
import hu.ait.android.chau.minesweepergame.Model.MinesweeperModel;
import hu.ait.android.chau.minesweepergame.R;

/**
 * Created by Chau on 2/25/2015.
 */
public class GameView extends View {

    private Paint paintBorder, paintUnchecked, paintNum;
    private Boolean isTouchable = true;
    private MainActivity context = (MainActivity) this.getContext();
    private Bitmap backgroundImg, mineImg, flagImg;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = getResources();
        backgroundImg = BitmapFactory.decodeResource(res, R.drawable.portal);
        mineImg = BitmapFactory.decodeResource(res, R.drawable.companioncube);
        flagImg = BitmapFactory.decodeResource(res, R.drawable.flag);

        paintBorder = new Paint();
        paintBorder.setStrokeWidth(4);
        paintBorder.setColor(Color.CYAN);
        paintBorder.setStyle(Paint.Style.STROKE);

        paintUnchecked = new Paint();
        paintUnchecked.setStyle(Paint.Style.FILL);
        paintUnchecked.setColor(Color.GRAY);

        paintNum = new Paint();
        paintNum.setColor(Color.rgb(255, 171, 18));
        paintNum.setStyle(Paint.Style.FILL_AND_STROKE);
        paintNum.setStrokeWidth(4);
        paintNum.setTextSize(paintNum.getTextSize() * 5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBG(canvas);
        drawGameArea(canvas);
        drawFields(canvas);
    }

    private void drawBG(Canvas canvas) {
        Rect dst = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(backgroundImg, null, dst, null);
    }

    private void drawGameArea(Canvas canvas) {
        // Border
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBorder);
        // Horizontal Lines
        for (int i = 1; i < MinesweeperModel.NUM_ROW; i++) {
            canvas.drawLine(0, i * (getHeight() / MinesweeperModel.NUM_ROW),
                    getWidth(), i * (getHeight() / MinesweeperModel.NUM_ROW), paintBorder);
        }
        // Vertical Lines
        for (int i = 1; i < MinesweeperModel.NUM_COL; i++) {
            canvas.drawLine(i * (getWidth() / MinesweeperModel.NUM_COL), 0,
                    i * (getWidth() / MinesweeperModel.NUM_COL), getHeight(), paintBorder);
        }
    }

    private void drawFields(Canvas canvas) {
        for (int i = 0; i < MinesweeperModel.NUM_COL; i++) {
            for (int j = 0; j < MinesweeperModel.NUM_ROW; j++) {
                int viewField = MinesweeperModel.getInstance().getViewField(i, j);
                Rect dst = new Rect(
                        i*(getWidth() / MinesweeperModel.NUM_COL) +
                                ((getWidth() / MinesweeperModel.NUM_COL) / 10),
                        j*(getHeight() / MinesweeperModel.NUM_ROW) +
                                ((getHeight() / MinesweeperModel.NUM_ROW) / 10),
                        (i+1)*(getWidth() / MinesweeperModel.NUM_COL) -
                                ((getWidth() / MinesweeperModel.NUM_COL) / 10),
                        (j+1)*(getHeight() / MinesweeperModel.NUM_ROW) -
                                ((getHeight() / MinesweeperModel.NUM_ROW) / 10)
                );

                switch (viewField) {
                    case MinesweeperModel.VM_UNCHECKED:
                        canvas.drawRect(dst, paintUnchecked);
                        break;
                    case MinesweeperModel.VM_MINE:
                        canvas.drawBitmap(mineImg, null, dst, null);
                        break;
                    case MinesweeperModel.VM_FLAG:
                        canvas.drawBitmap(flagImg, null, dst, null);
                        break;
                    default:
                        // Draw number.
                        canvas.drawText(
                                Integer.toString(viewField),
                                i*(getWidth() / MinesweeperModel.NUM_COL) +
                                        (getWidth() / MinesweeperModel.NUM_COL / 3),
                                j*(getHeight() / MinesweeperModel.NUM_ROW) +
                                        2*(getHeight() / MinesweeperModel.NUM_ROW / 3),
                                paintNum
                        );
                }
            }
        }
    }

    public void clearGameArea() {
        if ( !(isTouchable) ) {
            toggleEnable();
        }
        MinesweeperModel.getInstance().resetGame();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isTouchable) {
            int tX = ((int) event.getX()) / (getWidth() / MinesweeperModel.NUM_COL);
            int tY = ((int) event.getY()) / (getHeight() / MinesweeperModel.NUM_ROW);

            if (tX < MinesweeperModel.NUM_COL && tY < MinesweeperModel.NUM_ROW) {

                if (MinesweeperModel.getInstance().getMode() == MinesweeperModel.Mode.TRYFIELD) {
                    switch (MinesweeperModel.getInstance().getModelContent(tX, tY)) {
                        case EMPTY:
                            setNeighMineCount(tX, tY);
                            invalidate();
                            break;
                        case MINE:
                            mineLoss();
                            invalidate();
                            break;
                        case FLAG:
                            break;
                    }
                }

                if (MinesweeperModel.getInstance().getMode() == MinesweeperModel.Mode.PLACEFLAG) {
                    switch (MinesweeperModel.getInstance().getModelContent(tX, tY)) {
                        case EMPTY:
                            flagLoss();
                            invalidate();
                            break;
                        case MINE:
                            setFlag(tX, tY);
                            if (MinesweeperModel.getInstance().checkWinner()) {
                                gameWin();
                            }
                            invalidate();
                            break;
                        case FLAG:
                            break;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void gameWin() {
        context.showMessage(context.getString(R.string.winMsg));
        toggleEnable();
    }

    private void mineLoss() {
        context.showMessage(context.getString(R.string.mineMsg));
        MinesweeperModel.getInstance().revealMines();
        toggleEnable();
    }

    private void flagLoss() {
        context.showMessage(context.getString(R.string.flagMsg));
        MinesweeperModel.getInstance().revealMines();
        toggleEnable();
    }

    private void toggleEnable() {
        isTouchable = !isTouchable;
        context.flipToggleBtnEnable();
    }

    private void setNeighMineCount(int tX, int tY) {
        MinesweeperModel.getInstance().setViewField(tX, tY,
                MinesweeperModel.getInstance().countNeighboringMines(tX, tY));
    }

    private void setFlag(int tX, int tY) {
        MinesweeperModel.getInstance().incrementFoundCount();
        MinesweeperModel.getInstance().setViewField(tX, tY, MinesweeperModel.VM_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
        setMeasuredDimension(d, d);
    }
}
