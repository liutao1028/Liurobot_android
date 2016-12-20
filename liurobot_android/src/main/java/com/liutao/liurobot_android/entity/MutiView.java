package com.liutao.liurobot_android.entity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;

import com.liutao.liurobot_android.R;

/**
 * Created by liu on 16-11-29 .
 */
public class MutiView extends View {
	private static final String TAG = MutiView.class.getSimpleName();
	public Matrix matrix;
	private Bitmap bitmap;
	public float lastDis;
	public float lastDegrees;
	public int flag_touch = 0;
	private Paint mPaint;
	private Path mPath;
	private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if(flag_touch ==1){
			float[] values = new float[9];
			
			matrix.getValues(values);
			Log.i("bbbbb", "values[0]:"+values[0]+"   values[1]:"+values[1]+"   values[2]"+values[2]+"    values[3]:"+values[3]+"    values[4]:"+values[4]+"    values[5]:"+values[5]
					+"    values[6]:"+values[6]+"    values[7]:"+values[7]+"    values[8]:"+values[8]+"");
			float dx = checkDxBound(values, (-distanceX));
			float dy = checkDyBound(values, (-distanceY));
			Log.i("bbbbb", "dx:"+dx+"		dy:"+dy);
			matrix.postTranslate(dx, dy);
			}
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}
	};
	private GestureDetector.OnDoubleTapListener doubleTap = new GestureDetector.OnDoubleTapListener() {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(flag_touch==1){
				// matrix.postScale(2, 2, 0,0);
				// matrix.postRotate(45, e.getX(), e.getY());
				float[] values = new float[9];
				matrix.getValues(values);
				Log.d(TAG, "onDoubleTapEvent: " + Arrays.toString(values));
				if (values[0] > 2) {
					// matrix.setScale(1, 1, e.getX(), e.getY());
					matrix.postScale(1 / values[0], 1 / values[4], widthSize / 2, heightSize / 2);
					matrix.setTranslate(0, 0);
				} else {
					matrix.postScale(2, 2, widthSize / 2, heightSize / 2);
				}
			}
			
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}
	};
	public GestureDetector gesture;

	public MutiView(Context context) {
		this(context, null);
	}
	public MutiView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MutiView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		matrix = new Matrix();
		mPaint = new Paint();
		mPath = new Path();
		mPaint.setStrokeWidth(5);
		mPaint.setColor(Color.GREEN);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MutiView);
		Drawable drawable = array.getDrawable(R.styleable.MutiView_src);
		if (drawable != null && drawable instanceof BitmapDrawable) {
			bitmap = ((BitmapDrawable) drawable).getBitmap();
		}
		array.recycle();
		gesture = new GestureDetector(context, gestureListener);
		gesture.setOnDoubleTapListener(doubleTap);
	}

	public void setBitmapaa(Bitmap bitmap) {
		this.bitmap = bitmap;
		//postInvalidate();
		//invalidate();
		//((View)getParent()).invalidate();
	}

	Paint p;
	float x_all = 0;
	float y_all = 0;
	float z_all = 0;
	//Canvas canvas_new = null;
	@Override
	protected void onDraw(Canvas canvas) {
		if(bitmap!=null){
//			float[] values =new float[9];
//			matrix.getValues(values);
		canvas.drawBitmap(bitmap, matrix, null);
		//canvas.drawBitmap(bitmap, widthSize/2,heightSize/2, null);
		
		}
			p = new Paint();
			p.setColor(Color.RED);
			
			float[] tmp_values = new float[9];
			matrix.getValues(tmp_values);
			
			if (!onDraw_flag)
			{
				x_d = x_all;
				y_d = y_all;
				z_d = z_all;
			}
			
			if (x_d == 0 || y_d ==0)
				return ;
			
			float x_tmp = x_d * tmp_values[0] * bitmapwidth + tmp_values[2];
			float y_tmp = y_d * tmp_values[4] * bitmapheight + tmp_values[5];		
		
			
			canvas.drawCircle(x_tmp, y_tmp, 8, mPaint);
		
			canvas.drawLine(x_tmp, y_tmp, x_tmp + z_d * 100 * (y_y / Math.abs(y_y)), y_tmp + 100 * (y_y / Math.abs(y_y)), p);

//		}
	}
	
	public boolean onDraw_flag = false;
	public float x_down_tmp = 0;
	public float y_down_tmp = 0;
	public float x_down = 0;
	public float y_down = 0;
	public float x_move = 0;
	public float y_move = 0;
	public float x_d = 0;
	public float y_d = 0;
	public float z_d = 0;
	public float x_x = 0;
	public float y_y = 0;
	
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (event.getPointerCount() == 1) {
//			if(flag_touch == 0){
//				float rx = event.getRawX();
//				float ry = event.getRawY();
//				
//				
//				Log.i("aaaaa", "触摸点的绝对坐标X：" + rx);
//				Log.i("aaaaa", "触摸点的绝对坐标Y：" + ry);
//			
//			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:			
//				x_down_tmp = event.getX();
//				y_down_tmp = event.getY();		
//				//mPath.moveTo(event.getX(), event.getY());
//				break;
//			case MotionEvent.ACTION_MOVE:
//				//mPath.lineTo(event.getX(), event.getY());
//				x_down = x_down_tmp;
//				y_down = y_down_tmp;
//				
//				float[] tmp_values = new float[9];
//				matrix.getValues(tmp_values);
//				x_d = (x_down - tmp_values[2]) / (tmp_values[0] * bitmapwidth);
//				y_d = (y_down - tmp_values[5]) / (tmp_values[4] * bitmapheight);
//				
//				x_move = event.getX();
//				y_move = event.getY();			
//				
//				x_x = x_move - x_down;
//				y_y = y_move - y_down;
//				if (y_y == 0)
//					y_y = 0.000001f;
//				z_d = x_x / y_y;
//				
//				onDraw_flag = true;
//				break;
//			case MotionEvent.ACTION_CANCEL:
//			case MotionEvent.ACTION_UP:
//				
//				break;
//			}
//			}
//		} else if (event.getPointerCount() == 2) {
//			switch (event.getActionMasked()) {
//			case MotionEvent.ACTION_POINTER_DOWN:
//			case MotionEvent.ACTION_DOWN:
//				// MotionEvent obtain = MotionEvent.obtain(event.getDownTime(),
//				// event.getEventTime(), MotionEvent.ACTION_UP, event.getX(),
//				// event.getY(), event.getMetaState());
//				// gesture.onTouchEvent(obtain);
//				lastDis = ((float) Math
//						.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2)));
//				lastDegrees = ((float) Math
//						.toDegrees(Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1))));
//				break;
//			case MotionEvent.ACTION_MOVE:
//				float px = (event.getX(0) + event.getX(1)) / 2;
//				float py = (event.getY(0) + event.getY(1)) / 2;
//				float dis = ((float) Math
//						.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2)));
//				float degrees = ((float) Math
//						.toDegrees(Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1))));
//				float[] values = new float[9];
//				matrix.getValues(values);
//				Log.i("aaaa", "matrix values[0] sinX" + values[0] + "");
//				if (values[0] >= 2.0f) {
//					if (dis / lastDis <= 1) {
//						matrix.postScale(dis / lastDis, dis / lastDis, px, py);
//					}
//				} else if (values[0] <= 1.0f) {
//					if (dis / lastDis >= 1) {
//						matrix.postScale(dis / lastDis, dis / lastDis, px, py);
//					}
//				} else {
//					
//					// if (dis / lastDis / values[0] >= 2)
//					// {
//					// matrix.postScale(1, 1, px, py);
//					// }
//					// else if (dis / lastDis / values[0] <= 1)
//					// {
//					//
//					// matrix.postScale(1, 1, px, py);
//					//
//					//
//					// }
//					// if (dis / lastDis / values[0] < 2 && dis / lastDis /
//					// values[0] > 1 )
//					{
//						matrix.postScale(dis / lastDis, dis / lastDis, px, py);
//					}
//				}
//				// matrix.postScale(dis / lastDis, dis / lastDis, px, py);
//				// matrix.postRotate(degrees - lastDegrees, px, py);
//				lastDis = dis;
//				lastDegrees = degrees;
//				break;
//			case MotionEvent.ACTION_POINTER_UP:
//				Log.d(TAG, "onTouchEvent: " + String.format("%x", event.getAction()));
//				break;
//			}
//		}
//		gesture.onTouchEvent(event);
//		invalidate();
//		return true;
//	}

	/**
	 * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
	 * 
	 * @param values
	 * @param dy
	 * @return
	 */
	private float checkDyBound(float[] values, float dy) {
		float height = getHeight();
		if (bitmapheight * values[Matrix.MSCALE_Y] < height)
			return 0;
		if (values[Matrix.MTRANS_Y] + dy > 0)
			dy = -values[Matrix.MTRANS_Y];
		else if (values[Matrix.MTRANS_Y] + dy < -(bitmapheight * values[Matrix.MSCALE_Y] - height))
			dy = -(bitmapheight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
		return dy;
	}

	/**
	 * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
	 * 
	 * @param values
	 * @param dx
	 * @return
	 */
	private float checkDxBound(float[] values, float dx) {
		float width = getWidth();
		if (bitmapwidth * values[Matrix.MSCALE_X] < width)
			return 0;
		if (values[Matrix.MTRANS_X] + dx > 0)
			dx = -values[Matrix.MTRANS_X];
		else if (values[Matrix.MTRANS_X] + dx < -(bitmapwidth * values[Matrix.MSCALE_X] - width))
			dx = -(bitmapwidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
		return dx;
	}

	public int bitmapwidth = 0;
	public int bitmapheight = 0;
	int widthSize = 0;
	int heightSize = 0;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (bitmap != null) {
			bitmapwidth = bitmap.getWidth();
			bitmapheight = bitmap.getHeight();
		}
		widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		// switch (widthMode) {
		// case MeasureSpec.AT_MOST:
		// widthSize = Math.min(bitmapwidth, widthSize);
		// break;
		// case MeasureSpec.EXACTLY:
		// break;
		// case MeasureSpec.UNSPECIFIED:
		// widthSize = bitmapwidth;
		// break;
		// }
		// switch (heightMode) {
		// case MeasureSpec.AT_MOST:
		// heightSize = Math.min(bitmapheight, heightSize);
		// break;
		// case MeasureSpec.EXACTLY:
		// break;
		// case MeasureSpec.UNSPECIFIED:
		// heightSize = bitmapheight;
		// break;
		// }
		// if (bitmap != null) {
		// float min = Math.min((float) widthSize / bitmapwidth, (float)
		// heightSize / bitmapheight);
		// matrix.setScale(min, min, 0, 0);
		// }
		//setMeasuredDimension(bitmapwidth, bitmapheight);
		setMeasuredDimension(widthSize, heightSize);
	}
}
