package com.ztspeech.unisay.trans.dc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.manager.ZtspeechBinderCountManager;

public class ZtspeechBinderCountDC extends BaseDC {
	TextView title, sina_count_msg, tencent_count_msg, renren_count_msg;
	Button backButton, sina_count, tencent_count, renren_count;
	TextView mainTextView;
	ScrollView scrollView;
	LinearLayout layout;
	private SharedPreferences sp;
	private boolean isTencentCountBinder = false;
	private boolean isSinaCountBinder = false;
	private boolean isRenrenCountBinder = false;

	public ZtspeechBinderCountDC(Context context) {
		super(context);
		layout = (LinearLayout) inflater.inflate(R.layout.binder_count, null);
		addView(layout);
		title = (TextView) findViewById(R.id.binderTitle);
		sina_count_msg = (TextView) findViewById(R.id.sina_count_msg);
		tencent_count_msg = (TextView) findViewById(R.id.tencent_count_msg);
		renren_count_msg = (TextView) findViewById(R.id.renren_count_msg);
		backButton = (Button) findViewById(R.id.back);
		sina_count = (Button) findViewById(R.id.sina_count);
		tencent_count = (Button) findViewById(R.id.tencent_count);
		renren_count = (Button) findViewById(R.id.renren_count);
		backButton.setOnClickListener(this);
		sina_count.setOnClickListener(this);
		renren_count.setOnClickListener(this);
		tencent_count.setOnClickListener(this);
		scrollView = (ScrollView) findViewById(R.id.aboutScroll);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
	}

	public void updateWeiboCountTxt() {
		sp = context.getSharedPreferences("tencent", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			tencent_count.setText(R.string.de_tencent_binder);
			isTencentCountBinder = true;
		} else {
			tencent_count.setText(R.string.tencent_binder);
			isTencentCountBinder = false;
		}

		sp = context.getSharedPreferences("sina", 0);
		isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			sina_count.setText(R.string.de_sina_binder);
			isSinaCountBinder = true;
		} else {
			sina_count.setText(R.string.sina_binder);
			isSinaCountBinder = false;
		}

		if (Application.iRenren.isBinder()) {
			renren_count.setText(R.string.de_renren_binder);
			isRenrenCountBinder = true;
		} else {
			renren_count.setText(R.string.renren_binder);
			isRenrenCountBinder = false;
		}
	}

	@Override
	public void onShow() {
		updateWeiboCountTxt();
	}

	@Override
	public void init(Handler h, int SWidth, int SHeight) {
		super.init(h, SWidth, SHeight);
	}

	@Override
	public void onClicked(View v) {
		switch (v.getId()) {
		case R.id.back:
			handler.sendEmptyMessage(0);
			break;
		case R.id.sina_count:
			handler.sendMessage(Message.obtain(handler, ZtspeechBinderCountManager.CONSTANT_SINA_WEIBO_COUNT,
					isSinaCountBinder));
			break;
		case R.id.tencent_count:
			handler.sendMessage(Message.obtain(handler, ZtspeechBinderCountManager.CONSTANT_TENCENT_WEIBO_COUNT,
					isTencentCountBinder));
			break;
		case R.id.renren_count:
			handler.sendMessage(Message.obtain(handler, ZtspeechBinderCountManager.CONSTANT_RENREN_WEIBO_COUNT,
					isRenrenCountBinder));
			break;
		default:
			break;
		}
	}
}
