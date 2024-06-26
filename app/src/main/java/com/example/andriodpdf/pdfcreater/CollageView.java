package com.example.andriodpdf.pdfcreater;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CollageView extends AppCompatImageView {

    private static  final  int PADDING = 8;
    private static  final float STROKE_WIDTH = 8.0f;
    private Paint mBorderPaint;

    public CollageView(Context context) {
        this(context, null);

    }
    public CollageView (Context context , AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBorderPaint();
    }
    private void initBorderPaint(){
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.RED);
        mBorderPaint.setStrokeWidth(STROKE_WIDTH);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
