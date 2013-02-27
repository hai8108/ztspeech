package com.ztspeech.unisay.trans.dc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.Adapter.ZtsSpeechVoiceHistoryAdapter;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.manager.ZtsSpeechVoiceHistoryManager;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;

public class ZtsSpeechVoiceHistoryDC extends BaseDC implements OnItemClickListener {
	LinearLayout layout;
	int forDebug = 0;
	private Button back, delSel, allSel, noSel;
	private ListView listView;
	private List<ZtsSpeechVoiceHistoryModel> data = null;
	private ZtsSpeechVoiceHistoryAdapter myAdapter;
	private Handler handler;

	public ZtsSpeechVoiceHistoryDC(Context context, Handler handler) {
		super(context);
		super.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		layout = (LinearLayout) inflater.inflate(R.layout.voice_history_layout, null);
		addView(layout);
		this.handler = handler;
		back = (Button) layout.findViewById(R.id.back);
		delSel = (Button) layout.findViewById(R.id.delSel);
		allSel = (Button) layout.findViewById(R.id.allSel);
		noSel = (Button) layout.findViewById(R.id.noSel);

		listView = (ListView) layout.findViewById(R.id.voiceHistorylistview);
		listView.setFocusable(true);
		listView.setOnItemClickListener(this);

		back.setOnClickListener(this);
		delSel.setOnClickListener(this);
		allSel.setOnClickListener(this);
		noSel.setOnClickListener(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
	}

	public void initData(ArrayList<ZtsSpeechVoiceHistoryModel> data) {
		this.data = data;
		myAdapter = new ZtsSpeechVoiceHistoryAdapter(context, this.data, this);
		listView.setAdapter(myAdapter);
	}

	public void notifyDataSetChanged() {
		myAdapter.notifyDataSetChanged();
	}

	public void setData(ArrayList<ZtsSpeechVoiceHistoryModel> data) {
		this.data = data;
		myAdapter.setData(data);
	}

	@Override
	public void init(Handler h, int SWidth, int SHeight) {
		super.init(h, SWidth, SHeight);
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
		case R.id.delSel:
			if (data != null && data.size() > 0) {
				handler.sendEmptyMessage(ZtsSpeechVoiceHistoryManager.CONSTANT_DEL_SEL);
			} else {
				showToast(context.getString(R.string.currentcontentisnull));
			}
			break;
		case R.id.allSel:
			if (data != null && data.size() > 0) {
				handler.sendEmptyMessage(ZtsSpeechVoiceHistoryManager.CONSTANT_ALL_SEL);
			} else {
				showToast(context.getString(R.string.currentcontentisnull));
			}
			break;
		case R.id.noSel:
			if (data != null && data.size() > 0) {
				handler.sendEmptyMessage(ZtsSpeechVoiceHistoryManager.CONSTANT_NO_SEL);
			} else {
				showToast(context.getString(R.string.currentcontentisnull));
			}
			break;
		case R.id.checkbox:
			int position = (Integer) v.getTag();
			if (data.get(position).checked) {
				data.get(position).checked = false;
			} else {
				data.get(position).checked = true;
			}
			notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		handler.sendMessage(Message.obtain(handler, ZtsSpeechVoiceHistoryManager.CONSTANT_EDIT_CONTENT, arg2,
				(int) arg3));
	}

}
