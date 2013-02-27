package com.ztspeech.unisay.trans.manager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;

import com.ztspeech.recognizer.speak.VoicePlayer;
import com.ztspeech.recognizer.speak.interf.OnPlayerListener;
import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.dc.ZtsSpeechHistoryEditDetailsDC;
import com.ztspeech.unisay.trans.engine.VoiceDBEngine;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;

public class ZtsSpeechHistoryEditDetailsManager extends BaseManager {

	public final static int CONSTANT_SEND_MSG = 1;
	public final static int CONSTANT_SEND_EMAIL = 2;
	public final static int CONSTANT_SEND_WEIBO = 3;
	public final static int CONSTANT_ADD = 4;
	public final static int CONSTANT_COPY = 5;
	public final static int CONSTANT_DB_ITEM_SAVE = 6;

	public static final int CONSTANT_RECORD_PLAY = 11;
	public static final int CONSTANT_RECORD_STOP = 12;
	public static final int CONSTANT_RECORD_SAVE = 13;
	public static final int CONSTANT_RECORD_FILE_NOEXIST = 14;
	public static final int CONSTANT_RECORD_PLAY_END = 15;

	public ZtsSpeechHistoryEditDetailsDC mainDC;
	private ZtspeechWeiboManager mZtspeechWeiboManager;
	private File file;
	private InputStream is;
	private VoicePlayer mPlayer = null;
	private ZtsSpeechVoiceHistoryModel model;

	@Override
	public void initDC(Context c) {
		context = c;
		if (mainDC == null) {
			mainDC = new ZtsSpeechHistoryEditDetailsDC(context);
		}
		mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		mPlayer = new VoicePlayer(context);
		mPlayer.setListener(mPlayerListener);
		if (mZtspeechWeiboManager == null) {
			mZtspeechWeiboManager = new ZtspeechWeiboManager();
		}
	}

	public void setItemModel(ZtsSpeechVoiceHistoryModel model) {
		this.model = model;
	}

	@Override
	public void back() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				LogInfo.LogOut("haitian", "-----------------------------");
				VoiceDBEngine.updateById(mainDC.getItemModel());
			}
		}).start();

		if (is != null) {
			mPlayer.stop();
		}
		stopRecordVoice();
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
				case CONSTANT_SEND_MSG:// sms
					sendSMS((String) msg.obj);
					break;
				case CONSTANT_SEND_EMAIL:// mail
					sendEmail((String) msg.obj);
					break;
				case CONSTANT_SEND_WEIBO:
					shareWeibo(msg.arg1, (String) msg.obj);
					break;
				case CONSTANT_COPY:
					copyText((String) msg.obj);
					break;
				case CONSTANT_DB_ITEM_SAVE:
					mainDC.dismissLoading();
					if ((Boolean) msg.obj) {
						Application.isDbListGroupChange = true;
						mainDC.showToast(context.getString(R.string.successaddtohistory));
					} else {
						mainDC.showToast(context.getString(R.string.failedaddtohistory));
					}
					break;
				case CONSTANT_RECORD_PLAY:
					playRecordVoice();
					break;
				case CONSTANT_RECORD_STOP:
					stopRecordVoice();
					break;
				case CONSTANT_RECORD_SAVE:
					mainDC.showLoading(context.getString(R.string.savedataloading));
					saveWrite((ZtsSpeechVoiceHistoryModel) msg.obj);
					break;
				case CONSTANT_RECORD_FILE_NOEXIST:
					stopAutoRecordVoice();
					mainDC.showToast(context.getString(R.string.filenotexist));
					break;
				case CONSTANT_RECORD_PLAY_END:
					stopAutoRecordVoice();
					break;
				default:
					break;
				}
			}
		};
	}

	private void copyText(String str) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(str);
	}

	private void shareWeibo(int arg1, String msg) {
		if (arg1 != R.id.iv_more) {
			if (mZtspeechWeiboManager == null) {
				mZtspeechWeiboManager = new ZtspeechWeiboManager();
			}
			mZtspeechWeiboManager.initDC(context);
			mZtspeechWeiboManager.showDC();
		} else {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("*/*");
			// intent.setType("text/plain"); //文字分享
			/*
			 * 图片分享 　　　　it.setType("image/png"); 　　　　　//添加图片 　　　　 File f = new
			 * File(Environment.getExternalStorageDirectory()+"/name.png"); 　Uri
			 * uri = Uri.fromFile(f); 　　　　 intent.putExtra(Intent.EXTRA_STREAM,
			 * uri); 　　　　　
			 */
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			intent.putExtra(Intent.EXTRA_TEXT, msg);
			context.startActivity(Intent.createChooser(intent, "紫冬口述"));
		}
	}

	private void saveWrite(final ZtsSpeechVoiceHistoryModel model) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean flag = VoiceDBEngine.updateById(model);
				handler.sendMessage(Message.obtain(handler, CONSTANT_DB_ITEM_SAVE, flag));
			}
		}).start();
	}

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtsSpeechHistoryEditDetailsDC(context);
		}
		mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		mainDC.setItemModel(model);
		enterDC(mainDC);
	}

	private void sendEmail(String msg) {
		// TODO Auto-generated method stub
		Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
		mailIntent.setType("plain/test");

		mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		mailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
		mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
		context.startActivity(Intent.createChooser(mailIntent, "发送邮件"));
	}

	private void sendSMS(String msg) {
		// TODO Auto-generated method stub
		Uri smsToUri = Uri.parse("smsto:123456");

		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		sendIntent.putExtra("sms_body", msg);
		sendIntent.setType("vnd.android-dir/mms-sms");
		context.startActivity(sendIntent);
	}

	/**
	 * 播放录音
	 */
	private void playRecordVoice() {
		try {
			LogInfo.LogOut("haitian", model.recordPath);
			File temp = new File(model.recordPath);
			if (!temp.exists()) {
				handler.sendEmptyMessage(CONSTANT_RECORD_FILE_NOEXIST);
				return;
			}
			is = new ByteArrayInputStream(Utils.getData(new FileInputStream(temp)));
			mPlayer.play(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopRecordVoice() {
		try {
			mPlayer.stop();
			mainDC.stopPlay();
			if (is != null) {
				is.close();
				is = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopAutoRecordVoice() {
		try {
			mainDC.stopPlay();
			if (is != null) {
				is.close();
				is = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private OnPlayerListener mPlayerListener = new OnPlayerListener() {

		public InputStream getPlayWaveData(String text) {
			return null;
		}

		public void onPlayStart() {
		}

		public void onPlayStop() {
			handler.sendEmptyMessage(CONSTANT_RECORD_PLAY_END);
		}

		public void onPlayLoadDataStart() {
		}

		public void onPlayLoadDataEnd() {
		}
	};
}
