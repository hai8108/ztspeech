package com.ztspeech.unisay.widget;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.model.ZtspeechSmsInfoModel;

public class ZTSSmsSendDialog extends Dialog implements OnClickListener {
	public interface ZTSSmsSendDialogListener {
		public void onClick();
	}

	private TextView titleText;
	private Button cancelBtn;
	private Button moreBtn;
	private Button okBtn;
	private RelativeLayout bottomLayout01;
	private RelativeLayout footLayout;
	private ZTSSmsSendDialogListener okListener;
	private ZTSSmsSendDialogListener cancelLisenter;

	private ListView listview;
	private MyAdpter myAdapter;
	private ArrayList<ZtspeechSmsInfoModel> datas = null;

	public ZTSSmsSendDialog(Context context, OnCheckedChangeListener mOnCheckedChangeListener,
			View.OnClickListener mOnClickListener) {
		super(context, R.style.dialog);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.sms_list_layout);
		titleText = (TextView) findViewById(R.id.title);
		titleText.setText(context.getString(R.string.tip));
		cancelBtn = (Button) findViewById(R.id.BtnCancel);
		okBtn = (Button) findViewById(R.id.BtnOK);
		listview = (ListView) findViewById(R.id.smsList);
		footLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.listview_foot_button, null);
		moreBtn = (Button) footLayout.findViewById(R.id.btnMore);
		moreBtn.setOnClickListener(mOnClickListener);
		listview.addFooterView(footLayout);
		myAdapter = new MyAdpter(context, mOnCheckedChangeListener);
		listview.setAdapter(myAdapter);

		bottomLayout01 = (RelativeLayout) findViewById(R.id.layoutBottom01);
		cancelBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		okBtn.setText(context.getString(R.string.OK));
		cancelBtn.setText(context.getString(R.string.CANCEL));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.BtnOK:
			dismiss();
			if (okListener != null) {
				okListener.onClick();
			}
			break;
		case R.id.BtnCancel:
			dismiss();
			if (cancelLisenter != null) {
				cancelLisenter.onClick();
			}
			break;
		case R.id.btnMore:
			break;
		}
	}

	/**
	 * 
	 * 设置对话框的标题
	 */
	public ZTSSmsSendDialog setTitle(String title) {
		this.titleText.setText(title);
		return this;
	}

	/**
	 * 重新设置确定和取消按钮监听器
	 */
	public ZTSSmsSendDialog setButton(ZTSSmsSendDialogListener okListener, ZTSSmsSendDialogListener cancelListener) {
		this.okListener = okListener;
		this.cancelLisenter = cancelListener;
		return this;
	}

	/**
	 * 设置确定和取消按钮的文本和监听器
	 */
	public ZTSSmsSendDialog setButton(String oktext, String cancelText, ZTSSmsSendDialogListener okListener,
			ZTSSmsSendDialogListener cancelListener) {
		bottomLayout01.setVisibility(View.VISIBLE);
		if (oktext != null && !oktext.trim().equals("")) {
			okBtn.setText(oktext);
		}
		if (cancelText != null && !cancelText.trim().equals("")) {
			cancelBtn.setText(cancelText);
		}
		this.okListener = okListener;
		this.cancelLisenter = cancelListener;
		return this;
	}

	public ZTSSmsSendDialog setItems(ArrayList<ZtspeechSmsInfoModel> datas, OnItemClickListener listener) {
		listview.setVisibility(View.VISIBLE);
		this.datas = datas;
		if (listener != null) {
			listview.setOnItemClickListener(listener);
		}
		myAdapter.notifyDataSetChanged();
		return this;
	}

	public void notifyDataSetChanged() {
		myAdapter.notifyDataSetChanged();
	}

	private class MyAdpter extends BaseAdapter {
		private LayoutInflater layoutInflater = null;
		private OnCheckedChangeListener mOnCheckedChangeListener;

		public MyAdpter(Context context, OnCheckedChangeListener mOnCheckedChangeListener) {
			layoutInflater = LayoutInflater.from(context);
			this.mOnCheckedChangeListener = mOnCheckedChangeListener;
		}

		@Override
		public int getCount() {
			if (datas == null) {
				return 0;
			} else {
				return datas.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (datas == null) {
				return null;
			} else {
				return datas.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				v = layoutInflater.inflate(R.layout.sms_list_layout_item, null);
				viewHolder.smsName = (TextView) v.findViewById(R.id.smsname);
				viewHolder.smsNumber = (TextView) v.findViewById(R.id.smsnumber);
				viewHolder.checkBox = (ToggleButton) v.findViewById(R.id.checkbox);
				v.setTag(viewHolder);
			} else {
				v = convertView;
				viewHolder = (ViewHolder) v.getTag();
			}
			viewHolder.smsName.setText(datas.get(position).smsName);
			viewHolder.smsNumber.setText(datas.get(position).smsNumber);
			viewHolder.checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
			viewHolder.checkBox.setTag(position);

			if (datas.get(position).isChekced == true) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
			return v;
		}

		private final class ViewHolder {
			public TextView smsName;
			public TextView smsNumber;
			public ToggleButton checkBox;
		}
	}
}
