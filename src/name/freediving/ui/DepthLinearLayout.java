package name.freediving.ui;

import name.freediving.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Layout does the same as super, but draws background gradient with supported
 * "depth"
 * 
 * @author DD
 * 
 */
public class DepthLinearLayout extends LinearLayout {
	private int mBackHeight;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mBackHeight = MeasureSpec.getSize(heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public DepthLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public DepthLinearLayout(Context context) {
		super(context);
		init();
	}

	private Paint p;
	private int[] colors;
	private float[] positions;

	/*
	 * Init: getting resources and creating all we will need for drawing
	 */

	private void init() {
		TypedArray colorsArray = getResources().obtainTypedArray(
				R.array.depth_colors);
		TypedArray positionsArray = getResources().obtainTypedArray(
				R.array.depth_colors_positions);
		colors = new int[positionsArray.length()];
		positions = new float[positionsArray.length()];
		for (int i = 0; i < positions.length; i++) {
			positions[i] = positionsArray.getFloat(i, 0f);
			colors[i] = colorsArray.getColor(i, Color.BLACK);
		}
		p = new Paint();
	}

	private static final int MAX_DEPTH = 1000;
	private int mDepth, pre, prepre;

	/**
	 * Provide the depth - the bigger depth - then the darker layout
	 * 
	 * @param depth
	 *            - 0<depth<1000
	 */
	public void setDepth(int depth) {
		prepre = pre;
		pre = mDepth;
		if (depth < 0)
			depth = 0;
		if (depth > MAX_DEPTH)
			depth = MAX_DEPTH;
		mDepth = (2 * pre + 3 * prepre + mBackHeight * depth / MAX_DEPTH) / 6;
		invalidate();
	}

	/*
	 * drawing our gradient and the children lay via calling super
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		LinearGradient g = new LinearGradient(0, -4 * mDepth, 0, 5
				* mBackHeight - 4 * mDepth, colors, positions, TileMode.CLAMP);
		p.setShader(g);
		Rect r = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
		canvas.drawRect(r, p);
		super.onDraw(canvas);
	}

}
