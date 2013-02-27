package com.ztspeech.unisay.trans.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.weibo.IRenren;

public class RenrenActivity extends Activity implements OnClickListener {

	private Button backBtn, btnSend;
	private TextView tvResult;
	private EditText edtContent;
	private IRenren iRenren;
	private String msg = "";
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.renrenweibo_share);
		msg = (String) this.getIntent().getExtras().get("msg");
		LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>>--msg:" + msg);
		btnSend = (Button) findViewById(R.id.rrsharebutton);
		btnSend.setOnClickListener(this);
		backBtn = (Button) findViewById(R.id.SharBack);
		backBtn.setOnClickListener(this);
		edtContent = (EditText) findViewById(R.id.rrsharecontent);
		edtContent.setText(msg);
		iRenren = IRenren.getInstance();
		iRenren.init(this);
	}

	public void back() {
		finish();
		overridePendingTransition(R.anim.slideinleft, R.anim.slideoutright);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnSend) {
			iRenren.sendWeibo(this, edtContent.getText().toString().trim(), handler);
		} else if (v == backBtn) {
			back();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		iRenren.saveStatus(requestCode, resultCode, data);
	}
}
