package com.ztspeech.unisay.trans.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tencent.weibo.oauthv1.OAuthV1;
import com.ztspeech.unisay.trans.dc.ZtspeechSinaWeiboDC;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.weibo.ISina;
import com.ztspeech.weibo.sdk.android.WeiboAuthListener;

public class ZtspeechSinaWeiboManager extends BaseManager {
	public final static int CONSTENT_TENCENT_WEIBO_REGIST = 1;
	public final static int CONSTANT_LOADING_WEB_URL = 2;
	public final static int CONSTENT_SINA_WEIBO_REGIST = 3;
	public final static int BIND_SINA_CODE = 4;

	public ZtspeechSinaWeiboDC mainDC;
	private OAuthV1 oAuth = null;

	private String url;
	private String title;
	private WeiboAuthListener listener;
	private boolean isBinder = false;

	public void setWeiboAuthListener(WeiboAuthListener listener) {
		this.listener = listener;
	}

	public void isBinderOrSend(boolean isBinder) {
		this.isBinder = isBinder;
	}

	@Override
	public void initDC(Context c) {
		context = c;
	}

	@Override
	public void back() {
		mainDC.clearSelf();
		super.back();
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
				case CONSTENT_TENCENT_WEIBO_REGIST:
					mainDC.dismissLoading();
					back();
					break;
				case CONSTENT_SINA_WEIBO_REGIST:
					break;
				case CONSTANT_LOADING_WEB_URL:
					mainDC.loadURL(url);
					break;
				case ISina.BIND_SINA_CODE:
					mainDC.dismissLoading();
					if (isBinder) {
						back();
					} else {
						Application.iSina.sendWeibo();
						back();
					}
					break;
				case ISina.SEND_SINA_CODE:
					mainDC.dismissLoading();

					break;
				default:
					break;
				}
			}
		};
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void changeTitle(String title) {
		this.title = title;
	}

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtspeechSinaWeiboDC(context, handler);
		}
		mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		mainDC.setWeiboAuthListener(listener);
		mainDC.changeTitle(title);
		enterDC(mainDC);
		handler.sendMessageDelayed(Message.obtain(handler, CONSTANT_LOADING_WEB_URL), 100);

	}
}
