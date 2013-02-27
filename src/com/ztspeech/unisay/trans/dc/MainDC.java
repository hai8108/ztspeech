package com.ztspeech.unisay.trans.dc;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.utils.LogInfo;

public class MainDC extends BaseDC implements OnItemClickListener, OnLongClickListener {
	LinearLayout layout;
	RelativeLayout talk_layout, history_layout, binder_layout, setting_layout, help_layout, exit_layout;
	GridView gridView;
	long lastClick = 0;
	public boolean isClick = false;
	public BaseAdapter mainAdpter = null;
	public String autologinword = null;
	int forDebug = 0;
	/*
	 * <string name="ztsTalk">紫冬口述</string> <string name="history">历史记录</string>
	 * <string name="binder">账号绑定</string> <string name="settxt">我的设置</string>
	 * <string name="help">帮助说明</string> <string name="exit">退出程序</string>
	 */
	public static final int[] icon = new int[] { R.drawable.icon_ztspeech_unisay, R.drawable.iconfavorites,
			R.drawable.icon_binder_count, R.drawable.iconsettings, R.drawable.iconhelp, R.drawable.iconexit };
	public static final int[] iconText = new int[] { R.string.ztsTalk, R.string.history, R.string.binder,
			R.string.settxt, R.string.help, R.string.exit };

	public MainDC(Context context) {
		super(context);
		super.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		layout = (LinearLayout) inflater.inflate(R.layout.main, null);
		addView(layout);
		talk_layout = (RelativeLayout) layout.findViewById(R.id.talk_layout);
		history_layout = (RelativeLayout) layout.findViewById(R.id.history_layout);
		binder_layout = (RelativeLayout) layout.findViewById(R.id.binder_layout);
		setting_layout = (RelativeLayout) layout.findViewById(R.id.setting_layout);
		help_layout = (RelativeLayout) layout.findViewById(R.id.help_layout);
		exit_layout = (RelativeLayout) layout.findViewById(R.id.exit_layout);
		talk_layout.setOnClickListener(this);
		history_layout.setOnClickListener(this);
		binder_layout.setOnClickListener(this);
		setting_layout.setOnClickListener(this);
		help_layout.setOnClickListener(this);
		exit_layout.setOnClickListener(this);
		// gridView = (GridView) layout.findViewById(R.id.CategoryGrid);
		// gridView.setFocusable(true);
		// mainAdpter = new MainAdpter(context, false);
		// gridView.setAdapter(mainAdpter);
		// gridView.setOnItemClickListener(this);
		// layout.setLayoutParams(new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		// LinearLayout.LayoutParams.FILL_PARENT));
	}

	@Override
	public void init(Handler h, int SWidth, int SHeight) {
		super.init(h, SWidth, SHeight);
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
		if (notAnimition()) {
			if (Math.abs(System.currentTimeMillis() - lastClick) > 1000) {
				lastClick = System.currentTimeMillis();
				handler.sendEmptyMessage(position);
			}
			isClick = true;
			mainAdpter.notifyDataSetChanged();
			forDebug = 0;
		}
	}

	@Override
	public void onClicked(View v) {
		switch (v.getId()) {
		case R.id.talk_layout:
			handler.sendEmptyMessage(0);
			break;
		case R.id.history_layout:
			handler.sendEmptyMessage(1);
			break;
		case R.id.binder_layout:
			handler.sendEmptyMessage(2);
			break;
		case R.id.setting_layout:
			handler.sendEmptyMessage(3);
			break;
		case R.id.help_layout:
			handler.sendEmptyMessage(4);
			break;
		case R.id.exit_layout:
			handler.sendEmptyMessage(5);
			break;

		default:
			break;
		}
		if (forDebug >= 3) {
			forDebug++;
		}
		if (forDebug == 5) {
			LogInfo.isDebug = !LogInfo.isDebug;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (forDebug < 3) {
			forDebug++;
		}
		return true;
	}

	class MainAdpter extends BaseAdapter {
		Context context;
		public boolean isClick = false;
		private ViewHolder viewHolder;

		public MainAdpter(Context c, boolean isClick) {
			context = c;
			this.isClick = isClick;
		}

		@Override
		public int getCount() {
			return icon.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LinearLayout layout = new LinearLayout(context);
				layout.setOrientation(LinearLayout.VERTICAL);
				viewHolder.view = new ImageView(context);
				viewHolder.view.setImageResource(icon[position]);
				layout.addView(viewHolder.view);
				layout.setGravity(Gravity.CENTER);
				viewHolder.title = new TextView(context);
				viewHolder.title.setClickable(false);
				viewHolder.title.setGravity(Gravity.CENTER);
				viewHolder.title.setTextColor(Color.WHITE);
				viewHolder.title.setTextSize(20);
				viewHolder.title.setText(context.getString(iconText[position]));
				layout.addView(viewHolder.title);
				convertView = layout;
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.view.setImageResource(icon[position]);
				viewHolder.title.setText(context.getString(iconText[position]));
			}
			return convertView;
		}
	}

	public final class ViewHolder {
		public ImageView view;
		public TextView title;
	}
}
