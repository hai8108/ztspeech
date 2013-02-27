package com.ztspeech.unisay.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.ztspeech.unisay.utils.LogInfo;

/**
 * 由于Gallery会自动居中等不可控原因,自定义此Gallery,用于分类展示
 */
public class ZTSGallery extends HorizontalScrollView implements OnClickListener {
	Context context;
	int itemWidth = 0;
	int itemDimenWidth = 0;

	public interface TlcyGalleryOnItemClickListener {
		public void onItemClick(int position);
	}

	LayoutInflater inflater;
	LinearLayout layout = null;
	TlcyGalleryOnItemClickListener listener = null;

	/**
	 * 被选中的项
	 */
	int selected = -1;
	/**
	 * 子项数目
	 */
	int childNum = -1;
	/**
	 * 选中的背景
	 */
	int bg;
	/**
	 * 每个项的宽度dimen
	 */
	int widthDimenId;
	/**
	 * 每个项的高度dimen
	 */
	int heightDimenId;

	public ZTSGallery(Context context) {
		super(context);
		this.context = context;
	}

	public ZTSGallery(Context context, AttributeSet set) {
		super(context, set);
		this.context = context;
	}

	public ZTSGallery(Context context, AttributeSet set, int style) {
		super(context, set, style);
		this.context = context;
	}

	/**
	 * 设置回调函数
	 */
	public void setOnItemClickListener(TlcyGalleryOnItemClickListener listener) {
		this.listener = listener;
	}

	/**
	 * 设置分类名称列表 res为Item视图,必须是一个Button,bgRes为选中时的背景
	 */
	public void setAdapter(int res, int bgRes, int itemWidthDimenId, int itemHeightDimenId, String[] texts) {

		if (layout == null) {
			inflater = LayoutInflater.from(getContext());
			layout = new LinearLayout(getContext());
			addView(layout);
		} else {
			layout.removeAllViews();
		}
		bg = bgRes;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		widthDimenId = itemWidthDimenId;
		itemWidth = wm.getDefaultDisplay().getWidth() / texts.length;
		itemDimenWidth = (int) getResources().getDimension(widthDimenId);
		LogInfo.LogOut("info", "itemWidth =" + itemWidth + "    itemDimenWidth =" + itemDimenWidth);
		heightDimenId = itemHeightDimenId;
		if (texts != null) {
			for (int i = 0; i < texts.length; i++) {
				Button button = (Button) inflater.inflate(res, null);
				if (button != null) {
					button.setText(texts[i]);
					button.setTag(i);
					button.setOnClickListener(this);
					layout.addView(button, new LinearLayout.LayoutParams(getItemWidth()/*
																						 * (
																						 * int
																						 * )
																						 * getResources
																						 * (
																						 * )
																						 * .
																						 * getDimension
																						 * (
																						 * widthDimenId
																						 * )
																						 */, (int) getResources()
							.getDimension(heightDimenId)));
					// ((LinearLayout.LayoutParams)button.getLayoutParams()).weight=1;
				}
			}
			childNum = texts.length;
			setSelected(0);
		}
		// else {//走不到这里,不然itemWidth已经异常
		// childNum = -1;
		// setSelected(-1);
		// }

	}

	private int getItemWidth() {
		if (itemWidth > itemDimenWidth) {
			return itemWidth;
		} else {
			return itemDimenWidth;// itemDimenWidth;
		}
	}

	/**
	 * 返回是否按钮太多超出现实范围,需要滚动
	 */
	public boolean isScroll() {
		int width = 0;// getWidth();
		int btnWidth = getItemWidth()/*
									 * (int)
									 * getResources().getDimension(widthDimenId)
									 */;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		if (width < childNum * btnWidth) {
			return true;
		}
		return false;
	}

	/**
	 * 设置某一下为选中状态
	 */
	public void setSelected(int pos) {
		selected = pos;
		for (int i = 0; i < childNum; i++) {
			if (i == selected) {
				layout.getChildAt(i).setBackgroundResource(bg);
				// scrollBy(layout.getChildAt(i).getRight(), 0);
			} else {
				layout.getChildAt(i).setBackgroundResource(0);
			}
		}
	}

	/**
	 * 外部调用setSelected后,需调用此方法滚动到目标选中项
	 */
	public void scroolToSelected() {
		if (selected >= 0 && selected < childNum) {
			int scroolx = getScrollX();
			int width = getWidth();
			int btnWidth = getItemWidth()/*
										 * (int)
										 * getResources().getDimension(widthDimenId
										 * )
										 */;
			int perShowing = width / btnWidth;// 每页能显示的项数
			int maxShowing = (scroolx + width) / btnWidth;// 显示的最大的项数
			if (selected >= maxShowing) {// 选择的在右边,根据具体效果还需修改
				scrollTo((selected - perShowing + 1) * btnWidth, 0);
			} else {
				if ((maxShowing - selected) >= perShowing) {
					scrollTo((selected) * btnWidth, 0);
				}
			}

		}
	}

	public LinearLayout getLayout() {
		return layout;
	}

	/**
	 * 返回选中的序号,-1为没有选中
	 */
	public int getSelected() {
		return selected;
	}

	@Override
	public void onClick(View v) {
		try {
			int pos = Integer.parseInt(v.getTag().toString());
			setSelected(pos);
			if (listener != null) {
				listener.onItemClick(pos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		View view;
		for (int i = 0; i < childNum; i++) {
			view = layout.getChildAt(i);
			if (view != null) {
				view.getLayoutParams().width = getItemWidth()/*
															 * (int)
															 * getResources
															 * ().getDimension
															 * (widthDimenId)
															 */;
				view.getLayoutParams().height = (int) getResources().getDimension(heightDimenId);
				view.requestLayout();
			}
		}
		super.onConfigurationChanged(newConfig);
	}
}
