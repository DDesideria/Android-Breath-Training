package name.Freediving.ui;

import name.Freediving.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Layout with custom gradient background
 * 
 * @author DD
 * 
 */
public class DepthFrameLayout extends FrameLayout {
	public DepthFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private int[] colors;
	float[] positions;

	void init() {
		// Getting colors from Xml TypedArray
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
	}

	public DepthFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DepthFrameLayout(Context context) {
		super(context);
	}

	Paint p;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float width = MeasureSpec.getSize(widthMeasureSpec);
		float height = MeasureSpec.getSize(heightMeasureSpec);
		// Setting parameters
		float posX = width * 0.5f;
		float posY = height * 0.4f;
		float radius = Math.max(width, height) * 0.46f;
		RadialGradient gradient = new RadialGradient(posX, posY, radius,
				colors, positions, TileMode.CLAMP);
		Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setShader(gradient);
		p.setColor(Color.CYAN);
		canvas.drawPaint(p);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		this.setBackgroundDrawable(bd);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}
}