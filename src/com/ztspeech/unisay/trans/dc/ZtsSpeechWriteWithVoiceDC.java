package com.ztspeech.unisay.trans.dc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialogXML;
import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.main.Configs;
import com.ztspeech.unisay.trans.manager.ZtsSpeechWriteWithVoiceManager;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;
import com.ztspeech.unisay.widget.ZTSDialog;
import com.ztspeech.unisay.widget.ZTSEditText;

public class ZtsSpeechWriteWithVoiceDC extends BaseDC {
	LinearLayout layout;
	int forDebug = 0;
	private Button back, btn_send_msg, btn_send_mail, add_btn, tohistory;
	private Button btn_speak, btn_copy, btn_clear, btn_share;
	private ZTSEditText mEditResult;
	private ZTSDialog mZtsDialog;
	private PopupWindow mPopupWindow;

	private LayoutInflater mInflater;
	private View contentView;
	private UnisayRecognizerDialogXML mDialogInterface = null;
	private ZtsSpeechVoiceHistoryModel model;
	private String recordTmpFileName = Configs.ztsRecordCache + "tmpRecord.dat";
	private File file;
	private int pos_cursor = 0;
	private int[] leftViewArr = new int[] { R.drawable.main_wave_left_0, R.drawable.main_wave_left_1,
			R.drawable.main_wave_left_2, R.drawable.main_wave_left_3, R.drawable.main_wave_left_4 };
	private int[] RightViewArr = new int[] { R.drawable.main_wave_right_0, R.drawable.main_wave_right_1,
			R.drawable.main_wave_right_2, R.drawable.main_wave_right_3, R.drawable.main_wave_right_4 };
	private int[] waitting_anim = new int[] { R.drawable.nike_gps_mile_marker };

	public ZtsSpeechWriteWithVoiceDC(Context context) {
		super(context);
		super.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		layout = (LinearLayout) inflater.inflate(R.layout.talk_main, null);
		addView(layout);
		back = (Button) layout.findViewById(R.id.back);
		add_btn = (Button) layout.findViewById(R.id.add);
		btn_speak = (Button) layout.findViewById(R.id.btn_speak);
		btn_clear = (Button) layout.findViewById(R.id.btn_clear);
		btn_copy = (Button) layout.findViewById(R.id.btn_copy);
		btn_share = (Button) layout.findViewById(R.id.btn_share);
		tohistory = (Button) layout.findViewById(R.id.tohistory);
		mEditResult = (ZTSEditText) layout.findViewById(R.id.mEditResult);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = mInflater.inflate(R.layout.popup_window, null);
		int[] id_array = new int[] { R.id.imageviewL, R.id.imageviewR, R.id.imageviewanim, R.id.image_btn,
				R.id.imageviewleft, R.id.imageviewright, R.id.text_btn };
		mDialogInterface = new UnisayRecognizerDialogXML(context, "", mRecognizerDialogListener,
				R.layout.rrecognizer_view_layout, id_array);
		// 获得语音识别功能实例对象(带录音对话框)
		btn_send_msg = (Button) layout.findViewById(R.id.btn_send_msg);
		btn_send_mail = (Button) layout.findViewById(R.id.btn_send_email);
		btn_send_msg.setOnClickListener(this);
		btn_send_mail.setOnClickListener(this);
		tohistory.setOnClickListener(this);

		mEditResult.setLineSpacing(7, 1);
		btn_speak.setOnClickListener(this);
		back.setOnClickListener(this);
		add_btn.setOnClickListener(this);
		btn_share.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_copy.setOnClickListener(this);
		mEditResult.setBackgroundResource(R.drawable.background);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		// mEditResult.setOnLongClickListener(this);
		createRecordTmpFile(recordTmpFileName);
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
			LogInfo.LogOut("haitian", "afterTextChanged   pos_cursor =" + pos_cursor);
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
	}

	public void setEditFocus() {
		mEditResult.setFocusableInTouchMode(true);
		mEditResult.setFocusable(true);
		mEditResult.requestFocus();
	}

	@Override
	public void onClicked(View v) {
		viewReset(v);
	}

