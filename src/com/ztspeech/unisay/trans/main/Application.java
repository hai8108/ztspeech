package com.ztspeech.unisay.trans.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDiskIOException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.dc.InitDC;
import com.ztspeech.unisay.trans.dc.MainDC;
import com.ztspeech.unisay.trans.engine.AppEngine;
import com.ztspeech.unisay.trans.engine.DBEngine;
import com.ztspeech.unisay.trans.manager.AboutManager;
import com.ztspeech.unisay.trans.manager.BaseManager;
import com.ztspeech.unisay.trans.manager.ZtsSpeechHistoryEditDetailsManager;
import com.ztspeech.unisay.trans.manager.ZtsSpeechVoiceHistoryManager;
import com.ztspeech.unisay.trans.manager.ZtsSpeechVoiceListManager;
import com.ztspeech.unisay.trans.manager.ZtsSpeechWriteWithVoiceManager;
import com.ztspeech.unisay.trans.manager.ZtspeechBinderCountManager;
import com.ztspeech.unisay.trans.manager.ZtspeechSettingManager;
import com.ztspeech.unisay.trans.model.ZtspeechSmsInfoModel;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;
import com.ztspeech.unisay.weibo.IRenren;
import com.ztspeech.unisay.weibo.ISina;
import com.ztspeech.unisay.weibo.ITencent;
import com.ztspeech.unisay.widget.ZTSDialog;
import com.ztspeech.unisay.widget.ZTSDialog.TlcyDialogListener;

public class Application extends Activity {
	public static int ScreenWidth;
	public static int ScreenHeight;
	public static MainDC mainDC;
	public static AppEngine appEngine;

	public static Boolean binding_flag = false;
	public static Boolean sinashare_flag = false;
	ZTSDialog exitDialog = null;
	AlertDialog imeiDialog = null;
	private long timeForAnimator;
	NotificationManager mNM;
	public static Application application;
	public static BaseManager currentMng = null;
	public static ZtsSpeechWriteWithVoiceManager mZtsSpeechWriteWithVoiceManager;
	public static ZtsSpeechVoiceHistoryManager mZtsSpeechVoiceHistoryManager;
	public static ZtsSpeechVoiceListManager mZtsSpeechVoiceListManager;
	public static AboutManager mAboutManager;
	public static ZtspeechSettingManager mZtspeechSettingManager;
	public static ZtspeechBinderCountManager mZtspeechBinderCountManager;
	public static ZtsSpeechHistoryEditDetailsManager mZtsSpeechHistoryEditDetailsManager;

	public static boolean isActivityShow = true;
	public static String toAlert = null;
	public static Builder toAlertBuider = null;
	public static int listNum = 1;
	public static boolean isDbListGroupChange = false;

