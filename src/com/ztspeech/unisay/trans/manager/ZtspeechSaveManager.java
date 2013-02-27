package com.ztspeech.unisay.trans.manager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.recognizer.speak.VoicePlayer;
import com.ztspeech.recognizer.speak.interf.OnPlayerListener;
import com.ztspeech.unisay.trans.dc.ZtspeechSaveDC;
import com.ztspeech.unisay.trans.engine.VoiceDBEngine;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;

public class ZtspeechSaveManager extends BaseManager {
	public static final int CONSTANT_RECORD_PLAY = 1;
	public static final int CONSTANT_RECORD_STOP = 2;
	public static final int CONSTANT_RECORD_SAVE = 3;
	public static final int CONSTANT_RECORD_FILE_NOEXIST = 4;
	public static final int CONSTANT_RECORD_PLAY_END = 5;
	public static final int CONSTANT_DB_ITEM_SAVE = 6;

	public ZtspeechSaveDC mainDC;
	private String dcTitle;
	private ZtsSpeechVoiceHistoryModel model;
	private InputStream is;
	private VoicePlayer mPlayer = null;

	@Override
	public void initDC(Context c) {
		context = c;
		if (mainDC == null) {
			mainDC = new ZtspeechSaveDC(context);
		}
		mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		mPlayer = new VoicePlayer(context);
		mPlayer.setListener(mPlayerListener);
	}

	public void setItemModel(ZtsSpeechVoiceHistoryModel model) {
		this.model = model;
	}

	public void setDCTitle(String title) {
		this.dcTitle = title;
	}

	@Override
	protected void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					if (is != null) {
						mPlayer.stop();
					}
					stopRecordVoice();
					back();
					break;
				case CONSTANT_RECORD_PLAY:
					playRecordVoice();
					break;
				case CONSTANT_RECORD_STOP:
					stopRecordVoice();
					break;
				case CONSTANT_RECORD_SAVE:
					saveWrite((ZtsSpeechVoiceHistoryModel) msg.obj);
					break;
				case CONSTANT_RECORD_FILE_NOEXIST:
					stopAutoRecordVoice();
					// Toast.makeText(context, "文件不存在！",
					// Toast.LENGTH_SHORT).show();
					mainDC.showToast("文件不存在！");
					break;
				case CONSTANT_RECORD_PLAY_END:
					stopAutoRecordVoice();
					break;
				case CONSTANT_DB_ITEM_SAVE:
					if ((Boolean) msg.obj) {
						mainDC.showToast("_成功添加到口讯记录_");
					} else {
						mainDC.showToast("_内容添加失败_");
					}
					break;
				default:
					break;
				}
			}
		};
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

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtspeechSaveDC(context);
			mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		}
		mainDC.setItemModel(model);
		mainDC.setAboutTitle(dcTitle);
		enterDC(mainDC);
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
