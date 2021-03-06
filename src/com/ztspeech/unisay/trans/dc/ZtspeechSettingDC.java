package com.ztspeech.unisay.trans.dc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Handler;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ztspeech.unisay.trans.R;

public class ZtspeechSettingDC extends BaseDC {
	TextView textView;
	Button backButton;
	TextView mainTextView;
	ScrollView scrollView;
	LinearLayout layout;

	public ZtspeechSettingDC(Context context) {
		super(context);
		layout = (LinearLayout) inflater.inflate(R.layout.setting, null);
		addView(layout);
		textView = (TextView) findViewById(R.id.aboutTitle);
		backButton = (Button) findViewById(R.id.aboutBack);
		backButton.setOnClickListener(this);

		scrollView = (ScrollView) findViewById(R.id.aboutScroll);
		mainTextView = (TextView) findViewById(R.id.aboutText);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
	}

	@Override
	public void init(Handler h, int SWidth, int SHeight) {
		super.init(h, SWidth, SHeight);
		scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenHeight * 26 / 30));
		StringBuffer sBuffer = new StringBuffer();
		BufferedReader bReader;
		try {
			// int lunCode = Utils.getLocalLanguage();
			bReader = new BufferedReader(new InputStreamReader(context.getAssets().open("ZtspeechSetting.txt")));
			String tline;
			while ((tline = bReader.readLine()) != null) {
				sBuffer.append(tline + "\n");
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mainTextView.setAutoLinkMask(Linkify.WEB_URLS);
		mainTextView.setText(sBuffer.toString());
		sBuffer = null;
	}

	@Override
	public void onClicked(View v) {
		handler.sendEmptyMessage(0);
	}
}