	public static ITencent iTencent;
	public static ISina iSina;
	public static IRenren iRenren;
	public static ArrayList<ZtspeechSmsInfoModel> datas = new ArrayList<ZtspeechSmsInfoModel>();
	public final static String SMS_URI_ALL = "content://sms/"; // 0
	public final static String SMS_URI_INBOX = "content://sms/inbox";// 1
	public final static String SMS_URI_SEND = "content://sms/sent";// 2
	public final static String SMS_URI_DRAFT = "content://sms/draft";// 3
	public final static String SMS_URI_OUTBOX = "content://sms/outbox";// 4
	public final static String SMS_URI_FAILED = "content://sms/failed";// 5
	public final static String SMS_URI_QUEUED = "content://sms/queued";// 6
	public final static String CONTACT_URI = "content://com.android.contacts/contacts";

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mZtsSpeechWriteWithVoiceManager.showDC();
				break;
			case 1:
				// mZtsSpeechVoiceHistoryManager.showDC();
				mZtsSpeechVoiceListManager.showDC();
				break;
			case 2:
				mZtspeechBinderCountManager.showDC();
				break;
			case 3:
				mZtspeechSettingManager.showDC();
				break;
			case 4:
				mAboutManager.showDC();
				break;
			case 5:
				showExit();
				break;
			default:
				break;
			}
			System.gc();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogInfo.LogOut("TAG" + "onCreate");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		application = this;
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Display display = Application.this.getWindowManager().getDefaultDisplay();
		ScreenHeight = display.getHeight();
		ScreenWidth = display.getWidth();

		appEngine = new AppEngine(this);
		setContentView(appEngine.getDCEngine());
		appEngine.setInitDC(new InitDC(this));
		timeForAnimator = System.currentTimeMillis();
		mZtsSpeechWriteWithVoiceManager = new ZtsSpeechWriteWithVoiceManager();
		mZtsSpeechVoiceHistoryManager = new ZtsSpeechVoiceHistoryManager();
		mZtsSpeechVoiceListManager = new ZtsSpeechVoiceListManager();
		mAboutManager = new AboutManager();
		mZtspeechSettingManager = new ZtspeechSettingManager();
		mZtspeechBinderCountManager = new ZtspeechBinderCountManager();
		mZtsSpeechHistoryEditDetailsManager = new ZtsSpeechHistoryEditDetailsManager();
		iTencent = ITencent.getInstance(Application.this);
		iTencent.init();

		iSina = ISina.getInstance(this);
		iSina.init();

		iRenren = IRenren.getInstance();
		iRenren.init(this);
		getLastTenDaysContactsList();
		AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				try {
					File file = new File(Configs.ztsTalkPath);
					if (!file.isDirectory()) {
						file.delete();
						file.mkdir();
					}
					file = new File(Configs.ztspeechLrcPath);
					if (!file.isDirectory()) {
						file.delete();
						file.mkdir();
					}
					file = new File(Configs.ztsImageCache);
					if (!file.isDirectory()) {
						file.delete();
						file.mkdir();
					}
					file = new File(Configs.ztsRecordCache);
					if (!file.isDirectory()) {
						file.delete();
						file.mkdir();
					}
					file = new File(Configs.ztspeechMusicPath);
					if (!file.isDirectory()) {
						file.delete();
						file.mkdir();
					}
					DBEngine.create();
					DBEngine.close();

					long sleep = System.currentTimeMillis() - timeForAnimator;
					if (sleep < 2000) {
						Thread.sleep(2000 - sleep);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					mZtsSpeechWriteWithVoiceManager.initDC(Application.this);
					mZtsSpeechVoiceHistoryManager.initDC(Application.this);
					mZtsSpeechVoiceListManager.initDC(Application.this);
					mAboutManager.initDC(Application.this);
					mZtspeechSettingManager.initDC(Application.this);
					mZtspeechBinderCountManager.initDC(Application.this);
					mZtsSpeechHistoryEditDetailsManager.initDC(Application.this);
					mainDC = new MainDC(Application.this);
					mainDC.init(handler, ScreenWidth, ScreenHeight);
					appEngine.setMainDC(mainDC);
				} catch (SQLiteDiskIOException e) {
					new AlertDialog.Builder(Application.this).setMessage(R.string.sdcard_error)
							.setPositiveButton(R.string.OK, new OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									onDestroy();
								}
							}).create().show();
				}

			}
		};
		if (Utils.isSDCard() && Utils.isSDCardFree()) {
			task.execute();
		} else {
			if (!Utils.isSDCard()) {
				ProgressDialog progress = new ProgressDialog(Application.this);
				progress.setMessage(getText(R.string.noSDCARD));
				progress.setIndeterminate(true);
				progress.setCancelable(false);
				progress.setButton(getString(R.string.OK), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onDestroy();
					}
				});
				progress.show();
			} else {
				task.execute();
				new AlertDialog.Builder(this)
						.setMessage(getString(R.string.noSDCARDmem) + getString(R.string.noSDCARDmemConfirm))
						.setPositiveButton(R.string.OK, null).setNegativeButton(R.string.cancel, new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								onDestroy();
							}
						}).create().show();
			}
		}

	}

	private void getLastTenDaysContactsList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Map<String, ZtspeechSmsInfoModel> currContactsMap = new HashMap<String, ZtspeechSmsInfoModel>();
				String[] projection = new String[] { "_id", "address", "person", "date", "read", "type", "body" };
				long dateTime = 0;
				int countNum = 0;
				String numberStr = null;
				String nameChar = "";
				ZtspeechSmsInfoModel mZtspeechSmsInfoModel = null;
				Uri uri = Uri.parse(SMS_URI_ALL);
				Cursor cur = managedQuery(uri, projection, null, null, "date desc");
				if (cur.moveToFirst()) {
					do {
						numberStr = cur.getString(cur.getColumnIndex("address"));
						if (numberStr != null && numberStr.trim().length() >= 11) {
							numberStr = numberStr.substring(numberStr.length() - 11);
							if (numberStr.startsWith("1")) {
								Cursor cursor = getContentResolver().query(
										Uri.parse("content://com.android.contacts/data/phones/filter/" + numberStr),
										new String[] { "display_name" }, null, null, null);
								if (cursor.moveToFirst()) {
									nameChar = cursor.getString(cursor.getColumnIndex("display_name"));
								} else {
									nameChar = "联系人未知";
								}
								cursor.close();

								dateTime = cur.getLong(cur.getColumnIndex("date"));
								Date date = new Date();

								mZtspeechSmsInfoModel = new ZtspeechSmsInfoModel(nameChar, numberStr, dateTime);
								if (currContactsMap.containsKey(numberStr)) {
									if (currContactsMap.get(numberStr).date > dateTime) {
										currContactsMap.put(numberStr, mZtspeechSmsInfoModel);
									}
								} else {
									countNum = (int) ((date.getTime() - dateTime) / 1000 / 60 / 60 / 24);
									if (countNum <= 10) {
										currContactsMap.put(numberStr, mZtspeechSmsInfoModel);
										datas.add(mZtspeechSmsInfoModel);
									}
								}
							}
						}
					} while (cur.moveToNext());
				}
				currContactsMap.clear();
			}
		}).start();
	}

	@Override
	public void onStart() {
		super.onStart();
		LogInfo.LogOut("TAG" + "onStart");

	}

	@Override
	public void onResume() {
		super.onResume();
		isActivityShow = true;
		if (toAlert != null) {
			new AlertDialog.Builder(this).setTitle(getString(R.string.userTip)).setMessage(toAlert)
					.setNegativeButton(getString(R.string.OK), null).create().show();
			toAlert = null;
		}
		if (toAlertBuider != null) {
			toAlertBuider.show();
			toAlertBuider = null;
		}
		LogInfo.LogOut("TAG" + "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		isActivityShow = false;
		LogInfo.LogOut("TAG" + "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogInfo.LogOut("TAG" + "onStop");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		LogInfo.LogOut("TAG" + "onReStart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DBEngine.close();
		System.exit(0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return super.onKeyDown(keyCode, event);
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			return super.onKeyDown(keyCode, event);
		} else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_BACKSLASH) {
			if (appEngine.isShowDC(mZtsSpeechHistoryEditDetailsManager.mainDC)) {
				mZtsSpeechHistoryEditDetailsManager.back();
				return true;
			}
			if (!appEngine.back()) {
				showExit();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_CALL) {
			new AlertDialog.Builder(this).setPositiveButton(R.string.OK, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onDestroy();
				}
			}).create().show();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				|| keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
				|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			Log.e("NewBook", "down : " + keyCode);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN
				|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
				|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
			// Log.e("dispatchKeyEvent", "code : " + event.getKeyCode());
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		return true;
	}

	/**
	 */
	void showExit() {
		if (exitDialog == null) {
			exitDialog = new ZTSDialog(this).setTitle(getString(R.string.userTip))
					.setMessage(getResources().getString(R.string.exitTip))
					.setButton(getString(R.string.ok), getString(R.string.cancel), new TlcyDialogListener() {

						@Override
						public void onClick() {
							// TODO Auto-generated method stub
							onDestroy();
						}
					}, null);
		}
		if (!exitDialog.isShowing()) {
			exitDialog.show();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		this.finish();
		this.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		iRenren.saveStatus(requestCode, resultCode, data);
	}

	/**
	 * 长按菜单响应函数
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// 关键代码
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>>>>>>--onContextItemSelected");
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {// 上面的type设定这里类型的判定！这里是child判定！
			int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			// 在child判定里面，获取该child所属group！
			int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
			// 在child判定里面，获取该child所属position！
			LogInfo.LogOut("haitian", "item.getItemId() =" + item.getItemId());
			switch (item.getItemId()) {
			case 0:
				mZtsSpeechVoiceListManager.longPressDeleteSelectItem(groupPos, childPos);
				break;
			default:
				break;
			}
			return true;
		}
		return false;
	}
}
