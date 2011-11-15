package name.freediving.ui;

import name.freediving.R;
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
import android.widget.RelativeLayout;

/**
 * Layout with custom gradient background
 * 
 * @author DD
 * 
 */
public class DepthRelativeLayout extends RelativeLayout {
	private int[] colors;

	Paint p;

	float[] positions;

	public DepthRelativeLayout(Context context) {
		super(context);
		init(context);
	}

	public DepthRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DepthRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	void init(Context context) {
		// Getting colors from Xml TypedArray
		TypedArray colorsArray = context.getResources().obtainTypedArray(
				R.array.depth_colors);
		TypedArray positionsArray = context.getResources().obtainTypedArray(
				R.array.depth_colors_positions);
		colors = new int[positionsArray.length()];
		positions = new float[positionsArray.length()];
		for (int i = 0; i < positions.length; i++) {
			positions[i] = positionsArray.getFloat(i, 0f);
			colors[i] = colorsArray.getColor(i, Color.BLACK);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

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