package com.liutao.liurobot_android.entity;

import com.liutao.liurobot_android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	private Paint mPaint;
	private Path mPath;
	private Bitmap back;
	private Bitmap mBitmap;
	private Canvas mBitmapCanvas;
	private int width;
	public DrawView(Context context) {
		this(context, null);
	}

	public DrawView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		back = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundblack);
		//画布大小
		mBitmap = Bitmap.createBitmap(back.getWidth(),back.getHeight(), Bitmap.Config.RGB_565);
		mBitmapCanvas = new Canvas(mBitmap);
       // mBitmapCanvas.drawColor(Color.WHITE);
        mBitmapCanvas.drawARGB(0, 0, 0, 0);
		mPaint = new Paint();
		mPath = new Path();
		
		mPaint.setStyle(Paint.Style.STROKE);
		if (attrs == null) {
			mPaint.setColor(Color.BLACK);
			mPaint.setStrokeWidth(5);

		} else {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawView);
			int color = typedArray.getColor(R.styleable.DrawView_paintColor, Color.BLACK);
			setPaintColor(color);
			float width = typedArray.getDimension(R.styleable.DrawView_paintWidth, 5);
			setPaintWidth(width);
		}
	}
	

	private void setPaintWidth(float width) {
		mPaint.setStrokeWidth(width);

	}

	public void setPaintColor(int color) {
		mPaint.setColor(color);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 画布背景
		//canvas.drawColor(Color.RED);
		//		canvas.drawBitmap(back, 0, 0, null);
		// 花线
		if(mBitmap != null) {
            //canvas.drawBitmap(mBitmap, 0, 0, mPaint);
            canvas.drawARGB(0, 0, 0, 0);
        }
		canvas.drawPath(mPath, mPaint);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//height = View.MeasureSpec.getSize(heightMeasureSpec);    
	    width = View.MeasureSpec.getSize(widthMeasureSpec);    
	    setMeasuredDimension(width,300);  //这里面是原始的大小，需要重新计算可以修改本行
		
	}

}
