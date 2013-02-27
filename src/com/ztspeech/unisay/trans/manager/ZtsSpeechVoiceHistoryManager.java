package com.ztspeech.unisay.trans.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.dc.ZtsSpeechVoiceHistoryDC;
import com.ztspeech.unisay.trans.engine.DBEngine;
import com.ztspeech.unisay.trans.engine.VoiceDBEngine;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.widget.ZTSDialog;
import com.ztspeech.unisay.widget.ZTSDialog.TlcyDialogListener;

public class ZtsSpeechVoiceHistoryManager extends BaseManager {

	public static final int CONSTANT_DEL_SEL = 1;
	public static final int CONSTANT_ALL_SEL = 2;
	public static final int CONSTANT_NO_SEL = 3;
	public static final int CONSTANT_EDIT_CONTENT = 4;
	public static final int CONSTANT_DELETE_ITEM_OVER = 5;
	public static final int CONSTANT_DELETE_LOADING = 6;

	public ZtsSpeechVoiceHistoryDC mainDC;
	private ArrayList<ZtsSpeechVoiceHistoryModel> data = new ArrayList<ZtsSpeechVoiceHistoryModel>();
	private Map<String, ArrayList<ZtsSpeechVoiceHistoryModel>> historyMap = new HashMap<String, ArrayList<ZtsSpeechVoiceHistoryModel>>();

	private ZtspeechSaveManager mZtspeechSaveManager;
	private ZtsSpeechHistoryEditDetailsManager mZtsSpeechHistoryEditDetailsManager;
	private ZTSDialog dialog;

	@Override
	public void initDC(Context c) {
		context = c;
		if (mainDC == null) {
			mainDC = new ZtsSpeechVoiceHistoryDC(context, handler);
			mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		}
		mainDC.initData(data);
	}

	private void queryDataByTime() {
		for (int i = 0; i < Application.listNum; i++) {
			historyMap.put(i + "", VoiceDBEngine.getHistoryListBytime(i));
		}
		data = historyMap.get("0");
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
				case CONSTANT_DEL_SEL:
					showDeleteDialog();
					break;
				case CONSTANT_ALL_SEL:
					setSelect(true);
					break;
				case CONSTANT_NO_SEL:
					setSelect(false);
					break;
				case CONSTANT_EDIT_CONTENT:
					saveWrite(msg.arg1);
					break;
				case CONSTANT_DELETE_ITEM_OVER:
					mainDC.dismissLoading();
					mainDC.notifyDataSetChanged();
					break;
				case CONSTANT_DELETE_LOADING:
					mainDC.showLoading("正在删除数据...");
					break;
				default:
					break;
				}
			}
		};
	}

	private void showDeleteDialog() {
		boolean flag = false;
		for (ZtsSpeechVoiceHistoryModel model : data) {
			if (model.checked) {
				flag = true;
				break;
			}
		}
		if (flag) {
			dialog = new ZTSDialog(context).setTitle("提示")
					.setMessage(context.getResources().getString(R.string.deleteHistory))
					.setButton("确定", "取消", new TlcyDialogListener() {
						@Override
						public void onClick() {
							dialog.dismiss();
							handler.sendEmptyMessage(CONSTANT_DELETE_LOADING);
							deleteSelectItem();
						}
					}, null);
			dialog.show();
		} else {
			mainDC.showToast("请选择要删除的记录！");
		}
	}

	private void deleteSelectItem() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = null;
				ArrayList<ZtsSpeechVoiceHistoryModel> dataTmp = new ArrayList<ZtsSpeechVoiceHistoryModel>();
				for (ZtsSpeechVoiceHistoryModel model : data) {
					dataTmp.add(model.clone());
				}
				DBEngine.create();
				for (ZtsSpeechVoiceHistoryModel model : dataTmp) {
					if (model.checked) {
						VoiceDBEngine.deleteById(model.id);
						file = new File(model.recordPath);
						if (file.exists()) {
							file.delete();
						}
						data.remove(model);
					}
				}
				DBEngine.close();
				dataTmp.clear();
				dataTmp = null;
				handler.sendEmptyMessage(CONSTANT_DELETE_ITEM_OVER);
			}
		}).start();
	}

	private void saveWrite(int arg1) {
		// if (mZtspeechSaveManager == null) {
		// mZtspeechSaveManager = new ZtspeechSaveManager();
		// }
		// mZtspeechSaveManager.initDC(context);
		// mZtspeechSaveManager.setItemModel(data.get(arg1));
		// mZtspeechSaveManager.setDCTitle("记录修改");
		// mZtspeechSaveManager.showDC();
		if (mZtsSpeechHistoryEditDetailsManager == null) {
			mZtsSpeechHistoryEditDetailsManager = new ZtsSpeechHistoryEditDetailsManager();
		}
		mZtsSpeechHistoryEditDetailsManager.initDC(context);
		mZtsSpeechHistoryEditDetailsManager.setItemModel(data.get(arg1));
		mZtsSpeechHistoryEditDetailsManager.showDC();
	}

	protected void setSelect(boolean flag) {
		if (flag) {
			for (ZtsSpeechVoiceHistoryModel model : data) {
				model.checked = true;
			}
		} else {
			for (ZtsSpeechVoiceHistoryModel model : data) {
				if (model.checked) {
					model.checked = false;
				} else {
					model.checked = true;
				}
			}
		}
		mainDC.notifyDataSetChanged();
	}

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtsSpeechVoiceHistoryDC(context, handler);
			mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		}
		data = VoiceDBEngine.getHistoryList();
		mainDC.initData(data);
		enterDC(mainDC);
	}

}