	public void viewReset(View v) {
		switch (v.getId()) {
		case R.id.back:
			handler.sendEmptyMessage(0);
			break;
		case R.id.tohistory:
			handler.sendEmptyMessage(ZtsSpeechWriteWithVoiceManager.CONSTANT_TO_HISTORYS);
			break;
		case R.id.add:
			createRecordTmpFile(recordTmpFileName);
			String str = mEditResult.getEditableText().toString();
			str = str.trim();
			if (str != null && str.length() > 0) {
				model = new ZtsSpeechVoiceHistoryModel();
				model.title = str.substring(0, str.length() > 5 ? 5 : str.length());// 默认以文本内容前五个汉字为标题
				model.time = Utils.returnNowTime();
				model.content = str;
				model.recordPath = recordTmpFileName;
				model.imageId = "";
				model.imagePath = "";
				model.state = 0;
				handler.sendMessage(Message.obtain(handler, ZtsSpeechWriteWithVoiceManager.CONSTANT_ADD, model));
			}
			break;
		case R.id.btn_speak:
			createRecordTmpFile(recordTmpFileName);
			mDialogInterface.setToContinuous(true);// 连续识别
			str = "192.168.154.3";
			mDialogInterface.setObject(str.getBytes());
			mDialogInterface.show();// 显示录音语音识别对话框
			break;
		case R.id.btn_share:
			str = mEditResult.getEditableText().toString();
			str = str.trim();
			if (str != null && str.length() > 0) {
				showPopWindowForView(v);
			} else {
				showToast(context.getString(R.string.sharecontentisnull));
			}

			break;
		case R.id.btn_clear:
			mEditResult.setText("");
			deleteRecordTmpFile(recordTmpFileName);
			showToast(context.getString(R.string.contentisclear));
			break;
		case R.id.btn_copy:
			str = mEditResult.getEditableText().toString();
			str = str.trim();
			if (str != null && str.length() > 0) {
				showToast(context.getString(R.string.contentiscopytoclip));
				handler.sendMessage(Message.obtain(handler, ZtsSpeechWriteWithVoiceManager.CONSTANT_COPY, str));
			} else {
				showToast(context.getString(R.string.copycontentisnull));
			}

			break;
		case R.id.mEditResult:
			// setEditFocus();
			// InputMethodManager m = (InputMethodManager)
			// context.getSystemService(Context.INPUT_METHOD_SERVICE);
			// m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		case R.id.btn_send_msg:
			str = mEditResult.getEditableText().toString();
			str = str.trim();
			if (str != null && str.length() > 0) {
				handler.sendMessage(Message.obtain(handler, ZtsSpeechWriteWithVoiceManager.CONSTANT_SEND_MSG,
						mEditResult.getEditableText().toString()));
			} else {
				showToast(context.getString(R.string.smscontentisnull));
			}

			break;
		case R.id.btn_send_email:
			str = mEditResult.getEditableText().toString();
			str = str.trim();
			if (str != null && str.length() > 0) {
				handler.sendMessage(Message.obtain(handler, ZtsSpeechWriteWithVoiceManager.CONSTANT_SEND_EMAIL,
						mEditResult.getEditableText().toString()));
			} else {
				showToast(context.getString(R.string.mailcontentisnull));
			}

			break;
		case R.id.iv_tencent:
		case R.id.iv_sohu:
		case R.id.iv_sina:
		case R.id.iv_more:
			mPopupWindow.dismiss();
			handler.sendMessage(Message.obtain(handler, ZtsSpeechWriteWithVoiceManager.CONSTANT_SEND_WEIBO, v.getId(),
					v.getId(), mEditResult.getEditableText().toString()));
			break;
		default:
			break;
		}
	}

	public void clearTextDisp() {
		mEditResult.setText("");
		model = null;
	}

	private void createRecordTmpFile(String recordTmpFileName) {
		try {
			mEditResult.addTextChangedListener(watcher);
			file = new File(recordTmpFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void deleteRecordTmpFile(String recordTmpFileName) {
		try {
			file = new File(recordTmpFileName);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showPopWindowForView(View view) {
		int[] arrayOfInf = new int[2];
		view.getLocationInWindow(arrayOfInf);
		int x = arrayOfInf[0];
		int y = arrayOfInf[1] - 88;

		mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 如果希望点击其他的位置popupwindow自动消失必须设置这两项： 1 setFoucable(true) 2
		// setBackground();
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(200);
		contentView.setAnimation(animation);

		mPopupWindow.showAtLocation(view, 50, x, y);

		ImageView iv_sina = (ImageView) contentView.findViewById(R.id.iv_sina);
		ImageView iv_tencent = (ImageView) contentView.findViewById(R.id.iv_tencent);
		ImageView iv_sohu = (ImageView) contentView.findViewById(R.id.iv_sohu);
		ImageView iv_more = (ImageView) contentView.findViewById(R.id.iv_more);
		iv_sina.setOnClickListener(this);
		iv_tencent.setOnClickListener(this);
		iv_sohu.setOnClickListener(this);
		iv_more.setOnClickListener(this);
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
					Utils.saveVoiceData(recordTmpFileName, voiceArray);
				}
			}).start();
		}

		public void onEngineStart() {

		}

		public void onEngineEnd() {

		}
	};
}
