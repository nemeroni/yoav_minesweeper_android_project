package com.example.yoavproject2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class PixelSplashView extends View {

    private final Paint paint = new Paint();
    private boolean[][] pixels;
    private int cellSize;
    private int rows;
    private int columns;

    public PixelSplashView(Context context) {
        super(context);
    }

    public PixelSplashView(Context context, AttributeSet attrs) {super(context, attrs);}

    public PixelSplashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int dp(float value) {
        return Math.max(1, (int) (value * getResources().getDisplayMetrics().density + 0.5f));
    }

    private void createPixelText(int width, int height) {
        cellSize = dp(3);
        columns = width / cellSize;
        rows = height / cellSize;
        pixels = new boolean[rows][columns];

        // יצירת תמונה קטנטנה בגודל של רשת התאים בלבד (מהיר פי 100!)
        Bitmap smallBitmap = Bitmap.createBitmap(columns, rows, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(smallBitmap);

        Paint textPaint = new Paint(); // בלי ANTI_ALIAS כדי לקבל פיקסלים חדים!
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(columns * 0.14f);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float xCenter = columns / 2f;
        float yCenter = (rows / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f);

        tempCanvas.drawText("שולה מוקשים", xCenter, yCenter, textPaint);

        // קריאה מהירה של הפיקסלים ישירות למערך
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                pixels[row][col] = Color.alpha(smallBitmap.getPixel(col, row)) > 50;
            }
        }

        smallBitmap.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Build the pixel grid right inside onDraw if it hasn't been created yet
        if (pixels == null) {
            createPixelText(w, h);
        }

        paint.setColor(Color.GREEN);
        int gap = Math.max(1, dp(1));

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (pixels[row][col]) {
                    float x = col * cellSize;
                    float y = row * cellSize;
                    canvas.drawRect(
                            x,
                            y,
                            x + cellSize - gap,
                            y + cellSize - gap,
                            paint
                    );
                }
            }
        }
    }
}