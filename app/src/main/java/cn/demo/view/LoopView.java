package cn.demo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import cn.demo.R;

public class LoopView extends View {

	private final int DEFAULT_INNER_COLOR = 0xff00ff00;
	private final int DEFAULT_INNER_RADIUS = 60;

	private final int DEFAULT_TOP_COLOR = 0xffff0000;
	private final int DEFAULT_BOTTOM_COLOR = 0xffffff00;
	private final int DEFAULT_OUTTER_WIDTH = 20;

	private Paint mInnerPaint;
	private int mInnerColor;
	private int mInnerRadius;

	private Paint mOutterPaint;
	private int mOutterWidth;
	
	private SweepGradient mSweepGradient;

	private RectF mOutterRectF;
	private int mAngle;

	private int mTopColor;
	private int mBottomColor;

	private boolean mShowInnerCircle;

	public LoopView(Context context) {
		super(context);
		init(context);
	}

	public LoopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Loader(context, attrs, 0);
		init(context);
	}

	public LoopView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Loader(context, attrs, defStyle);
		init(context);
	}

	private void Loader(Context context, AttributeSet attrs, int defStyle) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.LoopView, defStyle, 0);
		try {
			mShowInnerCircle = typedArray.getBoolean(
					R.styleable.LoopView_showInnerCircle, false);
			mInnerColor = typedArray.getColor(R.styleable.LoopView_innerColor,
					DEFAULT_INNER_COLOR);
			mInnerRadius = typedArray.getDimensionPixelSize(
					R.styleable.LoopView_innerRadius, DEFAULT_INNER_RADIUS);

			mTopColor = typedArray.getColor(R.styleable.LoopView_topColor,
					DEFAULT_TOP_COLOR);
			mBottomColor = typedArray.getColor(
					R.styleable.LoopView_bottomColor, DEFAULT_BOTTOM_COLOR);
			mOutterWidth = typedArray.getDimensionPixelSize(
					R.styleable.LoopView_outterWidth, DEFAULT_OUTTER_WIDTH);
			mAngle = typedArray.getInteger(R.styleable.LoopView_angle, 90);
		} finally {
			typedArray.recycle();
		}
	}

	private void init(Context context) {
		mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInnerPaint.setStyle(Style.FILL);
		mInnerPaint.setColor(mInnerColor);

		mOutterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mOutterPaint.setStyle(Style.STROKE);
		mOutterPaint.setStrokeWidth(mOutterWidth);
		mOutterPaint.setStrokeCap(Cap.ROUND);

		mOutterRectF = new RectF();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = (mInnerRadius + mOutterWidth) * 2 + getPaddingLeft()
				+ getPaddingRight();
		int height = (mInnerRadius + mOutterWidth) * 2 + getPaddingTop()
				+ getPaddingBottom();

		setMeasuredDimension(measureSize(widthMeasureSpec, width),
				measureSize(heightMeasureSpec, height));
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth,
			int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		mSweepGradient = new SweepGradient(
				(width - getPaddingLeft() - getPaddingRight()) / 2.0f, (height
						- getPaddingTop() - getPaddingBottom()) / 2.0f,
				new int[] { mTopColor, mBottomColor, mTopColor }, new float[] {
						0f, 0.5f, 1f });
	}

	private int measureSize(int measureSpec, int size) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = size;
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(specSize, size);
			}
		}

		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();

		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		int height = getHeight() - getPaddingTop() - getPaddingBottom();

		canvas.rotate(-90, getPaddingLeft() + width / 2.0f, getPaddingTop()
				+ height / 2.0f);

		if (mShowInnerCircle) {
			canvas.drawCircle(getPaddingLeft() + width / 2.0f, getPaddingTop()
					+ height / 2.0f, mInnerRadius + 1, mInnerPaint);
		}
		mOutterPaint.setShader(mSweepGradient);

		mOutterRectF.left = getPaddingLeft() + width / 2.0f - mInnerRadius
				- mOutterWidth / 2.0f;
		mOutterRectF.top = getPaddingTop() + height / 2.0f - mInnerRadius
				- mOutterWidth / 2.0f;
		mOutterRectF.right = getPaddingLeft() + width / 2.0f + mInnerRadius
				+ mOutterWidth / 2.0f;
		mOutterRectF.bottom = getPaddingTop() + height / 2.0f + mInnerRadius
				+ mOutterWidth / 2.0f;

		canvas.drawArc(mOutterRectF, 0, mAngle, false, mOutterPaint);
		canvas.restore();
	}

	public boolean isShowInnerCircle() {
		return mShowInnerCircle;
	}

	public void setShowInnerCircle(boolean showInnerCircle) {
		mShowInnerCircle = showInnerCircle;
		invalidate();
	}

	public int getInnerColor() {
		return mInnerColor;
	}

	public void setInnerColor(int innerColor) {
		mInnerColor = innerColor;
		mInnerPaint.setColor(mInnerColor);
		invalidate();
	}

	public int getInnerRadius() {
		return mInnerRadius;
	}

	public int getOutterWidth() {
		return mOutterWidth;
	}

	public void setRaiusAndWidth(int innerRadius, int outterWidth) {
		mInnerRadius = innerRadius;

		mOutterWidth = outterWidth;
		mOutterPaint.setStrokeWidth(outterWidth);

		requestLayout();

		invalidate();
	}

	public int getAngle() {
		return mAngle;
	}

	public void setAngle(int angle) {
		mAngle = angle;
		invalidate();
	}

	public int getTopColor() {
		return mTopColor;
	}

	public int getBottomColor() {
		return mBottomColor;
	}

	public void setColors(int topColor, int bottomColor) {
		mTopColor = topColor;
		mBottomColor = bottomColor;

		mSweepGradient = new SweepGradient(
				(getWidth() - getPaddingLeft() - getPaddingRight()) / 2.0f,
				(getHeight() - getPaddingTop() - getPaddingBottom()) / 2.0f,
				new int[] { mTopColor, mBottomColor, mTopColor }, new float[] {
						0, 0.5f, 1.0f });
		invalidate();
	}

}
