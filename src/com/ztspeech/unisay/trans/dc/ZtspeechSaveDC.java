package com.ztspeech.unisay.trans.dc;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizer.interf.RecognizerDialogInterface;
import com.ztspeech.recognizerDialog.RecognizerDialog;
import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.manager.ZtspeechSaveManager;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.utils.Utils;
import com.ztspeech.unisay.widget.ZTSEditText;

public class ZtspeechSaveDC extends BaseDC {
	private EditText edit_title;
	private TextView aboutTitle;
	private Button backButton, add_btn, btn_speak, btn_play;
	private ZTSEditText mEditResult;
	private LinearLayout layout;
	private RecognizerDialogInterface mDialogInterface = null;
	private boolean isPlay = false;
	private ZtsSpeechVoiceHistoryModel model;
	private String recordFileName;
	private File file;
	// private ZTSDialog mZtsDialog;
	private int pos_cursor = 0;
	private long lastClickTime = 0;

	public ZtspeechSaveDC(Context context) {
		super(context);
		layout = (LinearLayout) inflater.inflate(R.layout.save_write, null);
		addView(layout);
		edit_title = (EditText) findViewById(R.id.edit_title);
		aboutTitle = (TextView) findViewById(R.id.aboutTitle);
		backButton = (Button) findViewById(R.id.aboutBack);
		btn_speak = (Button) findViewById(R.id.btn_speak);
		btn_play = (Button) findViewById(R.id.btn_play);
		add_btn = (Button) findViewById(R.id.add);
		backButton.setOnClickListener(this);
		btn_speak.setOnClickListener(this);
		btn_play.setOnClickListener(this);
		add_btn.setOnClickListener(this);
		backButton.setFocusable(false);
		isPlay = false;
		btn_play.setText(R.string.play);
		mDialogInterface = new RecognizerDialog(context, "", mRecognizerDialogListener);// 获得语音识别功能实例对象(带录音对话框)
		mEditResult = (ZTSEditText) findViewById(R.id.mEditResult);
		mEditResult.setBackgroundResource(R.drawable.background);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		// mEditResult.setOnLongClickListener(this);
		// mZtsDialog = new ZTSDialog(context).setTitle("").setItems(new
		// String[] { "粘贴" }, this).setNoButton();
	}

	// 侦听EditText字数改变
	TextWatcher watcher = new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			textCountSet();
			pos_cursor = mEditResult.getSelectionStart();
		}
	};

	private void textCountSet() {
		String textContent = mEditResult.getText().toString();
		int currentLength = textContent.length();
		if (currentLength <= 0) {
			if (file.exists()) {
				file.delete();
			}
		}
	}

	@Override
	public void init(Handler h, int SWidth, int SHeight) {
		super.init(h, SWidth, SHeight);
		isPlay = false;
		btn_play.setText(R.string.play);
	}

	@Override
	public void onClicked(View v) {
		switch (v.getId()) {
		case R.id.aboutBack:
			handler.sendEmptyMessage(0);
			break;
		case R.id.add:
			if (Math.abs(System.currentTimeMillis() - lastClickTime) > 1000) {
				lastClickTime = System.currentTimeMillis();
				String strTitle = edit_title.getEditableText().toString();
				String str = mEditResult.getEditableText().toString();
				str = str.trim();
				strTitle = strTitle.trim();
				if (str != null && str.length() > 0) {
					model.content = str;
					if (strTitle != null && strTitle.length() > 0) {
						model.title = strTitle;
						model.time = Utils.returnNowTime();
						handler.sendMessage(Message.obtain(handler, ZtspeechSaveManager.CONSTANT_RECORD_SAVE, model));
					} else {
						showToast(context.getString(R.string.titleisnull));
					}
				} else {
					showToast(context.getString(R.string.savecontentisnull));
				}
			}
			break;
		case R.id.btn_speak:
			mDialogInterface.setToContinuous(true);// 连续识别
			mDialogInterface.show();// 显示录音语音识别对话框
			break;
		case R.id.btn_play:
			if (!isPlay) {
				startPlay();
				handler.sendEmptyMessage(ZtspeechSaveManager.CONSTANT_RECORD_PLAY);
			} else {
				stopPlay();
				handler.sendEmptyMessage(ZtspeechSaveManager.CONSTANT_RECORD_STOP);
			}
			break;
		default:
			break;
		}
	}

	private void startPlay() {
		isPlay = true;
		btn_play.setText(R.string.stop);
		btn_speak.setEnabled(false);
		add_btn.setEnabled(false);
	}

	public void stopPlay() {
		isPlay = false;
		btn_play.setText(R.string.play);
		btn_speak.setEnabled(true);
		add_btn.setEnabled(true);
	}

	public void setItemModel(ZtsSpeechVoiceHistoryModel model) {
		this.model = model;
		recordFileName = model.recordPath;
		mEditResult.setText(model.content);
		edit_title.setText(model.title);
		file = new File(model.recordPath);
		mEditResult.addTextChangedListener(watcher);
	}

	public void setAboutTitle(String msg) {
		if (msg != null && msg.trim().length() > 0) {
			aboutTitle.setText(msg);
		}
	}

	/**
	 * 语音识别回调监听
	 */
	private OnEngineListener mRecognizerDialogListener = new OnEngineListener() {

		public void onEngineResult(ArrayList list, int flag) {// 语音识别完成后，返回识别结果，回调该方法
			// TODO Auto-generated method stub
			if (list == null) {
				return;
			}
			StringBuffer tmpsb = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				tmpsb.append((CharSequence) list.get(i));
			}
			pos_cursor = mEditResult.getSelectionStart();
			mEditResult.getEditableText().insert(pos_cursor, tmpsb.toString());
			new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] voiceArray = (byte[]) mDialogInterface.getObject();
					Utils.saveVoiceData(recordFileName, voiceArray);
				}
			}).start();
		}

		public void onEngineStart() {

		}

		public void onEngineEnd() {

		}
	};

	// @Override
	// public boolean onLongClick(View v) {
	// LogInfo.LogOut("haitian", "onLongClick");
	// mZtsDialog.show();
	// return true;
	// }
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long
	// arg3) {
	// mZtsDialog.cancel();
	// if (Application.copyTxt != null && Application.copyTxt.length() > 0) {
	// pos_cursor = mEditResult.getSelectionStart();
	// LogInfo.LogOut("haitian", "pos_cursor =" + pos_cursor);
	// mEditResult.getEditableText().insert(pos_cursor, Application.copyTxt);
	// }
	// LogInfo.LogOut("haitian", "arg2:" + arg2);
	// }
}
