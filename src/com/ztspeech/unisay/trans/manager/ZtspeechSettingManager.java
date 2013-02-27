package com.ztspeech.unisay.trans.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.unisay.trans.dc.ZtspeechSettingDC;
import com.ztspeech.unisay.trans.main.Application;

public class ZtspeechSettingManager extends BaseManager {
	public ZtspeechSettingDC mainDC;

	@Override
	public void initDC(Context c) {
		context = c;
	}

	@Override
	protected void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					back();
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtspeechSettingDC(context);
			mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		}
		enterDC(mainDC);
	}

}
