package com.ztspeech.unisay.trans.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.dc.ZtspeechBinderCountDC;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.weibo.IRenren;
import com.ztspeech.unisay.weibo.ISina;
import com.ztspeech.unisay.widget.ZTSDialog;
import com.ztspeech.unisay.widget.ZTSDialog.TlcyDialogListener;

public class ZtspeechBinderCountManager extends BaseManager {
	public static final int CONSTANT_SINA_WEIBO_COUNT = 1;
	public static final int CONSTANT_TENCENT_WEIBO_COUNT = 2;
	public static final int CONSTANT_RENREN_WEIBO_COUNT = 3;

	public ZtspeechBinderCountDC mainDC;
	private ZTSDialog dialog;

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
				case CONSTANT_SINA_WEIBO_COUNT:
					boolean isSinaCountBinder = (Boolean) msg.obj;
					if (isSinaCountBinder) {
						unBinderSinaCount();
					} else {
						Application.iSina.bindSina(handler);
					}
					break;
				case CONSTANT_TENCENT_WEIBO_COUNT:
					boolean isTencentCountBinder = (Boolean) msg.obj;
					if (isTencentCountBinder) {
						unBinderCount();
					} else {
						Application.iTencent.bindTencent();
					}
					break;
				case CONSTANT_RENREN_WEIBO_COUNT:
					boolean isRenrenCountBinder = (Boolean) msg.obj;
					if (isRenrenCountBinder) {
						unBinderRenrenCount();
					} else {
						Application.iRenren.bindRenren(context, handler);
					}
					break;
				case ISina.BIND_SINA_CODE:
					mainDC.showToast((String) msg.obj);
					break;
				case IRenren.BIND_RENREN_CODE:
					if (msg.arg1 == 200 && msg.arg2 == 200) {
						mainDC.showToast("账号绑定成功！");
					} else if (msg.arg1 == 404 && msg.arg2 == 404) {
						mainDC.showToast("账号绑定失败！");
					}
					break;
				default:
					break;
				}
			}
		};
	}

	private void unBinderCount() {
		dialog = new ZTSDialog(context)
				.setTitle(context.getString(R.string.tip))
				.setMessage(context.getString(R.string.unbindertencent))
				.setButton(context.getString(R.string.ok), context.getString(R.string.cancel),
						new TlcyDialogListener() {
							@Override
							public void onClick() {
								dialog.dismiss();
								SharedPreferences sp = context.getSharedPreferences("tencent", 0);
								sp.edit().putString("isBind", "no").commit();
								Application.iTencent.setOAuth();
								mainDC.updateWeiboCountTxt();
							}
						}, null);
		dialog.show();

	}

	private void unBinderSinaCount() {
		dialog = new ZTSDialog(context)
				.setTitle(context.getString(R.string.tip))
				.setMessage(context.getString(R.string.unbindersina))
				.setButton(context.getString(R.string.ok), context.getString(R.string.cancel),
						new TlcyDialogListener() {
							@Override
							public void onClick() {
								dialog.dismiss();
								SharedPreferences sp = context.getSharedPreferences("sina", 0);
								sp.edit().putString("isBind", "no").commit();
								Application.iSina.setO2at();
								mainDC.updateWeiboCountTxt();
							}
						}, null);
		dialog.show();

	}

	private void unBinderRenrenCount() {
		dialog = new ZTSDialog(context)
				.setTitle(context.getString(R.string.tip))
				.setMessage(context.getString(R.string.unbinderrenren))
				.setButton(context.getString(R.string.ok), context.getString(R.string.cancel),
						new TlcyDialogListener() {
							@Override
							public void onClick() {
								dialog.dismiss();
								Application.iRenren.unBinderRenren(context);
								mainDC.updateWeiboCountTxt();
							}
						}, null);
		dialog.show();
	}

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtspeechBinderCountDC(context);
			mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		}
		mainDC.updateWeiboCountTxt();
		enterDC(mainDC);
	}

}
