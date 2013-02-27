package com.ztspeech.unisay.trans.dc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.engine.VoiceDBEngine;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.manager.ZtsSpeechVoiceListManager;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;

public class ZtsSpeechVoiceListDC extends BaseDC implements OnChildClickListener, OnGroupClickListener {
	LinearLayout layout;
	int forDebug = 0;
	private Button back, delSel, allSel, noSel;
	private ExpandableListView listView;
	private List<ZtsSpeechVoiceHistoryModel> data = null;
	private MyExpandableListAdapter myAdapter;
	private Handler handler;
	private Map<String, ArrayList<ZtsSpeechVoiceHistoryModel>> historyMap;
	private int myGroupPosition = 0;
	private Context mContext;

	public ZtsSpeechVoiceListDC(Context context, Handler handler) {
		super(context);
		super.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		mContext = context;
		layout = (LinearLayout) inflater.inflate(R.layout.voice_list_layout, null);
		addView(layout);
		this.handler = handler;
		back = (Button) layout.findViewById(R.id.back);
		delSel = (Button) layout.findViewById(R.id.delSel);
		allSel = (Button) layout.findViewById(R.id.allSel);
		noSel = (Button) layout.findViewById(R.id.noSel);

		listView = (ExpandableListView) layout.findViewById(R.id.voiceHistorylistview);
		listView.setFocusable(true);
		listView.setOnChildClickListener(this);
		listView.setOnGroupClickListener(this);

		back.setOnClickListener(this);
		delSel.setOnClickListener(this);
		allSel.setOnClickListener(this);
		noSel.setOnClickListener(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));

