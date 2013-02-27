package com.ztspeech.unisay.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

/**
 * 信纸样式EditText
 * 
 * @author haitian
 * 
 */
public class ZTSEditText extends EditText {
	// private Context mContext;
	private int mLineColor;
	private Bitmap mBitmap;

	// private final String PACKAGE_NAME = "zkj.HttpTest";

	public ZTSEditText(Context context) {
		super(context);
		// mContext = context;
	}

	public ZTSEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// mContext = context;
		// 获得自定义属性资源id

		// 第一个参数：spaceName

		// 第二个参数：属性名

		// 第三个参数：如果属性不存在则要使用的默认值
		// int resourceId = attrs.getAttributeResourceValue(PACKAGE_NAME,
		// "backgroud", Color.GREEN);

		// 得到id对应的颜色值
		// mLineColor = getResources().getColor(resourceId);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 设置分割线颜色
		Paint _paint = getPaint();
		_paint.setColor(Color.BLACK);
		if (mLineColor != 0) {
			_paint.setColor(mLineColor);
		}
		// if (mLineSrc != 0) {
		// mBitmap = BitmapFactory.decodeResource(getResources(), mLineSrc);
		// }
		int _lineHeight = this.getLineHeight();
		int _topPadding = this.getPaddingTop();
		int _leftPadding = this.getPaddingLeft();
		float _textSize = this.getTextSize();
		setGravity(Gravity.LEFT | Gravity.TOP);
		int y = (int) (_topPadding + _textSize);
		int _num = this.getHeight() / _lineHeight;
		int _lineCount = this.getLineCount();
		if (_num > _lineCount) {
			for (int i = 0; i < _lineCount; i++) {
				canvas.drawLine(_leftPadding, y + 5, getRight() - _leftPadding, y + 5, _paint);
				y += _lineHeight;
			}
			for (int i = _lineCount + 1; i < _num; i++) {
				canvas.drawLine(_leftPadding, y + 5, getRight() - _leftPadding, y + 5, _paint);
				y += _lineHeight;
			}
		} else {
			for (int i = 0; i < _lineCount; i++) {
				canvas.drawLine(_leftPadding, y + 5, getRight() - _leftPadding, y + 5, _paint);
				y += _lineHeight;
			}
		}
		//
		// }
		// if (_lineCount > _num) {
		// for (int i = 0; i < _lineCount; i++) {
		// // canvas.drawBitmap(mBitmap, _leftPadding, y + 8, _paint);
		// canvas.drawLine(_leftPadding, y+5, getRight()-_leftPadding, y+5,
		// _paint);
		// y += _lineHeight;
		// }
		// } else {
		// for (int i = 0; i < _num; i++) {
		// canvas.drawLine(_leftPadding, y+5, getRight()-_leftPadding, y+5,
		// _paint);
		// // canvas.drawBitmap(mBitmap, _leftPadding, y + 8, _paint);
		// y += _lineHeight;
		// }
		// }
		canvas.translate(0, 0);
		super.onDraw(canvas);
	}

	/**
	 * 设置记事本的编辑框背景线条颜色
	 * 
	 * @param color
	 *            int type【代表颜色的整数】
	 */
	public void setLineColor(int color) {
		this.mLineColor = color;
		invalidate();
	}

	public void recycledBitmap() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.isRecycled();
			mBitmap = null;
		}
		System.gc();
	}

	/**
	 * 设置记事本的编辑框背景线条颜色
	 * 
	 * @param colorId
	 *            int type【代表颜色的资源id】
	 */
	public void setLineColorId(int colorId) {
		this.mLineColor = getResources().getColor(colorId);
		invalidate();
	}
}
