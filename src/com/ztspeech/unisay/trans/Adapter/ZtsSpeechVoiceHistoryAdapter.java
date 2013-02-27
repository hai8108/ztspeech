package com.ztspeech.unisay.trans.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.dc.ZtsSpeechVoiceHistoryDC;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;

public class ZtsSpeechVoiceHistoryAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater = null;
	private List<ZtsSpeechVoiceHistoryModel> data = null;
	private static ViewHolder viewHolder = null;
	private ZtsSpeechVoiceHistoryDC dc;

	public ZtsSpeechVoiceHistoryAdapter(Context context, List<ZtsSpeechVoiceHistoryModel> data,
			ZtsSpeechVoiceHistoryDC dc) {
		this.data = data;
		this.dc = dc;
		layoutInflater = LayoutInflater.from(context);
	}

	public void setData(List<ZtsSpeechVoiceHistoryModel> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		if (data != null && data.size() > 0) {
			return data.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (data != null && data.size() > 0) {
			return data.get(position);
		} else {
			return null;
		}

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
		viewHolder.name.setText((String) data.get(position).title);
		viewHolder.time.setText(data.get(position).time);
		viewHolder.checkBox.setOnClickListener(dc);
		viewHolder.checkBox.setTag(position);

		if (data.get(position).checked == true) {
			viewHolder.checkBox.setChecked(true);
		} else {
			viewHolder.checkBox.setChecked(false);
		}
		return convertView;
	}

	public final class ViewHolder {

		public TextView name;
		public TextView time;
		public ToggleButton checkBox;
	}
}