		// 长按事件
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(mContext.getString(R.string.selectmenu));
				menu.add(0, 0, 0, mContext.getString(R.string.del));
			}
		});
	}

	public void initData(Map<String, ArrayList<ZtsSpeechVoiceHistoryModel>> historyMap) {
		this.historyMap = historyMap;
		myAdapter = new MyExpandableListAdapter(context, this);
		myAdapter.setData(historyMap);
		listView.setAdapter(myAdapter);
	}

	public void notifyDataSetChanged() {
		myAdapter.notifyDataSetChanged();
	}

	public void setData(Map<String, ArrayList<ZtsSpeechVoiceHistoryModel>> historyMap) {
		this.historyMap = historyMap;
		myAdapter.setData(historyMap);
	}

	@Override
	public void init(Handler h, int SWidth, int SHeight) {
		super.init(h, SWidth, SHeight);
	}

	@Override
	public void onClicked(View v) {
		viewReset(v);
	}

	public void deleteSelectedItem(int groupPos, int childPos) {
		if (data != null && data.size() > 0) {
			handler.sendMessage(Message.obtain(handler, ZtsSpeechVoiceListManager.CONSTANT_DEL_SEL_LONG_PRESS,
					groupPos, childPos, data));
		}
	}

	public void viewReset(View v) {
		switch (v.getId()) {
		case R.id.back:
			handler.sendEmptyMessage(0);
			break;
		case R.id.delSel:
			if (data != null && data.size() > 0) {
				handler.sendMessage(Message.obtain(handler, ZtsSpeechVoiceListManager.CONSTANT_DEL_SEL, data));
			} else {
				showToast(mContext.getString(R.string.currentcontentisnull));
			}
			break;
		case R.id.allSel:
			if (data != null && data.size() > 0) {
				handler.sendMessage(Message.obtain(handler, ZtsSpeechVoiceListManager.CONSTANT_ALL_SEL, data));
			} else {
				showToast(mContext.getString(R.string.currentcontentisnull));
			}
			break;
		case R.id.noSel:
			if (data != null && data.size() > 0) {
				handler.sendMessage(Message.obtain(handler, ZtsSpeechVoiceListManager.CONSTANT_NO_SEL, data));
			} else {
				showToast(mContext.getString(R.string.currentcontentisnull));
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
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		myGroupPosition = arg2;
		data = historyMap.get(arg2 + "");
		LogInfo.LogOut("haitian", "myGroupPosition =" + myGroupPosition);
		if (data == null || data.size() <= 0) {
			showToast(mContext.getString(R.string.currentitemisnull));
		}
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		handler.sendMessage(Message.obtain(handler, ZtsSpeechVoiceListManager.CONSTANT_EDIT_CONTENT, groupPosition,
				childPosition));
		return false;
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		private Context mContext;
		private ViewHolder viewHolder;
		private LayoutInflater layoutInflater = null;
		private ZtsSpeechVoiceListDC dc;
		Map<String, ArrayList<ZtsSpeechVoiceHistoryModel>> historyMap;

		public void setData(Map<String, ArrayList<ZtsSpeechVoiceHistoryModel>> historyMap) {
			this.historyMap = historyMap;
		}

		public final class ViewHolder {

			public TextView name;
			public TextView time;
			public ToggleButton checkBox;
		}

		MyExpandableListAdapter(Context context, ZtsSpeechVoiceListDC dc) {
			this.dc = dc;
			mContext = context;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return historyMap.get(groupPosition + "").get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			// TODO Auto-generated method stub
			// super.onGroupExpanded(groupPosition);
			for (int i = 0; i < myAdapter.getGroupCount(); i++) {
				// ensure only one expanded Group exists at every time
				if (groupPosition != i && listView.isGroupExpanded(groupPosition)) {
					listView.collapseGroup(i);
				}
			}
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			data = historyMap.get(groupPosition + "");
			if (data == null || data.size() <= 0) {
				return null;
			}
			if (convertView == null) {

				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.voice_history_layout_item, null);
				viewHolder.name = (TextView) convertView.findViewById(R.id.title);
				viewHolder.time = (TextView) convertView.findViewById(R.id.time);

				viewHolder.checkBox = (ToggleButton) convertView.findViewById(R.id.checkbox);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.name.setText((String) data.get(childPosition).title);
			viewHolder.time.setText(data.get(childPosition).time);
			viewHolder.checkBox.setOnClickListener(dc);
			viewHolder.checkBox.setTag(childPosition);

			if (data.get(childPosition).checked == true) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return historyMap.get(groupPosition + "").size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return historyMap.get(groupPosition + "");
		}

		@Override
		public int getGroupCount() {
			return historyMap.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

			if (convertView == null) {

				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.voice_history_parent_layout_item, null);
				viewHolder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (historyMap.get(groupPosition + "") == null || historyMap.get(groupPosition + "").size() <= 0) {
				viewHolder.time.setText(getDayTxt(groupPosition) + "     "
						+ Utils.returnSubDaysDate(Utils.returnNowTime(), groupPosition));
			} else {
				viewHolder.time.setText(getDayTxt(groupPosition) + "     "
						+ historyMap.get(groupPosition + "").get(0).time.substring(0, 10));
			}

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private String getDayTxt(int day) {
			String txtDay = "";
			switch (day) {
			case 0:
				txtDay = mContext.getString(R.string.today);
				break;
			case 1:
				txtDay = mContext.getString(R.string.yesterday);
				break;
			case 2:
				txtDay = mContext.getString(R.string.twodaysbefore);
				break;
			default:
				txtDay = day + mContext.getString(R.string.ndaysbefore);
				break;
			}
			return txtDay;
		}
	}

	private void queryDataByTime() {
		for (int i = 0; i < Application.listNum; i++) {
			historyMap.put(i + "", VoiceDBEngine.getHistoryListBytime(i));
		}
		if (myAdapter != null) {
			myAdapter.setData(historyMap);
			notifyDataSetChanged();
		}
	}

	@Override
	public void onShow() {
		if (Application.isDbListGroupChange) {
			queryDataByTime();
			Application.isDbListGroupChange = false;
		}
	}

}
