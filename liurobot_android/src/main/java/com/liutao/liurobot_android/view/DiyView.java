package com.liutao.liurobot_android.view;

import com.liutao.liurobot_android.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * @author Mr Liu
 * @data 2016-9-22 --- 锟斤拷锟斤拷9:07:43
 */
public class DiyView extends View {
	/**
	 * 默锟斤拷为1锟斤拷
	 */
	private static final int DEFAULT_MOVIE_DURATION = 1000;

	private int mMovieResourceId;

	private Movie mMovie;

	private long mMovieStart;

	private int mCurrentAnimationTime = 0;

	private float mLeft;

	private float mTop;

	private float mScale;

	private int mMeasuredMovieWidth;

	private int mMeasuredMovieHeight;

	private boolean mVisible = true;

	private volatile boolean mPaused = false;

	public DiyView(Context context) {
		this(context, null);
	}

	public DiyView(Context context, AttributeSet attrs) {
		this(context, attrs, R.styleable.CustomTheme_gifViewStyle);
	}

	public DiyView(Context context, AttributeSet attrs, int defStyle)

	{
		super(context, attrs, defStyle);
		setViewAttributes(context, attrs, defStyle);
	}

	@SuppressLint("NewApi")
	private void setViewAttributes(Context context, AttributeSet

	attrs, int defStyle) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		// 锟斤拷锟斤拷锟斤拷锟侥硷拷锟叫讹拷锟斤拷gif锟斤拷值锟斤拷锟斤拷锟斤拷锟斤拷Movie实锟斤拷
		final TypedArray array = context.obtainStyledAttributes

		(attrs, R.styleable.GifView, defStyle,

				R.style.Widget_GifView);
		mMovieResourceId = array.getResourceId

		(R.styleable.GifView_gif, -1);
		mPaused = array.getBoolean(R.styleable.GifView_paused,

				false);
		array.recycle();
		if (mMovieResourceId != -1) {
			mMovie = Movie.decodeStream(getResources

			().openRawResource(mMovieResourceId));
		}
	}

	/**
	 * 锟斤拷锟斤拷gif图锟斤拷源
	 * 
	 * @param movieResId
	 */
	public void setMovieResource(int movieResId) {
		this.mMovieResourceId = movieResId;
		mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
		requestLayout();
	}

	public void setMovie(Movie movie) {
		this.mMovie = movie;
		requestLayout();
	}

	public Movie getMovie() {
		return mMovie;
	}

	public void setMovieTime(int time) {
		mCurrentAnimationTime = time;
		invalidate();
	}

	/**
	 * 锟斤拷锟斤拷锟斤拷停
	 * 
	 * @param paused
	 */
	public void setPaused(boolean paused) {
		this.mPaused = paused;
		if (!paused) {
			mMovieStart = android.os.SystemClock.uptimeMillis() - mCurrentAnimationTime;
		}
		invalidate();
	}

	/**
	 * 锟叫讹拷gif图锟角凤拷停止锟斤拷
	 * 
	 * @return
	 */
	public boolean isPaused() {
		return this.mPaused;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int

	heightMeasureSpec) {
		if (mMovie != null) {
			int movieWidth = mMovie.width();
			int movieHeight = mMovie.height();
			int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
			int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
			float scaleH = (float)movieHeight/(float)maximumHeight;
			float scaleW = (float) movieWidth / (float) maximumWidth;
			// 原锟斤拷1f 锟斤拷锟斤拷 锟矫匡拷锟斤拷为0.7
			mScale = 1f / scaleW;
//			mScale = 1f / scaleH;
//			mMeasuredMovieHeight = maximumHeight;
//			mMeasuredMovieWidth = (int) (movieWidth*mScale);
			mMeasuredMovieWidth = maximumWidth;
			mMeasuredMovieHeight = (int) (movieHeight * mScale);
			setMeasuredDimension(movieWidth,movieHeight);
		} else {
			setMeasuredDimension(getSuggestedMinimumWidth(),getSuggestedMinimumHeight());
		}
	}
   

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int

	b) {
		super.onLayout(changed, l, t, r, b);
		mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
		mTop = (getHeight() - mMeasuredMovieHeight) / 2f;
		mVisible = getVisibility() == View.VISIBLE;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mMovie != null) {
			if (!mPaused) {
				updateAnimationTime();
				drawMovieFrame(canvas);
				invalidateView();
			} else {
				drawMovieFrame(canvas);
			}
		}
	}

	@SuppressLint("NewApi")
	private void invalidateView() {
		if (mVisible) {
			if (Build.VERSION.SDK_INT >=

			Build.VERSION_CODES.JELLY_BEAN) {
				postInvalidateOnAnimation();
			} else {
				invalidate();
			}
		}
	}

	private void updateAnimationTime() {
		long now = android.os.SystemClock.uptimeMillis();
		// 锟斤拷锟斤拷锟揭恢★拷锟斤拷锟铰硷拷锟绞际憋拷锟�
		if (mMovieStart == 0) {
			mMovieStart = now;
		}
		// 取锟斤拷锟斤拷锟斤拷锟斤拷时锟斤拷
		int dur = mMovie.duration();
		if (dur == 0) {
			dur = DEFAULT_MOVIE_DURATION;
		}
		// 锟斤拷锟斤拷锟揭拷锟绞撅拷诩锟街�
		mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
	}

	private void drawMovieFrame(Canvas canvas) {
		// 锟斤拷锟斤拷要锟斤拷示锟斤拷帧锟斤拷锟斤拷锟狡硷拷锟斤拷
		mMovie.setTime(mCurrentAnimationTime);
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(mScale, mScale);
		mMovie.draw(canvas, mLeft/ mScale , mTop/ mScale);
		canvas.restore();
	}

	@SuppressLint("NewApi")
	@Override
	public void onScreenStateChanged(int screenState) {
		super.onScreenStateChanged(screenState);
		mVisible = screenState == SCREEN_STATE_ON;
		invalidateView();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onVisibilityChanged(View changedView, int

	visibility) {
		super.onVisibilityChanged(changedView, visibility);
		mVisible = visibility == View.VISIBLE;
		invalidateView();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		mVisible = visibility == View.VISIBLE;
		invalidateView();
	}

}
